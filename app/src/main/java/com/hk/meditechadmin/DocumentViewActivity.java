package com.hk.meditechadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.hk.meditechadmin.databinding.ActivityDocumentViewBinding;
import com.squareup.picasso.Picasso;

public class DocumentViewActivity extends AppCompatActivity {
    private ActivityDocumentViewBinding binding;
    private String name, image, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_document_view);

        //get value form intent
        if (getIntent() != null) {
            name = getIntent().getStringExtra("reportName");
            image = getIntent().getStringExtra("reportImage");
            date = getIntent().getStringExtra("generateDate");

            //set Into image
            Picasso.get().load(image).placeholder(R.drawable.ic_file).into(binding.reportImage);
            binding.reportName.setText(name);
            binding.generateDate.setText(date);
        }

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
