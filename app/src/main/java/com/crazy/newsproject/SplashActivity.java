package com.crazy.newsproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        Thread splashthread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
//                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    Intent intent  = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        splashthread.start();
    }
}