package com.ratiug.dev.service_and_receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DBG | Main Activity | ";
    ///
    private ProgressBar mProgressBar;
    private TextView mProgressValue;
    private Button testtest;
    ///
    public static final String BROADCAST_PROGRESS_ACTION = "com.ratiug.dev.service_and_receiver_progress_update";
    ProgressService progressService;
    Boolean bound = false;
    Intent intent;
    ServiceConnection sConn;
    BroadcastReceiver receiver;
   public int mMaxValue = 5000;
    int prgs;

    ///

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.pb_progressBar);
        mProgressValue = findViewById(R.id.tv_progress);
        Button mMinusFivePercent = findViewById(R.id.btn_5prc);
        testtest = findViewById(R.id.btn_5prc22);
        mProgressBar.setMax(mMaxValue);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive" + progressService.getmProgressValue());
                prgs = progressService.getmProgressValue();
                mProgressBar.setProgress(prgs);
                double tmp = (double) prgs / mMaxValue;
                mProgressValue.setText((int) (tmp * 100) + "%");
                if(prgs == progressService.mMaxValue) Toast.makeText(getApplicationContext(),"Loading completed:)",Toast.LENGTH_SHORT).show();
            }
        };

        IntentFilter filter = new IntentFilter(BROADCAST_PROGRESS_ACTION);
        registerReceiver(receiver,filter);

      intent = new Intent(this,ProgressService.class).putExtra("maxValue",mMaxValue);
       // Log.d(TAG, "onCreate: Max Value = " + mMaxValue);

         sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                progressService = ((ProgressService.MyBinder)iBinder).getService();
                bound = true;
                Log.d(TAG, "onServiceConnected: +");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                bound = false;
                Log.d(TAG, "onServiceDisconnected: -");
            }

        };



        mMinusFivePercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressService.minusPercent();
            }
        });

        testtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindService(intent,sConn,0);
                startService(intent);
            }
        });


    }
}