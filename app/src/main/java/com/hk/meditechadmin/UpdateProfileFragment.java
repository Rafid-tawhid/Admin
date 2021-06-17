package com.hk.meditechadmin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hk.meditechadmin.CommonStatic.Common;
import com.hk.meditechadmin.ModelClass.Patient;
import com.hk.meditechadmin.databinding.FragmentUpdateProfileBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class UpdateProfileFragment extends Fragment {
    private FragmentUpdateProfileBinding binding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private AlertDialog progressDialog;
    private String profileImage = "";
    private String[] bloodGroup = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private static final int REQUEST_CODE_FOR_IMAGE_GALLERY = 1;
    private static final int REQUEST_CODE_FOR_IMAGE_CAMERA = 0;
    private Context context;

    public UpdateProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_profile, container, false);

        context = container.getContext();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        //after updated need to back profile fragment and Update Common.currentPatient Property

        //progress dialogue set up

        progressDialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.progress_diaog_layout, null);
        progressDialog.setCancelable(false);
        progressDialog.setView(view);

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        //autocomplete Textview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.select_dialog_item, bloodGroup);
        binding.patientBlood.setThreshold(1);
        binding.patientBlood.setAdapter(adapter);

        //set current value

        profileImage = Common.currentPatient.getImage();
        Picasso.get().load(Common.currentPatient.getImage()).into(binding.circleImageView);


        binding.patientName.setText(Common.currentPatient.getName());
        binding.patientMail.setText(Common.currentPatient.getEmail());
        binding.patientAge.setText(Common.currentPatient.getAge());
        binding.patientBlood.setText(Common.currentPatient.getBloodGroup());
        binding.homePhone.setText(Common.currentPatient.getHomeNo());
        binding.patientAddress.setText(Common.currentPatient.getAddress());


        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.patientName.getText().toString().trim().equals("")) {
                    binding.patientName.setError("Enter patient name");
                    binding.patientName.requestFocus();
                    return;
                }
                if (binding.patientMail.getText().toString().trim().equals("")) {
                    binding.patientMail.setError("Enter patient mail");
                    binding.patientMail.requestFocus();
                    return;
                }
                if (binding.patientAge.getText().toString().trim().equals("")) {
                    binding.patientAge.setError("Enter patient age");
                    binding.patientAge.requestFocus();
                    return;
                }
                if (binding.patientAddress.getText().toString().trim().equals("")) {
                    binding.patientAddress.setError("Enter patient address");
                    binding.patientAddress.requestFocus();
                    return;
                }
                if (binding.patientBlood.getText().toString().trim().equals("")) {
                    binding.patientBlood.setError("Enter patient blood group");
                    binding.patientBlood.requestFocus();
                    return;
                }
                if (binding.homePhone.getText().toString().trim().equals("")) {
                    binding.homePhone.setError("Enter patient home phone");
                    binding.homePhone.requestFocus();
                    return;
                }


                savePatient(binding.patientName.getText().toString(),
                        binding.patientMail.getText().toString(),
                        binding.patientAge.getText().toString(),
                        binding.patientAddress.getText().toString(),
                        binding.patientBlood.getText().toString(),
                        binding.homePhone.getText().toString(),
                        profileImage,
                        Common.currentPatient.getQrImage()
                );

            }
        });


        //camera gallary button visible or invisible clicked on circleImage
        binding.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.cameraGalleryfield.getVisibility() == View.VISIBLE) {
                    binding.cameraGalleryfield.setVisibility(View.INVISIBLE);
                } else {
                    binding.cameraGalleryfield.setVisibility(View.VISIBLE);
                }
            }
        });


        //clicking for camera or gallery

        binding.cameraBtnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_CAMERA);
            }
        });

        binding.galleryBtnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_GALLERY);
            }
        });


        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load update profile fragment
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.frameLayoutID, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void savePatient(String name, String email, String age, String address, String blood, String homePhone, String image, String qrImage) {
        //binding.progress.setVisibility(View.VISIBLE);
        progressDialog.show();

        Patient patient = new Patient(Common.currentPatient.getPhoneNo(), name, Common.currentPatient.getGender(), email, age, address, blood, homePhone, image, qrImage);
        databaseReference.child("patients").child(Common.currentPatient.getPhoneNo()).child("profile").setValue(patient).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //binding.progress.setVisibility(View.GONE);
                    progressDialog.dismiss();

                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    //update current patient property
                    updateCurrentPatient();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                //binding.progress.setVisibility(View.GONE);
                progressDialog.dismiss();
            }
        });


    }

    private void updateCurrentPatient() {
        //binding.progress.setVisibility(View.VISIBLE);
        progressDialog.show();

        final DatabaseReference userRef = databaseReference.child("patients").child(Common.currentPatient.getPhoneNo()).child("profile");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Patient patient = dataSnapshot.getValue(Patient.class);
                    Common.currentPatient = patient;
                    //binding.progress.setVisibility(View.GONE);
                    progressDialog.dismiss();

                    //load back update profile fragment
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.frameLayoutID, profileFragment);
                    ft.addToBackStack(null);
                    ft.commit();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null && requestCode == REQUEST_CODE_FOR_IMAGE_GALLERY) {
                Uri uri = data.getData();
                binding.circleImageView.setImageURI(uri);
                uploadImageToStorage(uri);
            } else if (data != null && requestCode == REQUEST_CODE_FOR_IMAGE_CAMERA) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                binding.circleImageView.setImageBitmap(bitmap);

                //bitmap should be converted to uri format
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.
                        Images.Media.
                        insertImage(context.getContentResolver(), bitmap, "Camera Image", null);
                uploadImageToStorage(Uri.parse(path));
            }
        }
    }

    private void uploadImageToStorage(Uri uri) {
        //binding.progress.setVisibility(View.VISIBLE);
        progressDialog.show();

        final StorageReference memoryRef = storageReference.child(String.valueOf(System.currentTimeMillis()));

        memoryRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    memoryRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            profileImage = uri.toString();
                            Toast.makeText(context, "Picture Successfully Uploaded", Toast.LENGTH_LONG).show();
                            //binding.progress.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

}
