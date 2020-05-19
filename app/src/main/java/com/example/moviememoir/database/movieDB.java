package com.example.moviememoir.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.moviememoir.dao.movieDAO;
import com.example.moviememoir.entity.Movie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Movie.class}, version = 2, exportSchema = false)
public abstract class movieDB  extends RoomDatabase {
    private static movieDB INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract movieDAO movieDAO();

    public static synchronized movieDB getInstance(final Context context){
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    movieDB.class, "movieDB").fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;

    }

}
