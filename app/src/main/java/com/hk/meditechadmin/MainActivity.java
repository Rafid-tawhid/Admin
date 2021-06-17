package com.hk.meditechadmin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hk.meditechadmin.databinding.ActivityMainBinding;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String SHOWCASE_ID = "sequence example";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //permission
        getPermission();


        //button activity
        binding.addPationtId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PhoneNoActivity.class));  //testing purpose need to change
            }
        });

        binding.updatePatientId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PhoneUpdateActivity.class));  //testing purpose need to change
            }
        });
        binding.guideLineId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //show apps guideline
                startActivity(new Intent(MainActivity.this,GuideLineActivity.class));


            }
        });
        binding.aboutId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //show no internet Dialog
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                final View alertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.about_dialogue, null);
                alertDialog.setCancelable(true);
                Button okBtn = alertView.findViewById(R.id.okButton);

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setView(alertView);
                alertDialog.show();
            }
        });

        showCaseView();
    }


    private void showCaseView() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(MainActivity.this, SHOWCASE_ID);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
                Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem(binding.addPationtId, "Adding for new patient data", "Close");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(MainActivity.this)
                        .setSkipText("SKIP")
                        .setTarget(binding.updatePatientId)
                        .setDismissText("Close")
                        .setContentText("Update existing patient data")
                        .withCircleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(MainActivity.this)
                        .setTarget(binding.guideLineId)
                        .setDismissText("Close")
                        .setContentText("Find out apps guideline")
                        .withCircleShape()
                        .build()
        );
        sequence.start();
    }


    private void getPermission() {
        String[] permissions = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(permissions, 0);
            } else {

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}



