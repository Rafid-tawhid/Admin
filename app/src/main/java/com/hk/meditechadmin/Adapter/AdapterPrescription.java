package com.hk.meditechadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hk.meditechadmin.AddPrescriptionFragment;
import com.hk.meditechadmin.CommonStatic.Common;
import com.hk.meditechadmin.ModelClass.Prescription;
import com.hk.meditechadmin.PrescriptionViewActivity;
import com.hk.meditechadmin.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPrescription extends RecyclerView.Adapter<AdapterPrescription.ViewHolder> {
    private List<Prescription> prescriptionList;
    private Context context;
    private DatabaseReference databaseReference;
    private View getView;

    public AdapterPrescription(List<Prescription> prescriptionList, Context context) {
        this.prescriptionList = prescriptionList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_layout, parent, false);
        getView = view;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Prescription cPrescription = prescriptionList.get(position);

        //load image
        Picasso.get().load(cPrescription.getPrescriptionImage()).placeholder(R.drawable.ic_file).into(holder.prescriptionIV);

        holder.drName.setText(cPrescription.getDoctorName());
        holder.drDesignation.setText(cPrescription.getDoctorDesignation());
        holder.prescribeDate.setText(cPrescription.getPrescribeDate());

        //full screen image
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, PrescriptionViewActivity.class);
                intent.putExtra("doctorName",cPrescription.getDoctorName());
                intent.putExtra("prescribeDate",cPrescription.getPrescribeDate());
                intent.putExtra("prescriptionImage",cPrescription.getPrescriptionImage());
                context.startActivity(intent);
            }
        });

        //menu for delete update
        holder.menuBtn.setOnClickListener(new View.OnClickListener() {
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
                                updatePrescription(cPrescription);
                                break;
                            case R.id.deleteItem:
                                deletePrescription(cPrescription, position);
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
        return prescriptionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView prescriptionIV,menuBtn;
        private TextView drName,drDesignation, prescribeDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            drName = itemView.findViewById(R.id.doctorName);
            drDesignation = itemView.findViewById(R.id.doctorDesignation);
            prescribeDate = itemView.findViewById(R.id.prescribeDateTV);
            prescriptionIV = itemView.findViewById(R.id.prescriptionImage);
            menuBtn = itemView.findViewById(R.id.menuPrescriptionBtn);
        }
    }


    //custom methods
    private void deletePrescription(Prescription cPrescription, final int position) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("patients")
                .child(Common.currentPatient.getPhoneNo())
                .child("prescriptions").
                child(cPrescription.getPrescriptionID()).
                removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    prescriptionList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePrescription(Prescription cPrescription) {
        AddPrescriptionFragment addPrescriptionFragment = new AddPrescriptionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("prescriptionID", cPrescription.getPrescriptionID());
        bundle.putString("prescriptionImage", cPrescription.getPrescriptionImage());
        bundle.putString("doctorName",cPrescription.getDoctorName());
        bundle.putString("doctorDesignation",cPrescription.getDoctorDesignation());
        bundle.putString("prescribeDate",cPrescription.getPrescribeDate());
        addPrescriptionFragment.setArguments(bundle);

        AppCompatActivity activity = (AppCompatActivity) getView.getContext();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutID, addPrescriptionFragment).addToBackStack(null).commit();
    }
}
