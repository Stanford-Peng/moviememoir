package com.example.moviememoir.model;

public class CachedMemoir {
    String memoirName;
    String memoirComment ;
    String releaseDate ;
    String watchDate ;
    String watchTime ;
    String imageLink;
    String publicRating;
    String mdbLink;



    public CachedMemoir(String memoirName, String memoirComment, String releaseDate, String watchDate, String watchTime, String imageLink, String publicRating, String mdbLink) {
        this.memoirName = memoirName;
        this.memoirComment = memoirComment;
        this.releaseDate = releaseDate;
        this.watchDate = watchDate;
        this.watchTime = watchTime;
        this.imageLink = imageLink;
        this.publicRating = publicRating;
        this.mdbLink = mdbLink;
    }

    public String getMemoirName() {
        return memoirName;
    }

    public void setMemoirName(String memoirName) {
        this.memoirName = memoirName;
    }


    public String getMemoirComment() {
        return memoirComment;
    }

    public void setMemoirComment(String memoirComment) {
        this.memoirComment = memoirComment;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getWatchDate() {
        return watchDate;
    }

    public void setWatchDate(String watchDate) {
        this.watchDate = watchDate;
    }

    public String getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(String watchTime) {
        this.watchTime = watchTime;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getPublicRating() {
        return publicRating;
    }

    public void setPublicRating(String publicRating) {
        this.publicRating = publicRating;
    }

    public String getMdbLink() {
        return mdbLink;
    }

    public void setMdbLink(String mdbLink) {
        this.mdbLink = mdbLink;
    }
}
