package com.hk.meditechadmin;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hk.meditechadmin.CommonStatic.Common;
import com.hk.meditechadmin.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set image
        Picasso.get().load(Common.currentPatient.getImage()).into(binding.circleImageView);
        Picasso.get().load(Common.currentPatient.getQrImage()).into(binding.qrImage);
        //set profile
        binding.name.setText(Common.currentPatient.getName());
        binding.genderId.setText(Common.currentPatient.getGender());
        binding.email.setText(Common.currentPatient.getEmail());
        binding.age.setText(Common.currentPatient.getAge());
        binding.bloodGroup.setText(Common.currentPatient.getBloodGroup());
        binding.homePhoneNo.setText(Common.currentPatient.getHomeNo());
        binding.address.setText(Common.currentPatient.getAddress());


        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load update profile fragment
                UpdateProfileFragment profileFragment = new UpdateProfileFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.frameLayoutID, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

    }
}
