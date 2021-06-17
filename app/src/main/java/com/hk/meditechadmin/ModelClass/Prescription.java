package com.hk.meditechadmin.ModelClass;

public class Prescription {
    private String doctorName, doctorDesignation, prescribeDate,prescriptionImage,prescriptionID;

    public Prescription() {
    }

    public Prescription(String doctorName, String doctorDesignation, String prescribeDate, String prescriptionImage,String prescriptionID) {
        this.doctorName = doctorName;
        this.doctorDesignation = doctorDesignation;
        this.prescribeDate = prescribeDate;
        this.prescriptionImage = prescriptionImage;
        this.prescriptionID = prescriptionID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorDesignation() {
        return doctorDesignation;
    }

    public void setDoctorDesignation(String doctorDesignation) {
        this.doctorDesignation = doctorDesignation;
    }

    public String getPrescribeDate() {
        return prescribeDate;
    }

    public void setPrescribeDate(String prescribeDate) {
        this.prescribeDate = prescribeDate;
    }

    public String getPrescriptionImage() {
        return prescriptionImage;
    }

    public void setPrescriptionImage(String prescriptionImage) {
        this.prescriptionImage = prescriptionImage;
    }

    public String getPrescriptionID() {
        return prescriptionID;
    }

    public void setPrescriptionID(String prescriptionID) {
        this.prescriptionID = prescriptionID;
    }
}
