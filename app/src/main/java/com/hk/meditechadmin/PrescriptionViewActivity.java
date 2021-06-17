package com.hk.meditechadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hk.meditechadmin.databinding.ActivityPrescriptionViewBinding;
import com.squareup.picasso.Picasso;

public class PrescriptionViewActivity extends AppCompatActivity {
private ActivityPrescriptionViewBinding binding;
private String prescriptionImage, doctorName, prescribeDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_prescription_view);

        //get value form intent
        if (getIntent() != null) {
            doctorName = getIntent().getStringExtra("doctorName");
            prescriptionImage = getIntent().getStringExtra("prescriptionImage");
            prescribeDate = getIntent().getStringExtra("prescribeDate");

            //set Into image
            Picasso.get().load(prescriptionImage).placeholder(R.drawable.ic_file).into(binding.prescriptionImage);
            binding.doctorName.setText(doctorName);
            binding.prescribedDate.setText(prescribeDate);
        }

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
