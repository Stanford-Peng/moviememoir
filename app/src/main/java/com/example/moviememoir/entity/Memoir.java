package com.example.moviememoir.entity;

import java.util.Date;

public class Memoir {
    private String MName;
    private float MRatingScore;
    private Date MReleaseDate;
    private Date MWatchDate;
    private Date MWatchTime;
    private String MComment;
    private Person PId;
    private Cinema CId;

    //added




    public Memoir(String MName, float MRatingScore, Date MReleaseDate, Date MWatchDate, Date MWatchTime, String MComment, Person PId, Cinema CId) {
        this.MName = MName;
        this.MRatingScore = MRatingScore;
        this.MReleaseDate = MReleaseDate;
        this.MWatchDate = MWatchDate;
        this.MWatchTime = MWatchTime;
        this.MComment = MComment;
        this.CId = CId;
        this.PId = PId;
    }

    public String getMName() {
        return MName;
    }

    public void setMName(String MName) {
        this.MName = MName;
    }

    public float getMRatingScore() {
        return MRatingScore;
    }

    public void setMRatingScore(float MRatingScore) {
        this.MRatingScore = MRatingScore;
    }

    public Date getMReleaseDate() {
        return MReleaseDate;
    }

    public void setMReleaseDate(Date MReleaseDate) {
        this.MReleaseDate = MReleaseDate;
    }

    public Date getMWatchDate() {
        return MWatchDate;
    }

    public void setMWatchDate(Date MWatchDate) {
        this.MWatchDate = MWatchDate;
    }

    public Date getMWatchTime() {
        return MWatchTime;
    }

    public void setMWatchTime(Date MWatchTime) {
        this.MWatchTime = MWatchTime;
    }

    public String getMComment() {
        return MComment;
    }

    public void setMComment(String MComment) {
        this.MComment = MComment;
    }

    public Cinema getCId() {
        return CId;
    }

    public void setCId(Cinema CId) {
        this.CId = CId;
    }

    public Person getPId() {
        return PId;
    }

    public void setPId(Person PId) {
        this.PId = PId;
    }
}
