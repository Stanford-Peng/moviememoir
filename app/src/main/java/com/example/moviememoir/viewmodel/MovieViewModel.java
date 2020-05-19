package com.example.moviememoir.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviememoir.entity.Movie;
import com.example.moviememoir.repository.movieRepository;

import java.util.List;

public class MovieViewModel extends ViewModel {

    private movieRepository mRepository;
    private MutableLiveData<List<Movie>> allMovies;

    public MovieViewModel() {
        this.allMovies = new MutableLiveData<>();
    }

    public void setMovies(List<Movie> movies) {
        allMovies.setValue(movies);
    }

    public LiveData<List<Movie>> getAllMovies() {
        return mRepository.getAllMovies();
    }

    public LiveData<List<Movie>> getAllForUser(String username) {
        return mRepository.getAllForUser(username);
    }

    public void initializeVars(Application application) {
        mRepository = new movieRepository(application);
    }

    public void insert(Movie movie) {
        mRepository.insert(movie);
    }

    public void insertAll(Movie... movies) {
        mRepository.insertAll(movies);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void delete(Movie movie) {
        mRepository.delete(movie);
    }

    public void update(Movie... movies) {
        mRepository.updateMovies(movies);
    }

    public Movie findByID(int uid) {
        return mRepository.findByID(uid);
    }

    public Movie findByLink(String link) {
        return mRepository.findByLink(link);
    }

    public Movie findByNameReleaseDate(String name, String releaseDate){
        return mRepository.findByNameReleaseDate(name, releaseDate);
    }

    public Movie findByNameReleaseDateUser(String name, String releaseDate, String userName){
        return mRepository.findByNameReleaseDateUser(name, releaseDate, userName);
    }

    public void deleteByNameDateUser(final String mName, final String mDate, final String userName){
        mRepository.deleteByNameDateUser(mName,mDate, userName);
    }


}
