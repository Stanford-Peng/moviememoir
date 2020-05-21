package com.example.moviememoir.entity;

public class Cinema {
    private String CName;
    private String CPostcode;

    public Cinema(String CName, String CPostcode) {
        this.CName = CName;
        this.CPostcode = CPostcode;
    }

    public String getCName() {
        return CName;
    }

    public void setCName(String CName) {
        this.CName = CName;
    }

    public String getCPostcode() {
        return CPostcode;
    }

    public void setCPostcode(String CPostcode) {
        this.CPostcode = CPostcode;
    }
}
