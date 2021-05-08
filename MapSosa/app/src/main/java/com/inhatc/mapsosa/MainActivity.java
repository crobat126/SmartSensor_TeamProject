package com.inhatc.mapsosa;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager objSMG;                   // Object SensorManager

    Sensor sensor_Accelerometer;            // Object Sensor Accelerometer

    TextView objTV_X_Accelerometer;         // Accelerometer X Variable
    TextView objTV_Y_Accelerometer;         // Accelerometer Y Variable
    TextView objTV_Z_Accelerometer;         // Accelerometer Z Variable
    TextView objTV_Total_Accelerometer;     // Total Accelerometer Variable

    TextView txt_State;         // State Variable (상태 표시 : 정상, 경고, 위험)

    float total_Accelerometer;


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
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                objTV_X_Accelerometer.setText(("X : " + sensorEvent.values[0]));
                objTV_Y_Accelerometer.setText(("Y : " + sensorEvent.values[1]));
                objTV_Z_Accelerometer.setText(("Z : " + sensorEvent.values[2]));
                total_Accelerometer = (float)sensorEvent.values[0] + (float)sensorEvent.values[1] + (float)sensorEvent.values[2];
                objTV_Total_Accelerometer.setText(("Total : " + total_Accelerometer));

                // 테스트용 주석
                if (sensorEvent.values[1] < 3){
                    txt_State.setText("위험");
                }
                else if (sensorEvent.values[1] < 6) {
                    txt_State.setText("경고");
                }
                else {
                    txt_State.setText("정상");
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Call when sensor accuracy changed
    }
}