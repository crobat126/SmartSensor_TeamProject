package com.inhatc.mapsosa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.kakao.auth.AuthType;
import com.kakao.auth.Session;

public class homeActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnKakao;                           // Button object
    Button btnRegister;                           // Button object
    private SessionCallback sessionCallback = new SessionCallback();
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnKakao = (ImageButton) findViewById(R.id.btnKakao);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnKakao.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnKakao:
                session.open(AuthType.KAKAO_LOGIN_ALL, homeActivity.this);


                break;
            case R.id.btnRegister:
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        Firebase.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); //
                break;
            default:
                break;
        }
    }



}