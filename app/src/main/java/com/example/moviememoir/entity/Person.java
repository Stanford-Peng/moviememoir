package com.example.moviememoir.entity;

import java.util.Date;

public class Person {
    //private int p_id;
    private String PGivenName;
    private String PSurname;
    private Character PGender;
    private Date PDob;
    private String PAddress;
    private String PState;
    private String PPostcode;

    public Person(String PGivenName, String PSurname, Character PGender, Date PDob, String PAddress, String PState, String PPostcode) {
        this.PGivenName = PGivenName;
        this.PSurname = PSurname;
        this.PGender = PGender;
        this.PDob = PDob;
        this.PAddress = PAddress;
        this.PState = PState;
        this.PPostcode = PPostcode;
    }

    public String getPGivenName() {
        return PGivenName;
    }

    public void setPGivenName(String PGivenName) {
        this.PGivenName = PGivenName;
    }

    public String getPSurname() {
        return PSurname;
    }

    public void setPSurname(String PSurname) {
        this.PSurname = PSurname;
    }

    public Character getPGender() {
        return PGender;
    }

    public void setPGender(Character PGender) {
        this.PGender = PGender;
    }

    public Date getPDob() {
        return PDob;
    }

    public void setPDob(Date PDob) {
        this.PDob = PDob;
    }

    public String getPAddress() {
        return PAddress;
    }

    public void setPAddress(String PAddress) {
        this.PAddress = PAddress;
    }

    public String getPState() {
        return PState;
    }

    public void setPState(String PState) {
        this.PState = PState;
    }

    public String getPPostcode() {
        return PPostcode;
    }

    public void setPPostcode(String PPostcode) {
        this.PPostcode = PPostcode;
    }
}
