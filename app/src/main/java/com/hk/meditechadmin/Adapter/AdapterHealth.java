package com.hk.meditechadmin.Adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hk.meditechadmin.CommonStatic.Common;
import com.hk.meditechadmin.DetailHealthSheet;
import com.hk.meditechadmin.ModelClass.Health;
import com.hk.meditechadmin.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdapterHealth extends RecyclerView.Adapter<AdapterHealth.ViewHolder> {
    private Context context;
    private List<Health> healthList;

    private DatabaseReference databaseReference;

    private String conditionValue;
    private int pos;
    private AlertDialog dialog;
    private AVLoadingIndicatorView progress;
    private Button examineDateButton;
    private RadioButton conditionRB;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private Calendar calendar;
    private int year, month, day;
    private String cDate;

    public AdapterHealth(Context context, List<Health> healthList) {
        this.context = context;
        this.healthList = healthList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_health_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Health cHealth = healthList.get(position);

        pos = position;

        //layout process
        holder.name.setText(cHealth.getDeasesName());
        holder.type.setText(cHealth.getReportType());
        holder.value.setText(cHealth.getLabResult());
        holder.condition.setText(cHealth.getCondition());

        //set condition color
        if(cHealth.getCondition().equals("Normal")){
            holder.condition.setTextColor(Color.GREEN);
        }
        else{
            holder.condition.setTextColor(Color.RED);
        }


        //item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //details bottom sheet

                DetailHealthSheet detailHealthSheet = new DetailHealthSheet(
                        cHealth.getDeasesName(),
                        cHealth.getReportType(),
                        cHealth.getLabResult(),
                        cHealth.getComment(),
                        cHealth.getExamineDate(),
                        cHealth.getCondition()
                );
                detailHealthSheet.show(((FragmentActivity) context).getSupportFragmentManager(), "Health Details");
            }
        });

        //setting button
        holder.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //popup update and delete menu
                PopupMenu menu = new PopupMenu(context, view);  //set popup menu custom process
                menu.getMenuInflater().inflate(R.menu.health_menu, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.updateItem:
                                updateHealth(cHealth);
                                break;
                            case R.id.deleteItem:
                                deleteHealth(cHealth, position);
                                break;

                        }
                        return false;
                    }
                });
                menu.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return healthList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, type, value, condition;
        private ImageView setting;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.deasesName);
            type = itemView.findViewById(R.id.reportType);
            value = itemView.findViewById(R.id.resultValue);
            condition = itemView.findViewById(R.id.conditionType);
            setting = itemView.findViewById(R.id.settingBtn);
        }
    }


    private void updateHealth(final Health cHealth) {
//getDate from Calender
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        dialog = new AlertDialog.Builder(context).create();
        final View view = LayoutInflater.from(context).inflate(R.layout.add_health_dialogue, null);
        progress = view.findViewById(R.id.progress);

        final EditText deasesName = view.findViewById(R.id.deasesName);
        final EditText reportType = view.findViewById(R.id.reportType);
        final EditText comment = view.findViewById(R.id.comment);
        final EditText resultValue = view.findViewById(R.id.resultValue);
        final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        final RadioButton normalRB = view.findViewById(R.id.normalRB);
        final RadioButton threatRB = view.findViewById(R.id.threatRB);
        Button saveButton = view.findViewById(R.id.saveButton);
        examineDateButton = view.findViewById(R.id.examineDate);
        Button canceleButton = view.findViewById(R.id.cancelButton);

        //set current value
        deasesName.setText(cHealth.getDeasesName());
        reportType.setText(cHealth.getReportType());
        comment.setText(cHealth.getComment());
        resultValue.setText(cHealth.getLabResult());
        examineDateButton.setText(cHealth.getExamineDate());
        if (cHealth.getCondition().equals("Normal")) {
            normalRB.setChecked(true);
        } else {
            threatRB.setChecked(true);
        }


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
                        conditionValue,
                        cHealth.getHealthId()

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

    private void deleteHealth(final Health cHealth, final int position) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("patients")
                .child(Common.currentPatient.getPhoneNo())
                .child("healthConditions").
                child(cHealth.getHealthId()).
                removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    healthList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //from date listener
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
    private void saveHealthData(String deasesName, String reportType, String labResult, String comment, String examineDate, String condition, String hId) {
        cDate = dateFormat.format(new Date());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        progress.setVisibility(View.VISIBLE);
        Health health = new Health(deasesName, reportType, labResult, comment, examineDate, condition, hId);

        DatabaseReference healthReference = databaseReference.child("patients").child(Common.currentPatient.getPhoneNo()).child("healthConditions").child(hId);
        healthReference.setValue(health).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    databaseReference.child("patients").child(Common.currentPatient.getPhoneNo()).child("lastUpdated").setValue(cDate);
                    progress.setVisibility(View.GONE);
                    Toast.makeText(context, "Health data updated successfully", Toast.LENGTH_SHORT).show();
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
