package com.example.moviememoir.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviememoir.R;
import com.example.moviememoir.entity.Movie;
import com.example.moviememoir.viewmodel.MovieViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WatchListFragment extends Fragment {

    MovieViewModel movieViewModel;
    List<HashMap<String, String>> watchListArray;
    SimpleAdapter myListAdapter;
    String[] colHEAD = new String[] {"MOVIE","RELEASE DATE","ADDED DATE"};
    int[] dataCell = new int[] {R.id.mName,R.id.mReleaseDate,R.id.mAddDate};
    String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.watchlist_fragment, container, false);
        watchListArray = new ArrayList<HashMap<String,String>>();
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        movieViewModel.initializeVars(getActivity().getApplication());

        final ListView watchList = view.findViewById(R.id.watchList);

        movieViewModel.getAllMovies();
        SharedPreferences shared = getActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE);

        username = shared.getString("username", null);

        movieViewModel.getAllForUser(username).observe(getViewLifecycleOwner(), new Observer<List<Movie>>() {

            @Override
            public void onChanged(final List<Movie> movies) {
                watchListArray.clear();
                //movieViewModel.initializeVars(getActivity().getApplication());
                for (Movie m :  movies) {
                   HashMap<String, String> temp = new HashMap<>();
                   temp.put("MOVIE", m.getName());
                   temp.put("RELEASE DATE", m.getReleasedDate());
                   temp.put("ADDED DATE", m.getAddDate());
                   watchListArray.add(temp);
                }
                myListAdapter = new customedAdaptor(getActivity(), watchListArray, R.layout.watch_list_view, colHEAD, dataCell);
                watchList.setAdapter(myListAdapter);

            }

        });

//myListAdapter.getView();

//watchList.
//        watchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                final Adapter adp = parent.getAdapter();
//                ImageView delete = view.findViewById(R.id.iv_item_delete);
//                TextView name = view.findViewById(R.id.mName);
//                TextView date = view.findViewById(R.id.mReleaseDate);
                //delete.setOnClickListener();
//            ImageView imageView = convertView.findViewById(R.id.iv_item_delete);
//            TextView name = convertView.findViewById(R.id.mName);
//            TextView date = convertView.findViewById(R.id.mReleaseDate);
//            final String mName = name.getText().toString();
//            final String mDate = date.getText().toString();
//            Log.i("mName",mName);
//            delete.setOnClickListener(
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            movieViewModel.deleteByNameDate(mName,mDate);
//                            adp.notifyDataSetChanged();
//                        }
//                    }
//            );
//           }
//        });






        return view;

    };

    private  class customedAdaptor extends SimpleAdapter {


        public customedAdaptor(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //final Adapter = convertView.getAdaptor();
            //final RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder();
            //viewHolder.textView1 = (TextView) view.findViewById(R.id.text1);
            convertView=super.getView(position, convertView, parent);
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.watch_list_view, null);

           }
            ImageView imageView = convertView.findViewById(R.id.iv_item_delete);
            TextView name = convertView.findViewById(R.id.mName);
            TextView date = convertView.findViewById(R.id.mReleaseDate);
            final String mName = name.getText().toString();
            final String mDate = date.getText().toString();
            //holder = new ViewHolder();
            Log.i("mName",mName);
            imageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            movieViewModel.deleteByNameDateUser(mName,mDate,username);
                            notifyDataSetChanged();
                        }
                    }
            );

            return convertView;
        }

        @Override
        public ViewBinder getViewBinder() {

            return super.getViewBinder();
        }
    }


}
