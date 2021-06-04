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

    Button main_btnKakao;                       // Button object
    Button main_btnRegister;                    // Button object

    private SessionCallback sessionCallback = new SessionCallback();
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        main_btnKakao = (Button) findViewById(R.id.main_btnKakao);
        main_btnRegister = (Button) findViewById(R.id.main_btnRegister);

        main_btnKakao.setOnClickListener(this);
        main_btnRegister.setOnClickListener(this);

        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_btnKakao:
                session.open(AuthType.KAKAO_LOGIN_ALL, homeActivity.this);
                break;

            case R.id.main_btnRegister:
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