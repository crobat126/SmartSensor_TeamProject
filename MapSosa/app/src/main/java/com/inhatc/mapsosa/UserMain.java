package com.inhatc.mapsosa;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.Session;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserMain extends AppCompatActivity implements SensorEventListener {
    SensorManager objSMG;

    Sensor sensor_Accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        //Object for access sensor device
        objSMG = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor_Accelerometer = objSMG.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    public void onResume() {
        super.onResume();

        //Register Listener for changing sensor value
        objSMG.registerListener(this, sensor_Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        objSMG.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                int xValue = (int) sensorEvent.values[0];
                int yValue = (int) sensorEvent.values[1];
                int zValue = (int) sensorEvent.values[2];

                if (xValue >= 25 || yValue >= 25 || zValue >= 25){
                    showDialog();
                }
                break;
        }
    }

    void showDialog() {
//        AlertDialog.Builder msgBulder = new AlertDialog.Builder(MainActivity.this)
//                .setTitle("낙상이 감지되었습니다!")
//                .setMessage("위험한 상황입니까?")
//                .setPositiveButton("아니오", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i){
//                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
//                        startActivity(intent);
//                    }
//                })
//                .setNegativeButton("예", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i){
//                        Toast.makeText(MainActivity.this, "안 끔", Toast.LENGTH_SHORT).show();
//                    }
//                });
//        AlertDialog msgDlg = msgBulder.create();
//        msgDlg.show();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("낙상이 감지되었습니다!")
                .setMessage("위험한 상황입니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(UserMain.this, MapActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("아니오", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private static final int AUTO_DISMISS_MILLIS = 10000;
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                final CharSequence negativeButtonText = defaultButton.getText();
                new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        defaultButton.setText(String.format(
                                Locale.getDefault(), "%s (%d)",
                                negativeButtonText,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                        ));
                    }
                    @Override
                    public void onFinish() {
                        if (((AlertDialog) dialog).isShowing()) {
                            dialog.dismiss();
                            Intent intent = new Intent(UserMain.this, MapActivity.class);
                            startActivity(intent);
                        }
                    }
                }.start();
            }
        });
        dialog.show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Call when sensor accuracy changed
    }
}
