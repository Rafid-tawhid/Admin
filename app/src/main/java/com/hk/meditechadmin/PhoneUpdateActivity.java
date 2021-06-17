package com.hk.meditechadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hk.meditechadmin.CommonStatic.Common;
import com.hk.meditechadmin.ModelClass.Patient;
import com.hk.meditechadmin.databinding.ActivityPhoneUpdateBinding;

public class PhoneUpdateActivity extends AppCompatActivity {
private ActivityPhoneUpdateBinding binding;
    private DatabaseReference databaseReference;
    private AlertDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_phone_update);

        //progress dialogue set up

        progressDialog = new AlertDialog.Builder(PhoneUpdateActivity.this).create();
        final View view = LayoutInflater.from(PhoneUpdateActivity.this).inflate(R.layout.progress_diaog_layout, null);
        progressDialog.setCancelable(false);
        progressDialog.setView(view);

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        binding.continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.phoneET.getText().toString().trim().equals("")){
                    binding.phoneET.setError("Enter patient Id");
                    binding.phoneET.requestFocus();
                    return;
                }
                if(binding.phoneET.getText().toString().trim().length()!=11){
                    binding.phoneET.setError("Invalid phone no.");
                    binding.phoneET.requestFocus();
                    return;
                }
                checkPatient(binding.phoneET.getText().toString());
            }
        });

        //back button
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void checkPatient(String phone) {
        //binding.progress.setVisibility(View.VISIBLE);
        progressDialog.show();

        final DatabaseReference userRef = databaseReference.child("patients").child(phone).child("profile");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Patient patient = dataSnapshot.getValue(Patient.class);
                    Common.currentPatient = patient;
                    //binding.progress.setVisibility(View.GONE);
                    progressDialog.dismiss();

                    //go to patient activity
                    startActivity(new Intent(PhoneUpdateActivity.this,PatientActivity.class));
                    finish();
                }
                else{
                    //binding.progress.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    Toast.makeText(PhoneUpdateActivity.this, "User not exist with this number! ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
