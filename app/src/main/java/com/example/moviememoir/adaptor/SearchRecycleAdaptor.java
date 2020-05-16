package com.example.moviememoir;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class SearchRecycleAdaptor {

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


}
