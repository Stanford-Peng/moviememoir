package com.example.moviememoir.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Movie {
    @PrimaryKey(autoGenerate = true)
    public int mid;
    @ColumnInfo(name="link")
    public String link;
    @ColumnInfo(name="name")
    private String name;
    @ColumnInfo(name="release_date")
    private String releasedDate;
    @ColumnInfo(name="add_date")
    private String addDate;
    @ColumnInfo(name="user_name")
    private String username;

    public Movie(String link ,String name, String releasedDate, String addDate, String username) {
        this.link = link;
        this.name = name;
        this.releasedDate = releasedDate;
        this.addDate = addDate;
        this.username=username;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String Link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
