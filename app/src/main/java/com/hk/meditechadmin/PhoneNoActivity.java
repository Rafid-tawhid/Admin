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
import com.hk.meditechadmin.databinding.ActivityPhoneNoBinding;

public class PhoneNoActivity extends AppCompatActivity {
    private ActivityPhoneNoBinding binding;
    private AlertDialog progressDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_no);

        //progress dialogue set up

        progressDialog = new AlertDialog.Builder(PhoneNoActivity.this).create();
        final View view = LayoutInflater.from(PhoneNoActivity.this).inflate(R.layout.progress_diaog_layout, null);
        progressDialog.setCancelable(false);
        progressDialog.setView(view);

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        binding.continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPhone();
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void checkPhone() {
        if (binding.phoneET.getText().toString().trim().equals("")) {
            binding.phoneET.setError("Enter patient phone no");
            binding.phoneET.requestFocus();
            return;
        } else if (binding.phoneET.getText().toString().trim().length() != 11) {
            binding.phoneET.setError("Invalid phone no");
            binding.phoneET.requestFocus();
            return;
        } else {
            checkPhone(binding.phoneET.getText().toString());

        }
    }

    private void checkPhone(String phone) {
        //binding.progress.setVisibility(View.VISIBLE);
        progressDialog.show();

        final DatabaseReference userRef = databaseReference.child("patients").child(phone).child("profile");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(PhoneNoActivity.this, "User already exist with this number! ", Toast.LENGTH_SHORT).show();
                    //binding.progress.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
                else{
                    //binding.progress.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    Intent intent = new Intent(PhoneNoActivity.this, OTpVerifyActivity.class);
                    intent.putExtra("phone", binding.phoneET.getText().toString());
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
