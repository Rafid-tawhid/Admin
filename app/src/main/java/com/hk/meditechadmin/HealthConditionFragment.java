package com.hk.meditechadmin;


import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hk.meditechadmin.Adapter.AdapterHealth;
import com.hk.meditechadmin.CommonStatic.Common;
import com.hk.meditechadmin.ModelClass.Health;
import com.hk.meditechadmin.databinding.FragmentHealthConditionBinding;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HealthConditionFragment extends Fragment {
    private FragmentHealthConditionBinding binding;
    private Context context;
    private List<Health> healthList;
    private Button examineDateButton;
    private RadioButton conditionRB;

    private DatabaseReference databaseReference;
    private AdapterHealth adapterHealth;
    private String conditionValue;
    private AlertDialog dialog;
    private AVLoadingIndicatorView progress;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private Calendar calendar;
    private int year, month, day;
    private String cDate;

    public HealthConditionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_health_condition, container, false);
        context = container.getContext();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //getDate from Calender
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        //initialize
        healthList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        adapterHealth = new AdapterHealth(context, healthList);
        binding.hConditionRV.setLayoutManager(new LinearLayoutManager(context));
        binding.hConditionRV.setAdapter(adapterHealth);

        getHealth();

        //add health
        binding.addConditionFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add health dialogue
                addHealth();
            }
        });

        //hide FAB on scrolling
        binding.hConditionRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    binding.addConditionFB.show();
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && binding.addConditionFB.isShown())
                    binding.addConditionFB.hide();
            }
        });
    }


    private void getHealth() {
        binding.progress.setVisibility(View.VISIBLE);

        DatabaseReference healthReffReference = databaseReference.child("patients").child(Common.currentPatient.getPhoneNo()).child("healthConditions");
        healthReffReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    binding.emptyItemView.setVisibility(View.GONE);
                    healthList.clear();
                    for (DataSnapshot pSnapshot : dataSnapshot.getChildren()) {
                        Health cHealth = pSnapshot.getValue(Health.class);
                        healthList.add(cHealth);
                        adapterHealth.notifyDataSetChanged();
                    }
                    binding.progress.setVisibility(View.GONE);
                } else {
                    //empty data
                    binding.emptyItemView.setVisibility(View.VISIBLE);
                    binding.progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addHealth() {

        dialog = new AlertDialog.Builder(context).create();
        final View view = LayoutInflater.from(context).inflate(R.layout.add_health_dialogue, null);
        progress = view.findViewById(R.id.progress);

        final EditText deasesName = view.findViewById(R.id.deasesName);
        final EditText reportType = view.findViewById(R.id.reportType);
        final EditText comment = view.findViewById(R.id.comment);
        final EditText resultValue = view.findViewById(R.id.resultValue);
        final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        Button saveButton = view.findViewById(R.id.saveButton);
        examineDateButton = view.findViewById(R.id.examineDate);
        Button canceleButton = view.findViewById(R.id.cancelButton);


        //date
        examineDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, fromDateListener, year, month, day);
                datePickerDialog.show();

            }
        });

        //save
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deasesName.getText().toString().trim().equals("")) {
                    deasesName.setError("Disease name");
                    deasesName.requestFocus();
                    return;
                }
                if (reportType.getText().toString().trim().equals("")) {
                    reportType.setError("Report type");
                    reportType.requestFocus();
                    return;
                }
                if (comment.getText().toString().trim().equals("")) {
                    comment.setError("Disease comment");
                    comment.requestFocus();
                    return;
                }
                if (resultValue.getText().toString().trim().equals("")) {
                    resultValue.setError("Result value");
                    resultValue.requestFocus();
                    return;
                }
                if (examineDateButton.getText().toString().trim().equals("")) {
                    examineDateButton.setError("Examine date");
                    examineDateButton.requestFocus();
                    return;
                }

                //radio button
                int selectedId = radioGroup.getCheckedRadioButtonId();
                conditionRB = view.findViewById(selectedId);
                //conditionValue = conditionRB.getText().toString();
                if (selectedId == R.id.normalRB) {
                    conditionValue = "Normal";
                } else {
                    conditionValue = "Threat";
                }


                //save to database
                saveHealthData(
                        deasesName.getText().toString(),
                        reportType.getText().toString(),
                        resultValue.getText().toString(),
                        comment.getText().toString(),
                        examineDateButton.getText().toString(),
                        conditionValue

                );

            }
        });

        //cancel
        canceleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.setView(view);
        dialog.show();
    }


    //from date
    final DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int toDay) {
            calendar.set(year, month, toDay);
            String userFromDate = dateFormat.format(calendar.getTime());
            examineDateButton.setText(userFromDate);
            examineDateButton.setTextColor(Color.BLACK);

        }
    };

    //save data
    private void saveHealthData(String deasesName, String reportType, String labResult, String comment, String examineDate, String condition) {
        cDate = dateFormat.format(new Date());


        progress.setVisibility(View.VISIBLE);
        String hId = databaseReference.push().getKey();
        Health health = new Health(deasesName, reportType, labResult, comment, examineDate, condition, hId);

        DatabaseReference healthReference = databaseReference.child("patients").child(Common.currentPatient.getPhoneNo()).child("healthConditions").child(hId);
        healthReference.setValue(health).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    databaseReference.child("patients").child(Common.currentPatient.getPhoneNo()).child("lastUpdated").setValue(cDate);
                    progress.setVisibility(View.GONE);
                    Toast.makeText(context, "Health data added successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.setVisibility(View.GONE);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


    }


}
