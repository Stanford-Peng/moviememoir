package com.example.moviememoir.adaptor;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviememoir.Fragment.MovieView;

import com.example.moviememoir.R;
import com.example.moviememoir.model.CachedMemoir;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MemoirRecycleAdaptor extends RecyclerView.Adapter<MemoirRecycleAdaptor.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder should contain variables for all the views in each row of the list
        public TextView movie;
        public ImageView image;
        public TextView comment;
        public TextView releaseDate;
        public TextView addTime;
        public RatingBar rating;
        // a constructor that accepts the entire View (itemView)
        // provides a reference and access to all the views in each row
        public ViewHolder(View itemView) {
            super(itemView);
            movie = itemView.findViewById(R.id.movieTitle);
            image=itemView.findViewById(R.id.imageMemoir);
            comment = itemView.findViewById(R.id.Comment);
            releaseDate = itemView.findViewById(R.id.releaseDate);
            addTime = itemView.findViewById(R.id.addTime);
            rating = itemView.findViewById(R.id.ratingBar);
        }

    }

    private List<CachedMemoir> cachedMemoirs;
    private Context context;
    private String sort ="";
    private String filter ="Default";
    private String ratingControl = "user" ;
    //private int extraView = 0;
    private List<CachedMemoir> sortedMemoirs= new ArrayList<CachedMemoir>();


//    public MemoirRecycleAdaptor(List<CachedMemoir> cachedMemoirs, Context context, String sort) {
//        this.cachedMemoirs = cachedMemoirs;
//        this.context = context;
//        this.sort = sort;
//    }

    public MemoirRecycleAdaptor(List<CachedMemoir> cachedMemoirs, Context context, String sort, String filter) {
        this.cachedMemoirs = cachedMemoirs;
        this.context = context;
        this.sort = sort;
        this.filter = filter;
    }

    public MemoirRecycleAdaptor(List<CachedMemoir> cachedMemoirs, Context context) {
        this.cachedMemoirs = cachedMemoirs;
        this.context=context;
    }

    public void addCachedMemoir(List<CachedMemoir> cachedMemoirs){
        this.cachedMemoirs = cachedMemoirs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemoirRecycleAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View memoirView = inflater.inflate(R.layout.memoir_card, parent,false);
        ViewHolder viewholder = new ViewHolder(memoirView);
//        if(filter.equals("Default")){
//            sortedMemoirs=cachedMemoirs;
//        }else {
//            sortedMemoirs.clear();
//            for (CachedMemoir cm: cachedMemoirs) {
//                if(Arrays.asList(cm.getGenres()).toString().contains(filter)) {
//                    sortedMemoirs.add(cm);
//                }
//            }
//        }

        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemoirRecycleAdaptor.ViewHolder holder, int position) {

        switch(sort){
            case "Watch Date":
            Collections.sort(sortedMemoirs, new Comparator<CachedMemoir>() {
                @Override
                public int compare(CachedMemoir o1, CachedMemoir o2) {
                    return o1.getWatchDate().compareTo(o2.getWatchDate());
                }
            });
            setRatingControl("user");
            break;
            case "User Rating":
            Collections.sort(sortedMemoirs, new Comparator<CachedMemoir>() {
                @Override
                public int compare(CachedMemoir o1, CachedMemoir o2) {
                    return o1.getUserRating().compareTo(o2.getUserRating());
                }
            });
            setRatingControl("user");
            break;
            case "Public Rating":
            Collections.sort(sortedMemoirs, new Comparator<CachedMemoir>() {
                    @Override
                    public int compare(CachedMemoir o1, CachedMemoir o2) {
                        return o1.getPublicRating().compareTo(o2.getPublicRating());
                    }
                });
            setRatingControl("public");
            break;
            default:
                break;
        }
        final CachedMemoir cachedMemoir = sortedMemoirs.get(position);

//        if(filter.equals("Default") || Arrays.asList(cachedMemoir.getGenres()).contains(filter)) {
            TextView movie = holder.movie;
            ImageView image = holder.image;
            TextView comment = holder.comment;
            TextView releaseDate = holder.releaseDate;
            TextView addTime = holder.addTime;
            RatingBar ratingBar = holder.rating;
            movie.setText(cachedMemoir.getMemoirName());
            Picasso.get().load(cachedMemoir.getImageLink()).placeholder(R.mipmap.ic_launcher).into(image);
            comment.setText(cachedMemoir.getMemoirComment());
            releaseDate.setText(cachedMemoir.getReleaseDate());
            addTime.setText(cachedMemoir.getWatchDate() + " " + cachedMemoir.getWatchTime());

            if (ratingControl == "public") {
                ratingBar.setRating(Float.valueOf(cachedMemoir.getPublicRating()) / 2);
            } else {
                ratingBar.setRating(Float.valueOf(cachedMemoir.getUserRating()));
            }
            ratingBar.setIsIndicator(true);
            if (cachedMemoir.getMdbLink().startsWith("tt")) {
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment MovieView = new MovieView(cachedMemoir.getMdbLink());
                        fragmentTransaction.replace(R.id.content_frame, MovieView);
                        fragmentTransaction.commit();
                    }
                };
                movie.setOnClickListener(listener);
                image.setOnClickListener(listener);
                movie.setPaintFlags(movie.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }



    }

    @Override
    public int getItemCount() {
//        sortedMemoirs = new ArrayList<>();
//        if(filter.equals("Default")){
//            sortedMemoirs=cachedMemoirs;
//        }else {
//            for (CachedMemoir cm: cachedMemoirs) {
//                if(cm.getGenres().toString().contains(filter)) {
//                    sortedMemoirs.add(cm);
//                }
//            }
//        }

        if(filter.equals("Default")){
            sortedMemoirs=cachedMemoirs;
        }else {
            sortedMemoirs.clear();
            for (CachedMemoir cm: cachedMemoirs) {
                if(Arrays.asList(cm.getGenres()).toString().contains(filter)) {
                    sortedMemoirs.add(cm);
                }
            }
        }
//        Log.i("filter",filter);
//        Log.i("sorted",sortedMemoirs.toString());
        return sortedMemoirs.size();
    }

    public void setRatingControl(String ratingControl) {
        this.ratingControl = ratingControl;
    }

}

//        if(sort.equals("Watch Date")){
//            Collections.sort(cachedMemoirs, new Comparator<CachedMemoir>() {
//                @Override
//                public int compare(CachedMemoir o1, CachedMemoir o2) {
//                    return 0;
//                }
//            });
//        }

//        List<CachedMemoir> sortedMemoirs = new ArrayList<CachedMemoir>();
//        if(filter.equals("Default")){
//            sortedMemoirs=cachedMemoirs;
//        }else {
//            for (CachedMemoir cm: cachedMemoirs) {
//                if(Arrays.asList(cm.getGenres()).toString().contains(filter)) {
//                    sortedMemoirs.add(cm);
//                }
//            }
//        }