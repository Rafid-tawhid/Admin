package com.hk.meditechadmin.ModelClass;

public class Patient {
    private String phoneNo,name,gender,email,age,address,bloodGroup,homeNo,image,qrImage;
    public Patient() {
    }

    public Patient(String phoneNo, String name, String gender, String email, String age, String address, String bloodGroup, String homeNo, String image,String qrImage) {
        this.phoneNo = phoneNo;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.age = age;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.homeNo = homeNo;
        this.image = image;
        this.qrImage = qrImage;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getHomeNo() {
        return homeNo;
    }

    public void setHomeNo(String homeNo) {
        this.homeNo = homeNo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQrImage() {
        return qrImage;
    }

    public void setQrImage(String qrImage) {
        this.qrImage = qrImage;
    }
}
