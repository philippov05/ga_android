package com.hackyourself.guitair.back;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MotionController {
    private String LOG_TAG = "Sensorss";

    private Context context;

    private IObserver listener;

    private SensorManager mSensorManager;
    private Sensor mGyroscopeSensor;

    private Vibrator mVibrator;

    public MotionController(Context context){
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor: sensorList)
            Log.d("SENSORSSSS", sensor.getName());
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void start(){
        if(mSensorManager.registerListener(mSensorListener, mGyroscopeSensor,SensorManager.SENSOR_DELAY_GAME))
            Log.d(LOG_TAG, "RegisterLIstener");
        else
            Log.d(LOG_TAG, "Fail Register");

    }

    public void stop(){
        mSensorManager.unregisterListener(mSensorListener);
    }

    public void registerObserver(IObserver observer){
        this.listener = observer;
    }

    public void unregisterObserver(){
        this.listener = null;
    }

    private ArrayList<Data> mEventsStack = new ArrayList<Data>();
    private long mTimestamp;
    private int mCurrentMovement;

    private void clearStack(){
        mEventsStack.clear();
    }

    private boolean isValidSequence(){
        return (mEventsStack.size() > 2);
    }

    private Data getEventFromStack(){
        return mEventsStack.get(2);
    }

    private void addEventToStack(Data event){
        mEventsStack.add(event);
    }

    private void notifyListener(Data event){
        Log.d(LOG_TAG, "I'm notifying with " + event.getTime() + " " + event.getMovement());
        listener.update(event);
        // Vibrate for 500 milliseconds
        mVibrator.vibrate(100);
    }

    private void handleEvent(Data event){
        if (event.getTime() - mTimestamp < 120000000){
            if (event.getMovement() != 0){
                if (event.getMovement() == mCurrentMovement){
                    addEventToStack(event);
                    //Log.d(LOG_TAG, event.getmMovement().toString());
                } else {
                    if (isValidSequence()){
                        notifyListener(getEventFromStack());
                    }
                    clearStack();
                    mCurrentMovement = event.getMovement();
                    mTimestamp = event.getTime();
                }
            }
        } else {
            if (isValidSequence()){
                notifyListener(getEventFromStack());
            }
            clearStack();
            mCurrentMovement = event.getMovement();
            mTimestamp = event.getTime();
        }
    }

    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (Math.abs(event.values[0]) > 3 && Math.abs(event.values[1]) > 3){
                if (event.values[0] > 0 && event.values[1] > 0) {
                    handleEvent(new Data(event.timestamp,Movement.UP));
                    Log.d(LOG_TAG, "GYRO: I go UP!" + Long.toString(event.timestamp));
                } else if (event.values[0] < -3 && event.values[1] < -3) {
                    handleEvent(new Data(event.timestamp,Movement.DOWN));
                    Log.d(LOG_TAG, "GYRO: I go DOWN!" + Long.toString(event.timestamp));
                }
            } else {
                handleEvent(new Data(event.timestamp, 0));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
