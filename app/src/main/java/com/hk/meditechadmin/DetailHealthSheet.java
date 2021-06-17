package com.hk.meditechadmin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DetailHealthSheet extends BottomSheetDialogFragment {
    private TextView deasesNameTV, reportTypeTV, labResultTV, commentTV, examineDateTV, conditionTV;


    private String deasesName, reportType, labResult, comment, examineDate, condition;

    public DetailHealthSheet(String deasesName, String reportType, String labResult, String comment, String examineDate, String condition) {
        this.deasesName = deasesName;
        this.reportType = reportType;
        this.labResult = labResult;
        this.comment = comment;
        this.examineDate = examineDate;
        this.condition = condition;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_health_sheet_layout, container);
        deasesNameTV = view.findViewById(R.id.deasesNameTV);
        reportTypeTV = view.findViewById(R.id.reportTypeTV);
        labResultTV = view.findViewById(R.id.resultValueTV);
        commentTV = view.findViewById(R.id.commentTV);
        examineDateTV = view.findViewById(R.id.examineDateTV);
        conditionTV = view.findViewById(R.id.conditionTV);




        //setText

        deasesNameTV.setText(deasesName);
        reportTypeTV.setText(reportType);
        labResultTV.setText(labResult);
        commentTV.setText(comment);
        examineDateTV.setText(examineDate);
        conditionTV.setText(condition);

        //set condition color
        if(condition.equals("Normal")){
            conditionTV.setTextColor(Color.GREEN);
        }
        else{
            conditionTV.setTextColor(Color.RED);
        }

        return view;

    }
}
