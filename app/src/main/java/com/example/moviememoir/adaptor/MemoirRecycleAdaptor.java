package com.example.moviememoir.adaptor;

import android.content.Context;
import android.graphics.Paint;
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
import com.example.moviememoir.model.CachedMemoir;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MemoirRecycleAdaptor extends RecyclerView.Adapter<MemoirRecycleAdaptor.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder should contain variables for all the views in each row of the list
        public TextView movie;
        public ImageView image;
        public TextView comment;
        public TextView releaseDate;
        public TextView addTime;
        // a constructor that accepts the entire View (itemView)
        // provides a reference and access to all the views in each row
        public ViewHolder(View itemView) {
            super(itemView);
            movie = itemView.findViewById(R.id.movieTitle);
            image=itemView.findViewById(R.id.imageMemoir);
            comment = itemView.findViewById(R.id.Comment);
            releaseDate = itemView.findViewById(R.id.releaseDate);
            addTime = itemView.findViewById(R.id.addTime);
        }

    }

    private List<CachedMemoir> cachedMemoirs;
    private Context context;

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
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemoirRecycleAdaptor.ViewHolder holder, int position) {
        final CachedMemoir cachedMemoir = cachedMemoirs.get(position);
        TextView movie = holder.movie;
        ImageView image = holder.image;
        TextView comment = holder.comment;
        TextView releaseDate = holder.releaseDate;
        TextView addTime= holder.addTime;
        movie.setText(cachedMemoir.getMemoirName());
        Picasso.get().load(cachedMemoir.getImageLink()).placeholder(R.mipmap.ic_launcher).into(image);
        comment.setText(cachedMemoir.getMemoirComment());
        releaseDate.setText(cachedMemoir.getReleaseDate());
        addTime.setText(cachedMemoir.getWatchDate()+ " " +cachedMemoir.getWatchTime());
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
        return cachedMemoirs.size();
    }
}
