package com.example.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private TextView tvTime;
    private Button btnStart, btnStop, btnReset, btnrotate;
    private Handler handler;
    private long startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    private boolean isRunning = false;
    private Runnable updateTimerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTime = findViewById(R.id.tvTime);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);
        btnrotate = findViewById(R.id.btnrotate);

        handler = new Handler();


        updateTimerThread = new Runnable() {
            public void run() {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updateTime = timeSwapBuff + timeInMilliseconds;
                updateDisplay();
                handler.postDelayed(this, 1000); // update every 1 second
            }
        };


        if (savedInstanceState != null) {
            startTime = savedInstanceState.getLong("startTime");
            timeInMilliseconds = savedInstanceState.getLong("timeInMilliseconds");
            timeSwapBuff = savedInstanceState.getLong("timeSwapBuff");
            updateTime = savedInstanceState.getLong("updateTime");
            isRunning = savedInstanceState.getBoolean("isRunning");
            updateDisplay();
            if (isRunning) {
                startTime = SystemClock.uptimeMillis() - timeInMilliseconds;
                handler.post(updateTimerThread);
            }
            updateButtonStates(!isRunning, true, isRunning, !isRunning);
        } else {

            updateButtonStates(true, true, false, false);
        }


        btnStart.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = SystemClock.uptimeMillis();
                handler.post(updateTimerThread);
                isRunning = true;
                updateButtonStates(false, true, true, true);
            }
        });


        btnStop.setOnClickListener(v -> {
            if (isRunning) {
                timeSwapBuff += timeInMilliseconds;
                handler.removeCallbacks(updateTimerThread);
                isRunning = false;
                updateButtonStates(true, true, false, true);
            }
        });

        btnReset.setOnClickListener(v -> {
            startTime = timeSwapBuff = timeInMilliseconds = updateTime = 0L;
            updateDisplay();
            handler.removeCallbacks(updateTimerThread);
            isRunning = false;
            updateButtonStates(true, true, false, false);
        });


        btnrotate.setOnClickListener(v -> rotateDevice(v));
    }


    private void updateDisplay() {
        int secs = (int) (updateTime / 1000);
        int mins = secs / 60;
        secs %= 60;
        tvTime.setText(String.format("%02d:%02d", mins, secs));
    }


    private void updateButtonStates(boolean start, boolean rotate, boolean stop, boolean reset) {
        btnStart.setEnabled(start);
        btnrotate.setEnabled(rotate);
        btnStop.setEnabled(stop);
        btnReset.setEnabled(reset);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("startTime", startTime);
        outState.putLong("timeInMilliseconds", timeInMilliseconds);
        outState.putLong("timeSwapBuff", timeSwapBuff);
        outState.putLong("updateTime", updateTime);
        outState.putBoolean("isRunning", isRunning);
    }


    public void rotateDevice(View view) {
        Log.d("MainActivity", "rotateDevice called");
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
