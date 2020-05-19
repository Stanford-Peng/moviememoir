package com.example.moviememoir.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.moviememoir.entity.Movie;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface movieDAO {
    @Query("SELECT * FROM Movie")
    LiveData<List<Movie>> getAll();

    @Query("SELECT * FROM MOVIE WHERE user_name = :username")
    LiveData<List<Movie>> getAllForUser( String username);

    @Query("SELECT * FROM Movie WHERE mid = :mId LIMIT 1")
    Movie findByID(int mId);

    @Query("SELECT * FROM Movie WHERE link = :mLink")
    Movie findByLink(String mLink);

    @Query("SELECT * FROM Movie WHERE name = :mName and release_date = :releaseDate")
    Movie findByNameReleaseDate(String mName, String releaseDate);

    @Query("SELECT * FROM Movie WHERE name = :mName and release_date = :releaseDate and user_name= :useName")
    Movie findByNameReleaseDateUser(String mName, String releaseDate, String useName);

    @Insert
    void insertAll(Movie... movies);

    @Insert
    void  insert(Movie movie);

    @Delete
    void delete(Movie movie);

    @Update(onConflict = REPLACE)
    void updateCustomers(Movie... movies);

    @Query("DELETE FROM Movie")
    void deleteAll();

    @Query("DELETE FROM Movie WHERE name = :mName and release_date = :mDate and user_name = :userName")
    void deleteByNameDateUser(String mName, String mDate, String userName);

}
