package com.ratiug.dev.service_and_receiver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class ProgressService extends Service {

    private static final String TAG = "DBG | ProgressService";
    MyBinder binder = new MyBinder();
    Timer timer;
    TimerTask timerTask;
    int mMaxValue ;
    int mProgressValue ;
    int mTempValue;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service Created");
        timer = new Timer();
    }

    void startTask(){

        if (timerTask!= null) timerTask.cancel();

        if (mProgressValue < mMaxValue){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.BROADCAST_PROGRESS_ACTION);
                    mTempValue = mMaxValue * 5 / 100;

                    if (mProgressValue + mTempValue > mMaxValue){
                        mProgressValue = mMaxValue;

                    }
                    else {
                        mProgressValue += mTempValue;
                    }
                    Log.d(TAG, "run: Progress = " + mProgressValue);
                    sendBroadcast(intent);
                    if (mProgressValue == mMaxValue) {
                        Log.d(TAG, "startTask: Max Value!!!!" + Thread.currentThread().getName());
                        timerTask.cancel();
                    }
                }
            };
            timer.schedule(timerTask,1000,1000);
        }
    }

    void minusPercent() {
        Intent intent = new Intent(MainActivity.BROADCAST_PROGRESS_ACTION);
        mTempValue = mMaxValue * 50 / 100;
        {
            if (mProgressValue - mTempValue < 0) {
                mProgressValue = 0;
            } else {
                mProgressValue = (int) (mProgressValue - mTempValue);
            }
            sendBroadcast(intent);
            Log.d(TAG, "splitPercent: Progress " + mProgressValue);
            startTask();
        }
    }

    public int getmProgressValue(){
        return mProgressValue;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Service Bounded");
        mMaxValue = intent.getIntExtra("maxValue",0);
        startTask();
        return binder;
    }

    class MyBinder extends Binder{
        ProgressService getService() {

            return ProgressService.this;
        }
    }
}
