package com.hk.meditechadmin;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hk.meditechadmin.Adapter.AdapterPrescription;
import com.hk.meditechadmin.CommonStatic.Common;
import com.hk.meditechadmin.ModelClass.Prescription;
import com.hk.meditechadmin.databinding.FragmentPrescriptionBinding;

import java.util.ArrayList;
import java.util.List;


public class PrescriptionFragment extends Fragment {
    FragmentPrescriptionBinding binding;
    private DatabaseReference databaseReference;
    private List<Prescription> prescriptionList;
    private AdapterPrescription adapterPrescription;
    private Context context;

    public PrescriptionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prescription, container, false);
        context = container.getContext();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("patients")
                .child(Common.currentPatient.getPhoneNo())
                .child("prescriptions");
        prescriptionList = new ArrayList<>();
        adapterPrescription = new AdapterPrescription(prescriptionList, context);
        binding.prescriptionRV.setLayoutManager(new LinearLayoutManager(context));
        binding.prescriptionRV.setAdapter(adapterPrescription);

        getPrescription();

        binding.addPrescriptionFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPrescriptionFragment addPrescriptionFragment = new AddPrescriptionFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.frameLayoutID, addPrescriptionFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        //hide FAB on scrolling
        binding.prescriptionRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    binding.addPrescriptionFB.show();
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && binding.addPrescriptionFB.isShown())
                    binding.addPrescriptionFB.hide();
            }
        });
    }

    private void getPrescription() {
        binding.progress.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    prescriptionList.clear();
                    binding.emptyItemView.setVisibility(View.GONE);
                    for (DataSnapshot pSnapshot : dataSnapshot.getChildren()) {
                        Prescription cPrescription = pSnapshot.getValue(Prescription.class);
                        prescriptionList.add(cPrescription);
                        adapterPrescription.notifyDataSetChanged();
                    }
                    binding.progress.setVisibility(View.GONE);
                } else {
                    binding.emptyItemView.setVisibility(View.VISIBLE);
                    binding.progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
