package com.inhatc.mapsosa;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager objSMG;                   // Object SensorManager

    Sensor sensor_Accelerometer;            // Object Sensor Accelerometer

    TextView objTV_X_Accelerometer;         // Accelerometer X Variable
    TextView objTV_Y_Accelerometer;         // Accelerometer Y Variable
    TextView objTV_Z_Accelerometer;         // Accelerometer Z Variable
    TextView objTV_Total_Accelerometer;     // Total Accelerometer Variable

    TextView txt_State;         // State Variable (상태 표시 : 정상, 경고, 위험)

    double total_Accelerometer;

    TextView test1;
    TextView test2;
    TextView test3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Object for access sensor device
        objSMG = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensor_Accelerometer = objSMG.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        objTV_X_Accelerometer = (TextView) findViewById(R.id.txtX_Accelerometer);
        objTV_Y_Accelerometer = (TextView) findViewById(R.id.txtY_Accelerometer);
        objTV_Z_Accelerometer = (TextView) findViewById(R.id.txtZ_Accelerometer);
        objTV_Total_Accelerometer = (TextView) findViewById(R.id.txtTotal_Accelerometer);

        txt_State = (TextView) findViewById(R.id.txt_State);

        test1 = (TextView) findViewById(R.id.test1);
        test2 = (TextView) findViewById(R.id.test2);
        test3 = (TextView) findViewById(R.id.test3);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register listener for changing sensor value
        objSMG.registerListener(this, sensor_Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Release sensor listener
        objSMG.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double loX = sensorEvent.values[0];     // X축 값
            double loY = sensorEvent.values[1];     // Y축 값
            double loZ = sensorEvent.values[2];     // Z축 값

            objTV_X_Accelerometer.setText(("X : " + loX));
            objTV_Y_Accelerometer.setText(("Y : " + loY));
            objTV_Z_Accelerometer.setText(("Z : " + loZ));
            total_Accelerometer = loX + loY + loZ;
            objTV_Total_Accelerometer.setText(("Total : " + total_Accelerometer));

            double loAccelerationReader = Math.sqrt(Math.pow(loX, 2)
                    + Math.pow(loY, 2)
                    + Math.pow(loZ, 2));

            test1.setText("loAccelerationReader : " + loAccelerationReader);

            DecimalFormat precision = new DecimalFormat("0.00");
            double ldAccRound = Double.parseDouble(precision.format(loAccelerationReader));

            test2.setText("ldAccRound : " + ldAccRound);

            if (ldAccRound > 0.3d && ldAccRound < 0.5d) {
                txt_State.setText("호우!!");
            } else {
                txt_State.setText("테스트중");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Call when sensor accuracy changed
    }
}