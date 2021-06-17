package com.hk.meditechadmin;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hk.meditechadmin.databinding.ActivityOtpVerifyBinding;

import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

public class OTpVerifyActivity extends AppCompatActivity {
    private ActivityOtpVerifyBinding binding;
    private String phoneNo;
    private String verificationId;
    private FirebaseAuth firebaseAuth;
    public AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verify);

        dialog = new SpotsDialog.Builder().setContext(OTpVerifyActivity.this).build();
        dialog.setMessage("Please wait.."+".");
        dialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();


        //get phone
        if (getIntent() != null) {
            phoneNo = getIntent().getStringExtra("phone");
        }

        sendOTP();

        //button
        binding.changeNumberTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.resendOtpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTP();
            }
        });

        binding.continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.otpInput.getText().toString().trim().equals("")) {
                    binding.otpInput.setError("Enter OTP code");
                    binding.otpInput.requestFocus();
                    return;
                } else if (binding.otpInput.getText().toString().trim().length() != 6) {
                    binding.otpInput.setError("Invalid OTP code");
                    binding.otpInput.requestFocus();
                    return;
                } else {
                    verify(binding.otpInput.getText().toString().trim());
                }
            }
        });
    }

    private void sendOTP() {



        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+88" + phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                OTpVerifyActivity.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


        Log.d("SendOtp() : ", "Called");
        //binding.progressView.setVisibility(View.VISIBLE);
        dialog.show();
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d("onVerification():", "Called");
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                binding.otpInput.setText(code);
                verify(code);
            }
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OTpVerifyActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            //binding.progressView.setVisibility(View.GONE);
            dialog.dismiss();
            Toast.makeText(OTpVerifyActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
            Log.d("onCodeSent() : ", "Called");
        }

    };


    private void verify(String code) {
        dialog.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //binding.progressView.setVisibility(View.GONE);
                    //go to sign up page
                    dialog.dismiss();

                    Toast.makeText(OTpVerifyActivity.this, "phone verified successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(
                            new Intent(OTpVerifyActivity.this, RegistrationActivity.class)
                                    .putExtra("phoneNo", phoneNo)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                    );
                } else {
                    dialog.dismiss();
                    Toast.makeText(OTpVerifyActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
