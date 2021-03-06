package com.inhatc.mapsosa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class homeActivity extends AppCompatActivity implements View.OnClickListener {

    public static Context context_main; // context 변수 선언
    public static String KakaoId; // KakaoId 변수 선언
    FirebaseDatabase myFirebase;                // Firebase object
    DatabaseReference myDB_Reference = null;    // Firebase DB reference

    // HashMap<String, Object> childNode = null;
    HashMap<String, Object> user = null;

    Button main_btnKakao;                       // Button object
    Button main_btnLogin;                       // Button object
    Button main_btnRegister;                    // Button object

    EditText main_edtId;                        // EditText object
    EditText main_edtPwd;                       // EditText object

    String strHeader = "USER";                  // Firebase Key
    String main_strId = null;
    String main_strPwd = null;

    String ID = null;
    Integer flagLogin = 0;                      // 0 : 로그인 전, 1 : 로그인 성공, 2 : 로그인 실패

    private SessionCallback sessionCallback = new SessionCallback();
    Session session;

    int btnFlag = 0;  // 0 : 버튼클릭 X, 1 : 회원 로그인, 2 : 카카오 로그인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context_main = this;

        main_btnKakao = (Button) findViewById(R.id.main_btnKakao);
        main_btnLogin = (Button) findViewById(R.id.main_btnLogin);
        main_btnRegister = (Button) findViewById(R.id.main_btnRegister);

        main_btnKakao.setOnClickListener(this);
        main_btnLogin.setOnClickListener(this);
        main_btnRegister.setOnClickListener(this);

        main_edtId = (EditText) findViewById(R.id.main_edtId);
        main_edtPwd = (EditText) findViewById(R.id.main_edtPwd);

        session = Session.getCurrentSession();
        //session.addCallback(sessionCallback);

        myFirebase = FirebaseDatabase.getInstance();    // Get FirebaseDatabase instance
        myDB_Reference = myFirebase.getReference();     // Get Firebase reference

        user = new HashMap<>();               // Create HashMap
    }

    public void seetter(String newId){
        this.KakaoId = newId;
    }

    private void getHashKey() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash123", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    @Override
    public void onClick(View v) {
        main_strId = main_edtId.getText().toString();
        main_strPwd = main_edtPwd.getText().toString();
        switch (v.getId()){
            case R.id.main_btnLogin:
                btnFlag = 1;
                mGet_FirebaseDatabase();
                break;

            case R.id.main_btnRegister:
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        Firebase.class); // 다음 넘어갈 클래스 지정
                startActivity(intent);
                break;

            case R.id.main_btnKakao:
                btnFlag = 2;
                loginKaKao();
                break;

            default:
                break;
        }
    }


    private void loginKaKao(){
        Log.e("SessionCallback :: ", "시작");
        session.open(AuthType.KAKAO_LOGIN_ALL, homeActivity.this);
        Log.e("SessionCallback :: ", "Session: " + session.toString());
        Log.e("SessionCallback :: ", "Session getAccessToken: " + session.getAccessToken());
        Log.e("SessionCallback :: ", "Session getTokenInfo: " + session.getTokenInfo());

        Log.e("SessionCallback :: ", "Session session.isOpenable(): " + session.isOpenable());

        Log.e("SessionCallback :: ", "Session session.getAccessTokenCallback(): " + session.getAccessTokenCallback());


        UserManagement.getInstance()
                .me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        Log.i("KAKAO_API", "사용자 아이디: " + result.getId());
                        homeActivity.KakaoId = String.valueOf(result.getId());

                        UserAccount kakaoAccount = result.getKakaoAccount();
                        if (kakaoAccount != null) {

                            // 이메일
                            String email = kakaoAccount.getEmail();
                            homeActivity.KakaoId = email;

                            if (email != null) {
                                Log.i("KAKAO_API", "email: " + email);

                            } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                                // 동의 요청 후 이메일 획득 가능
                                // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.

                            } else {
                                // 이메일 획득 불가
                            }

                            // 프로필
                            Profile profile = kakaoAccount.getProfile();

                            if (profile != null) {
                                Log.d("KAKAO_API", "nickname: " + profile.getNickname());
                                Log.d("KAKAO_API", "profile image: " + profile.getProfileImageUrl());
                                Log.d("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());

                                homeActivity.KakaoId = profile.getNickname();

                            } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                // 동의 요청 후 프로필 정보 획득 가능

                            } else {
                                // 프로필 획득 불가
                            }
                            Log.i("KAKAO_API", " 로그인 성공 화면 이동");
                            // 로그인 성공 화면 이동
                            Intent intent2 = new Intent(
                                    homeActivity.this, // 현재 화면의 제어권자
                                    UserMain.class); // 다음 넘어갈 클래스 지정
                            startActivity(intent2);

                        }
                    }

                });



    }

    // Data : Firebase DB -> App
    public void mGet_FirebaseDatabase() {
        flagLogin = 2;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme); // 알림창 builder
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String strKey = postSnapshot.getKey();
                    String password = postSnapshot.child("Password").getValue().toString();
                    if (strKey.equals(main_edtId.getText().toString())
                            && password.equals(main_edtPwd.getText().toString())) {
                        ID = strKey;
                        flagLogin = 1;
                    }
                }

                if (flagLogin == 1) {
                    builder.setTitle("로그인 성공").setMessage(ID + "님 환영합니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(
                                    homeActivity.this, // 현재 화면의 제어권자
                                    UserMain.class); // 다음 넘어갈 클래스 지정
                            startActivity(intent);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else if (flagLogin == 2) {
                    builder.setTitle("오류").setMessage("아이디와 비밀번호를 확인해주세요.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError dbError) {
                // Failed to read value
                Log.w("TAG : ", "Failed to read value.", dbError.toException());
            }
        };
        Query sortbyName = FirebaseDatabase.getInstance().getReference()
                .child(strHeader).orderByChild(main_strId);
        sortbyName.addListenerForSingleValueEvent(postListener);
    }

}