package com.hk.meditechadmin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.hk.meditechadmin.CommonStatic.Common;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //delay
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(SplashActivity.this)){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }
                else{
                    //show no internet Dialog
                    AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
                    final View view = LayoutInflater.from(SplashActivity.this).inflate(R.layout.no_internet_dialogue, null);
                    alertDialog.setCancelable(false);
                    Button exitBtn = view.findViewById(R.id.exitButton);

                    exitBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    });

                    alertDialog.setView(view);
                    alertDialog.show();

                }
            }
        },2500);
    }
}
