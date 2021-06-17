package com.hk.meditechadmin;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hk.meditechadmin.CommonStatic.Common;
import com.hk.meditechadmin.ModelClass.Prescription;
import com.hk.meditechadmin.databinding.FragmentAddPrescriptionBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddPrescriptionFragment extends Fragment {
    private FragmentAddPrescriptionBinding binding;
    private AlertDialog progressDialog;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private Calendar calendar;
    private int year, month, day;
    private String cDate;
    private Context context;
    private boolean reqUpdate = false;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String doctorName = "", prescribeDate = "", prescriptionID = "", prescriptionImage = "", doctorDesignation = "";
    private static final int REQUEST_CODE_FOR_IMAGE_GALLERY = 1;
    private static final int REQUEST_CODE_FOR_IMAGE_CAMERA = 0;

    public AddPrescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_prescription, container, false);
        context = container.getContext();
        return binding.getRoot();
    }

    //rest of coding
    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        //progress dialogue set up

        progressDialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.progress_diaog_layout, null);
        progressDialog.setCancelable(false);
        progressDialog.setView(view);

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        //getDate from Calender
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //get value
        Bundle bundle = getArguments();
        if (bundle != null) {
            reqUpdate = true;

            prescriptionImage = bundle.getString("prescriptionImage");
            Picasso.get().load(prescriptionImage).into(binding.prescriptionImage);

            doctorName = bundle.getString("doctorName");
            prescribeDate = bundle.getString("prescribeDate");
            prescriptionID = bundle.getString("prescriptionID");
            doctorDesignation = bundle.getString("doctorDesignation");

            binding.doctorName.setText(doctorName);
            binding.generateDate.setText(prescribeDate);
            binding.doctorDesignation.setText(doctorDesignation);
        }

        //camera gallery field
        binding.prescriptionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.cameraGalleryfield.getVisibility() == View.VISIBLE) {
                    binding.cameraGalleryfield.setVisibility(View.INVISIBLE);
                } else {
                    binding.cameraGalleryfield.setVisibility(View.VISIBLE);
                }
            }
        });

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
                DocumentFragment documentFragment = new DocumentFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.frameLayoutID, documentFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        binding.generateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, fromDateListener, year, month, day);
                datePickerDialog.show();
            }
        });
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.doctorName.getText().toString().trim().equals("")) {
                    binding.doctorName.setError("Doctor name");
                    binding.doctorName.requestFocus();
                }
                if (binding.generateDate.getText().toString().trim().equals("")) {
                    binding.generateDate.setError("Prescribed date");
                    binding.generateDate.requestFocus();
                }
                if (binding.doctorDesignation.getText().toString().trim().equals("")) {
                    binding.doctorDesignation.setError("Doctor designation");
                    binding.doctorDesignation.requestFocus();
                }
                if (prescriptionImage.equals("")) {
                    Toast.makeText(context, "Prescription image is required", Toast.LENGTH_SHORT).show();
                }

                saveDocument(binding.doctorName.getText().toString(),binding.doctorDesignation.getText().toString(), binding.generateDate.getText().toString(), prescriptionImage);
            }
        });

    }

    //from date
    final DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int toDay) {
            calendar.set(year, month, toDay);
            String userFromDate = dateFormat.format(calendar.getTime());
            binding.generateDate.setText(userFromDate);
            binding.generateDate.setTextColor(Color.BLACK);

        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null && requestCode == REQUEST_CODE_FOR_IMAGE_GALLERY) {
                Uri uri = data.getData();
                binding.prescriptionImage.setImageURI(uri);
                uploadImageToStorage(uri);
            } else if (data != null && requestCode == REQUEST_CODE_FOR_IMAGE_CAMERA) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                binding.prescriptionImage.setImageBitmap(bitmap);

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
                            prescriptionImage = uri.toString();
                            Toast.makeText(context, "Picture Successfully Uploaded", Toast.LENGTH_LONG).show();
                            //binding.progress.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }


    private void saveDocument(String doctorName, String doctorDesignation, String generateDate, String image) {

        cDate = dateFormat.format(new Date());


        // binding.progress.setVisibility(View.VISIBLE);
        progressDialog.show();

        String dId = databaseReference.push().getKey();

        DatabaseReference documentReference = databaseReference
                .child("patients")
                .child(Common.currentPatient.getPhoneNo())
                .child("prescriptions");
        if (!reqUpdate) {
            //Document document = new Document(image, reportName, generateDate, dId);
            Prescription cPrescription = new Prescription(doctorName,doctorDesignation,generateDate,image,dId);
            documentReference.child(dId).setValue(cPrescription).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        databaseReference.child("patients").child(Common.currentPatient.getPhoneNo()).child("lastUpdated").setValue(cDate);
                        //binding.progress.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        Toast.makeText(context, "Prescription added successfully", Toast.LENGTH_SHORT).show();
                        //back to document list
                        PrescriptionFragment prescriptionFragment = new PrescriptionFragment();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();
                        ft.replace(R.id.frameLayoutID, prescriptionFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            });
        } else if (reqUpdate) {

            //update value
            //Document document = new Document(image, reportName, generateDate, documentId);
            Prescription cPrescription = new Prescription(doctorName,doctorDesignation,prescribeDate,prescriptionImage,prescriptionID);
            documentReference.child(prescriptionID).setValue(cPrescription).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        databaseReference.child("patients").child(Common.currentPatient.getPhoneNo()).child("lastUpdated").setValue(cDate);
                        // binding.progress.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();

                        //back to document list
                        PrescriptionFragment prescriptionFragment = new PrescriptionFragment();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();
                        ft.replace(R.id.frameLayoutID, prescriptionFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            });

        }


    }

}
