package com.example.moviememoir.entity;

import java.util.Date;

public class Credentials {
    private String crUsername;
    private String crPwdHash;
    private Date crSignupDate;
    private Person PId;

    public Credentials(String crUsername, String crPwdHash, Date crSignupDate) {
        this.crUsername = crUsername;
        this.crPwdHash = crPwdHash;
        this.crSignupDate = crSignupDate;
    }

    public String getCrUsername() {
        return crUsername;
    }

    public void setCrUsername(String crUsername) {
        this.crUsername = crUsername;
    }

    public String getCrPwdHash() {
        return crPwdHash;
    }

    public void setCrPwdHash(String crPwdHash) {
        this.crPwdHash = crPwdHash;
    }

    public Date getCrSignupDate() {
        return crSignupDate;
    }

    public void setCrSignupDate(Date crSignupDate) {
        this.crSignupDate = crSignupDate;
    }

    public Person getPId() {
        return PId;
    }

    public void setPId(Person PId) {
        this.PId = PId;
    }
}

//    private String p_given_name;
//    private String p_surname;
//    private String p_gender;
//    private Date p_dob;
//    private String p_address;
//    private String p_state;
//    private String p_postcode;
//public void setP_id(String p_given_name, String p_surname,String p_gender,Date p_dob, String p_address,String p_state,String p_postcode) {
//    this.p_id = new Person(String p_given_name, String p_surname,String p_gender,Date p_dob, String p_address,String p_state,String p_postcode);
//}