package com.hotelsys.cartable.androidapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences user_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        user_detail=getSharedPreferences(Constants.USER_DETAIL, Context.MODE_PRIVATE);
        if (user_detail.getBoolean(Constants.IS_LOGGED_IN, false)) {
            Intent i = new Intent(this, LoggedInActivity.class);
            startActivity(i);
            SplashActivity.this.finish();
        }
        else
        {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            SplashActivity.this.finish();
        }

    }
}
