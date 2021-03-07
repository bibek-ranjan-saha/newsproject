package com.crazy.newsproject;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class appintro extends AppIntro {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make sure you don't call setContentView!

        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        addSlide(AppIntroFragment.newInstance("Welcome to news app","Database",
                R.drawable.first1, ContextCompat.getColor(getApplicationContext(),R.color.firstcolor)));
        addSlide(AppIntroFragment.newInstance("See features","Adaptive app icon",
                R.drawable.second1, ContextCompat.getColor(getApplicationContext(),R.color.secondcolor)));
        addSlide(AppIntroFragment.newInstance("One more thing","Secure",
                R.drawable.third, ContextCompat.getColor(getApplicationContext(),R.color.thirdcolor)));
        addSlide(AppIntroFragment.newInstance("All set go","Amazing app for news",
                R.drawable.last1, ContextCompat.getColor(getApplicationContext(),R.color.lastcolor)));
        setFadeAnimation();
        sharedPreferences = getApplicationContext().getSharedPreferences("mypreference", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences != null){
            boolean checkshared = sharedPreferences.getBoolean("checkStated",false);
            if (checkshared == true){
                startActivity(new Intent(getApplicationContext(),SplashActivity.class));
                finish();
            }
        }
    }
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        editor.putBoolean("checkStated",true).commit();
        // Decide what to do when the user clicks on "Skip"
        finish();
    }
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        editor.putBoolean("checkStated",true).commit();
        // Decide what to do when the user clicks on "Done"
        finish();
    }
}