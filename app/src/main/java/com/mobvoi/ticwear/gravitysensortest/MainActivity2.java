package com.mobvoi.ticwear.gravitysensortest;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity2 extends Activity implements SensorEventListener {
    public static final String EXTRA_SENSOR_TYPE="sensorType";
    private static final String TAG="MainActivity2";
    private TextView mTextView;
    private Button btn;
    private Handler handler;
    private PowerManager powerManager;
    private SensorManager sensorManager;
    private boolean started;
    private Sensor sensor;
    private PowerManager.WakeLock wakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int sensorType=getIntent().getIntExtra(EXTRA_SENSOR_TYPE, Sensor.TYPE_ACCELEROMETER);
        setContentView(R.layout.activity2);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        handler=new Handler();
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(sensorType);
        if(sensor==null){
            Toast.makeText(this, "没有重力传感器", Toast.LENGTH_SHORT);
            finish();
        }
        Log.d(TAG , sensor.toString());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                btn= (Button) stub.findViewById(R.id.btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(btn.getText().equals("开始")){
                            startSensor();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    btn.setText("停止");
                                }
                            });
                        }else{
                            stopSensor();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    btn.setText("开始");
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        this.stopSensor();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.stopSensor();
    }

    private synchronized void startSensor(){
        if(started) return;
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "GestureRecorder");
        wakeLock.acquire();
        started=true;
    }

    private synchronized void stopSensor(){
        if(!started) return;
        sensorManager.unregisterListener(this);
        wakeLock.release();
        started=false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Log.d(TAG, "event: "+event.values[0]);

        StringBuilder sb=new StringBuilder();
        for(int i=0;i<event.values.length;i++){
            sb.append(event.values[i]+",");
        }
        final String data=sb.substring(0, sb.length()-1);
        handler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(data);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
