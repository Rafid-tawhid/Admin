package com.hk.meditechadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.hk.meditechadmin.CommonStatic.Common;
import com.hk.meditechadmin.ModelClass.Patient;
import com.hk.meditechadmin.databinding.ActivityRegistrationBinding;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private AlertDialog progressDialog;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private RadioButton genderRB;
    private String genderValue = "";
    private String profileImage = "";
    private Bitmap bitmap;
    private String TAG = "generateQRCode";
    private String qrImage = "";
    private String phoneNo;
    private String[] bloodGroup = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private static final int REQUEST_CODE_FOR_IMAGE_GALLERY = 1;
    private static final int REQUEST_CODE_FOR_IMAGE_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);

        //progress dialogue set up

        progressDialog = new AlertDialog.Builder(RegistrationActivity.this).create();
        final View view = LayoutInflater.from(RegistrationActivity.this).inflate(R.layout.progress_diaog_layout, null);
        progressDialog.setCancelable(false);
        progressDialog.setView(view);


        //autocomplete Textview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegistrationActivity.this,
                android.R.layout.select_dialog_item, bloodGroup);
        binding.patientBlood.setThreshold(1);
        binding.patientBlood.setAdapter(adapter);


        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        //getPhoneNo
        if (getIntent() != null) {
            phoneNo = getIntent().getStringExtra("phoneNo");
        }

        //generate QR Code and get image URL
        generateQRCode();


        binding.submitButton.setOnClickListener(new View.OnClickListener() {
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
                if (profileImage.equals("")) {
                    Toast.makeText(RegistrationActivity.this, "Patient image is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                //radio button
                int selectedId = binding.radioGroup.getCheckedRadioButtonId();
                genderRB = view.findViewById(selectedId);
                //conditionValue = conditionRB.getText().toString();
                if (selectedId == R.id.maleRB) {
                    genderValue = "Male";
                } else {
                    genderValue = "Female";
                }
                savePatient(binding.patientName.getText().toString(),
                        genderValue,
                        binding.patientMail.getText().toString(),
                        binding.patientAge.getText().toString(),
                        binding.patientAddress.getText().toString(),
                        binding.patientBlood.getText().toString(),
                        binding.homePhone.getText().toString(),
                        profileImage
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

        //back button
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void generateQRCode() {
        //qr code generate based on patient phone NO
        MultiFormatWriter formatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = formatWriter.encode(phoneNo, BarcodeFormat.QR_CODE, 250, 250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            //binding.qrImage.setImageBitmap(bitmap);
            //binding.saveToGallery.setVisibility(View.VISIBLE);

            //upload qr Image to storage
            //bitmap should be converted to uri format
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.
                    Images.Media.
                    insertImage(RegistrationActivity.this.getContentResolver(), bitmap, "Camera Image", null);
            uploadQRToStorage(Uri.parse(path));


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

    }

    private void uploadQRToStorage(Uri uri) {
        //binding.progress.setVisibility(View.VISIBLE);
        progressDialog.show();
        final StorageReference qrRef = storageReference.child(String.valueOf(System.currentTimeMillis()));

        qrRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    qrRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            qrImage = uri.toString();
                            Toast.makeText(RegistrationActivity.this, "QR image Successfully Uploaded", Toast.LENGTH_LONG).show();
                            // binding.progress.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void savePatient(String name, String genderValue, String email, String age, String address, String blood, String homePhone, String image) {
        //binding.progress.setVisibility(View.VISIBLE);
        progressDialog.show();

        final Patient patient = new Patient(phoneNo, name, genderValue, email, age, address, blood, homePhone, image, qrImage);
        databaseReference.child("patients").child(phoneNo).child("profile").setValue(patient).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //binding.progress.setVisibility(View.GONE);
                    Common.currentPatient = patient;  //after successfully registered got to patient activity

                    progressDialog.dismiss();

                    Toast.makeText(RegistrationActivity.this, "Patient added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this, PatientActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
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
                        insertImage(RegistrationActivity.this.getContentResolver(), bitmap, "Camera Image", null);
                uploadImageToStorage(Uri.parse(path));
            }
        }
    }

    private void uploadImageToStorage(Uri uri) {
        // binding.progress.setVisibility(View.VISIBLE);
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
                            Toast.makeText(RegistrationActivity.this, "Picture Successfully Uploaded", Toast.LENGTH_LONG).show();

                            // binding.progress.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

}
