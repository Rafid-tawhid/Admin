package com.hk.meditechadmin;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hk.meditechadmin.CommonStatic.Common;
import com.hk.meditechadmin.databinding.ActivityPatientBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class PatientActivity extends AppCompatActivity {
    private ActivityPatientBinding binding;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient);

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        replaceFragment(new HealthConditionFragment());  //replace initially

        //set title header including patient id and date
        setHeader();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setHeader() {
        binding.progress.setVisibility(View.VISIBLE);
        if (Common.currentPatient != null) {
            //set date
            databaseReference.child("patients").child(Common.currentPatient.getPhoneNo()).child("lastUpdated").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        binding.updatedDate.setText(dataSnapshot.getValue().toString());
                    }
                    binding.progress.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //set patient id
            binding.patientId.setText(Common.currentPatient.getPhoneNo());
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.healthCondition:
                    replaceFragment(new HealthConditionFragment());
                    return true;
                case R.id.documents:
                    replaceFragment(new DocumentFragment());
                    return true;
                case R.id.prescriptions:
                    replaceFragment(new PrescriptionFragment());
                    return true;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    return true;
            }
            return false;
        }
    };


    public void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.frameLayoutID, fragment);
        ft.commit();
    }
}
