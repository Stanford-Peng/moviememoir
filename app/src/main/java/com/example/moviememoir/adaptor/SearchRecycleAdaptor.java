package com.example.moviememoir.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviememoir.Fragment.MovieView;

import com.example.moviememoir.R;
import com.example.moviememoir.model.SearchedMovie;

import java.util.List;

public class SearchRecycleAdaptor extends RecyclerView.Adapter<SearchRecycleAdaptor.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder should contain variables for all the views in each row of the list
        public TextView movie;
        public ImageView image;
        // a constructor that accepts the entire View (itemView)
        // provides a reference and access to all the views in each row
        public ViewHolder(View itemView) {
            super(itemView);
            movie = itemView.findViewById(R.id.movie);
            image=itemView.findViewById(R.id.image);
        }

    }

    private List<SearchedMovie> searchedMovies;
    private Context context;

    public SearchRecycleAdaptor(List<SearchedMovie> searchedMovies, Context context) {
        this.searchedMovies = searchedMovies;
        this.context=context;
    }

    public void addMovies(List<SearchedMovie> movies){
        searchedMovies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View moviesView = inflater.inflate(R.layout.movie_list, parent,false);
        ViewHolder viewholder = new ViewHolder(moviesView);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SearchedMovie searchedMovie = searchedMovies.get(position);
        if (searchedMovie.getLink().startsWith("tt")) {
            TextView movie = holder.movie;
            movie.setText(searchedMovie.getMovie());
            ImageView image = holder.image;
            image.setImageBitmap(searchedMovie.getImage());
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    ;
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment MovieView = new MovieView(searchedMovie.getLink());
                    fragmentTransaction.replace(R.id.content_frame, MovieView);
                    fragmentTransaction.commit();
                }
            };
            movie.setOnClickListener(listener);
            image.setOnClickListener(listener);
        }


    }

    @Override
    public int getItemCount() {
        return searchedMovies.size();
    }




}
