package com.inhatc.mapsosa;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Firebase extends AppCompatActivity implements View.OnClickListener {
    FirebaseDatabase myFirebase;                // Firebase object
    DatabaseReference myDB_Reference = null;    // Firebase DB reference

    // HashMap<String, Object> childNode = null;
    HashMap<String, Object> user = null;

    Button btnId;                     // 아이디 중복확인 버튼
    Button btnPhone;                  // 휴대폰인증 버튼
    Button btnRegister;               // 회원가입 버튼

    EditText edtId;                   // 아이디 EditText
    EditText edtPwd;                  // 비밀번호 EditText
    EditText edtPhone;                // 휴대폰번호 EditText

    String strHeader = "USER";        // Firebase Key
    String strId = null;
    String strPwd = null;
    String strPhone = null;

    Integer flagId = 0;               // 0 : 중복확인 전, 1 : 중복, 2 : 중복확인 완료

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        edtId = (EditText) findViewById(R.id.edtId);
        edtPwd = (EditText) findViewById(R.id.edtPwd);
        edtPhone = (EditText) findViewById(R.id.edtPhone);

        btnId = (Button) findViewById(R.id.btnId);
        btnPhone = (Button) findViewById(R.id.btnPhone);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnId.setOnClickListener(this);
        btnPhone.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        myFirebase = FirebaseDatabase.getInstance();    // Get FirebaseDatabase instance
        myDB_Reference = myFirebase.getReference();     // Get Firebase reference

        user = new HashMap<>();               // Create HashMap
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
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }


    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme); // 알림창 builder
        strId = edtId.getText().toString();
        strPwd = edtPwd.getText().toString();
        strPhone = edtPhone.getText().toString();
        switch (v.getId()){
            case R.id.btnId:
                if (!strId.equals("")){
                    mGet_FirebaseDatabase();
                    break;
                }

            case R.id.btnPhone:
                break;

            case R.id.btnRegister:
                // edtId 변화 감지하여 아이디 중복체크하게 하기
                edtId.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        flagId = 0;
                    }
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        //
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        //
                    }
                });

                // 아이디 미입력시
                if (strId.equals("")) {
                    builder.setTitle("오류").setMessage("아이디를 입력해주세요.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else if (flagId == 0 || flagId == 1) {
                    builder.setTitle("오류").setMessage("아이디 중복 확인을 해주세요.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                // 비밀번호 미입력시
                else if (strPwd.equals("")) {
                    builder.setTitle("오류").setMessage("비밀번호를 입력해주세요.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                // 휴대폰번호 미입력시
                else if (strPhone.equals("")) {
                    builder.setTitle("오류").setMessage("휴대폰번호를 입력해주세요.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                else {
                    user.put("ID", strId);
                    user.put("Password", strPwd);
                    user.put("Phone Number", strPhone);
                    mSet_FirebaseDatabase(true);    // App -> Firebase DB

                    // 알림창 띄우기
                    builder.setTitle("회원가입 완료").setMessage("성공적으로 회원가입을 완료하였습니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            // 메인화면으로 돌아가기
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    homeActivity.class); // 다음 넘어갈 클래스 지정
                            startActivity(intent);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
                }

            default:
                break;
        }
    }

    // Data : App -> Firebase DB
    public void mSet_FirebaseDatabase(boolean bFlag) {
        // bFlag = true(add)/false(delete)
        if (bFlag){
            myDB_Reference.child(strHeader).child(strId).setValue(user);
        }
    }

    // Data : Firebase DB -> App
    public void mGet_FirebaseDatabase() {
        flagId = 2;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme); // 알림창 builder
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String strKey = postSnapshot.getKey();
                    if (strKey.equals(edtId.getText().toString())) {
                        flagId = 1;
                    }
                }

                if (flagId == 1) {
                    builder.setTitle("오류").setMessage("중복된 아이디 입니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else if (flagId == 2) {
                    builder.setTitle("완료").setMessage("사용 가능한 아이디 입니다.");
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
                .child(strHeader).orderByChild(strId);
        sortbyName.addListenerForSingleValueEvent(postListener);
    }
}
