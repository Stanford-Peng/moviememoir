package com.example.moviememoir.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.moviememoir.dao.movieDAO;
import com.example.moviememoir.database.movieDB;
import com.example.moviememoir.entity.Movie;

import java.util.List;

public class movieRepository {

    private movieDAO dao;
    private LiveData<List<Movie>> allMovies;
    private Movie movie;

    public movieRepository(Application application) {
        movieDB db = movieDB.getInstance(application);
        dao = db.movieDAO();

    }

    public LiveData<List<Movie>> getAllMovies() {
        allMovies = dao.getAll();
        return allMovies;
    }

    public LiveData<List<Movie>> getAllForUser(String username) {
        allMovies = dao.getAllForUser(username);
        return allMovies;
    }


    public void insert(final Movie movie) {
        movieDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insert(movie);
            }
        });
    }

    public void deleteAll() {
        movieDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteAll();
            }
        });
    }

    public void delete(final Movie movie) {
        movieDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(movie);
            }
        });
    }

    public void insertAll(final Movie... movies) {

        movieDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insertAll(movies);
            }
        });
    }

    public void updateMovies(final Movie... movies) {
        movieDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.updateCustomers(movies);
            }
        });
    }

    public Movie findByID(final int mId) {
        Runnable runnable =  new Runnable() {
            @Override
            public synchronized void run() {
                Movie movie = dao.findByID(mId);
                setMovie(movie);
                this.notify();
            }
        };

        synchronized (runnable) {//lock object and make it monitor
            try {
                movieDB.databaseWriteExecutor.execute(runnable);
                runnable.wait();//thread then wait for the object/release the lock of r until notify
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return movie;
    }

    public Movie findByLink(final String link) {
        Runnable runnable =  new Runnable() {
            @Override
            public synchronized void run() {
                Movie movie = dao.findByLink(link);
                setMovie(movie);
                this.notify();
            }
        };

        synchronized (runnable) {//lock object and make it monitor
            try {
                movieDB.databaseWriteExecutor.execute(runnable);
                runnable.wait();//thread then wait for the object/release the lock of r until notify
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return movie;
    }

    public Movie findByNameReleaseDate(final String mName, final String releaseDate){
        Runnable runnable = new Runnable() {
            @Override
            public synchronized void run() {
                Movie movie = dao.findByNameReleaseDate(mName,releaseDate);
                setMovie(movie);
                this.notify();
            }
        };

        synchronized (runnable){
            try{
                movieDB.databaseWriteExecutor.execute(runnable);
                runnable.wait();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return movie;
    }

    public Movie findByNameReleaseDateUser(final String mName, final String releaseDate, final String userName){
        Runnable runnable = new Runnable() {
            @Override
            public synchronized void run() {
                Movie movie = dao.findByNameReleaseDateUser(mName,releaseDate, userName);
                setMovie(movie);
                this.notify();
            }
        };

        synchronized (runnable){
            try{
                movieDB.databaseWriteExecutor.execute(runnable);
                runnable.wait();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return movie;
    }

    public void deleteByNameDateUser(final String mName, final String mDate, final String userName){

        movieDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public synchronized void run() {
                dao.deleteByNameDateUser(mName,mDate, userName);
                this.notify();

            }
        });
        //        Runnable runnable = new Runnable() {
//            @Override
//            public synchronized void run() {
//                dao.deleteByNameDateUser(mName,mDate, userName);
//                this.notify();
//
//            }
//        };
//        synchronized (runnable){
//            try{
//                movieDB.databaseWriteExecutor.execute(runnable);
//                runnable.wait();
//            }catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }



}
