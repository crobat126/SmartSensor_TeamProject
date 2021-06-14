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
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.Session;
import com.kakao.usermgmt.response.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class editPhone extends AppCompatActivity implements View.OnClickListener {
    FirebaseDatabase myFirebase;                // Firebase object
    DatabaseReference myDB_Reference = null;    // Firebase DB reference

    // HashMap<String, Object> childNode = null;
    HashMap<String, Object> user = null;

    TextView phone_txtPhone;
    TextView phone_txtPhone2;

    Button phone_btnPhone;            // 휴대폰인증 버튼
    Button phone_btnPhone2;           // 보호자 휴대폰인증 버튼
    Button btnEdit;                   // 번호변경 버튼

    EditText phone_edtPhone;          // 휴대폰번호 EditText
    EditText phone_edtPhone2;         // 보호자 휴대폰번호 EditText

    String exist_PhoneNum;            // 기존 휴대폰번호
    String exist_PhoneNum2;           // 기존 보호자 휴대폰번호

    String strHeader = "USER";        // Firebase Key
    String phone_strId = null;
    String phone_strPwd = null;
    String phone_strPhone = null;
    String phone_strPhone2 = null;

    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editphone);

        exist_PhoneNum = ((UserMain)UserMain.context_user).login_strPhone;
        exist_PhoneNum2 = ((UserMain)UserMain.context_user).login_strPhone2;
        phone_txtPhone = (TextView) findViewById(R.id.phone_txtPhone);
        phone_txtPhone2 = (TextView) findViewById(R.id.phone_txtPhone2);
        phone_txtPhone.setText("휴대폰번호 : " + exist_PhoneNum);
        phone_txtPhone2.setText("보호자 휴대폰번호 : " + exist_PhoneNum2);

        phone_strId = ((UserMain)UserMain.context_user).login_strId;
        phone_strPwd = ((UserMain)UserMain.context_user).login_strPwd;

        phone_edtPhone = (EditText) findViewById(R.id.phone_edtPhone);
        phone_edtPhone2 = (EditText) findViewById(R.id.phone_edtPhone2);

        phone_btnPhone = (Button) findViewById(R.id.phone_btnPhone);
        phone_btnPhone2 = (Button) findViewById(R.id.phone_btnPhone2);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        phone_btnPhone.setOnClickListener(this);
        phone_btnPhone2.setOnClickListener(this);
        btnEdit.setOnClickListener(this);

        session = Session.getCurrentSession();

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
        phone_strPhone = phone_edtPhone.getText().toString();
        phone_strPhone2 = phone_edtPhone2.getText().toString();
        switch (v.getId()){
            case R.id.phone_btnPhone:
                // 휴대폰인증 로직
                break;

            case R.id.phone_btnPhone2:
                // 휴대폰인증 로직
                break;

            case R.id.btnEdit:
                // 휴대폰번호 미입력시
                if (phone_strPhone.equals("")) {
                    builder.setTitle("오류").setMessage("변경할 휴대폰번호를 입력해주세요.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                else if (phone_strPhone2.equals("")) {
                    builder.setTitle("오류").setMessage("변경할 보호자 휴대폰번호를 입력해주세요.");
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
                    user.put("ID", phone_strId);
                    user.put("Password", phone_strPwd);
                    user.put("Phone Number", phone_strPhone);
                    user.put("Phone Number 2", phone_strPhone2);
                    mSet_FirebaseDatabase(true);    // App -> Firebase DB

                    // 알림창 띄우기
                    builder.setTitle("번호변경 완료").setMessage("성공적으로 번호를 변경하였습니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            // 낙상감지 화면으로 돌아가기
                            Intent intent = new Intent(
                                    getApplicationContext(), // 현재 화면의 제어권자
                                    UserMain.class); // 다음 넘어갈 클래스 지정
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
            myDB_Reference.child(strHeader).child(phone_strId).setValue(user);
        }
    }
}
