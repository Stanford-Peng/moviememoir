package com.example.moviememoir.Fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.example.moviememoir.R;
import com.example.moviememoir.adaptor.SearchRecycleAdaptor;
import com.example.moviememoir.model.SearchedMovie;
import com.example.moviememoir.networkconnection.NetworkConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchFragment extends Fragment {
    NetworkConnection networkConnection = null;
    //String[] images = new String[3];
    List<String> links = new ArrayList<String>();
    private RecyclerView recyclerView;
    private LayoutManager layoutManager;
    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        final EditText inputMovie = view.findViewById(R.id.editText);

        Button searchBtn = view.findViewById(R.id.search);
        networkConnection = new NetworkConnection();
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputMovie.getText().toString().trim().toLowerCase();
                Log.i("input",input);
                if(!input.isEmpty()){
                    SearchTask searchTask = new SearchTask();
                    searchTask.execute(new String[]{input});
                }
                else{
                    Toast.makeText(getActivity(), "Please input a movie first", Toast.LENGTH_SHORT).show();
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        });
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        return view;
    }

    private class SearchTask extends AsyncTask<String, Void, ArrayList<SearchedMovie>>
    {

        @Override
        protected ArrayList<SearchedMovie> doInBackground(String... strings) {
            //Log.i("strings",strings[0]);
            ArrayList<SearchedMovie> results = new ArrayList<SearchedMovie>();
            String res = networkConnection.searchMovie(strings[0]);
            if (!(res.contains("\"status_code\": 34") ||res.isEmpty()||res==null) ){
                JsonElement jsonElement = new JsonParser().parse(res);
            JsonObject jobject = jsonElement.getAsJsonObject();
            JsonArray items = jobject.getAsJsonArray("items");
            for (int i = 0; i < items.size(); i++) {
                JsonElement e0= items.get(i).getAsJsonObject().getAsJsonPrimitive("title");
                JsonElement e1 = items.get(i).getAsJsonObject().getAsJsonObject("pagemap");
                JsonArray e2 = items.get(i).getAsJsonObject().getAsJsonObject("pagemap").getAsJsonArray("cse_thumbnail");
                JsonArray e3 = items.get(i).getAsJsonObject().getAsJsonObject("pagemap").getAsJsonArray("metatags");
                JsonElement e4 = items.get(i).getAsJsonObject().getAsJsonObject("pagemap").getAsJsonArray("metatags").get(0).getAsJsonObject().getAsJsonPrimitive("pageid");
                if(!(e0==null||e1==null||e2==null||e3==null||e4==null)) {
                    String movie = e0.getAsString();
                    String image = e2.get(0).getAsJsonObject().getAsJsonPrimitive("src").getAsString();
                    String link = e4.getAsString();

                URL url = null;
                try {
                    url = new URL(image);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    SearchedMovie searchedMovie = new SearchedMovie(movie, bmp, link);
                    results.add(searchedMovie);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }


            }
        }
            //Log.i("link", links.get(0));
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<SearchedMovie> al) {

           // recyclerView = getView().findViewById(R.id.recyclerView);
            SearchRecycleAdaptor adaptor = new SearchRecycleAdaptor(al, getContext());
            //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(adaptor);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);


        }
    }

}

//            String[] movies = new String[3];
//            Bitmap[] bmps = new Bitmap[3];
//            Log.i("search", movies[0]);
//            Log.i("image", images[0]);
//                    bmps[i] = bmp;
//                    imageViews[i].setImageBitmap(bmp);
//            String[] movies = hm.keySet().iterator().next();
//            Bitmap[] bmps = hm.values().iterator().next();

//            TextView movie1 = getView().findViewById(R.id.movie1);
//            TextView movie2 = getView().findViewById(R.id.movie2);
//            TextView movie3 = getView().findViewById(R.id.movie3);
//            TextView[] textViews = new TextView[]{movie1,movie2,movie3};

//            ImageView image1 = getView().findViewById(R.id.image1);
//            ImageView image2 = getView().findViewById(R.id.image2);
//            ImageView image3 = getView().findViewById(R.id.image3);
//            ImageView[] imageViews = new ImageView[]{image1,image2,image3};
//            for(int i=0; i<movies.length;i++)
//            {
//                textViews[i].setText(movies[i]);
//            }
//            for (int i=0; i<bmps.length;i++){
//                imageViews[i].setImageBitmap(bmps[i]);
//
//            }
//            for (int i=0; i<images.length;i++) {
//                URL url = null;
//                try {
//                    url = new URL(images[i]);
//                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                    bmps[i] = bmp;
////                    imageViews[i].setImageBitmap(bmp);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

//            movie1.setText(movies[0]);
//            movie2.setText(movies[1]);
//            movie3.setText(movies[2]);

//            ImageView image1 = getView().findViewById(R.id.image1);
//            ImageView image2 = getView().findViewById(R.id.image2);
//            ImageView image3 = getView().findViewById(R.id.image3);
//            ImageView[] imageViews = new ImageView[]{image1,image2,image3};
//            for (int i=0; i<images.length;i++) {
//                URL url = null;
//                try {
//                    url = new URL(images[i]);
//                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                    imageViews[i].setImageBitmap(bmp);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

//TextView results = getView().findViewById(R.id.results);
//results.setText(s);

//                map.put("movie", movies[0]);
//                        map.put("image", bmp);
//            ListView movieList = getView().findViewById(R.id.listView);
//            ArrayList movieListArray = new ArrayList<HashMap<String, Bitmap>>();
//            HashMap<String,Bitmap> map = new HashMap<String,Bitmap>();
//            HashMap<String,Bitmap> map1 = new HashMap<String,Bitmap>();
//            HashMap<String,Bitmap> map2 = new HashMap<String,Bitmap>();
//            map.put("movie", movies[0]);
//            movieListArray.add(map);
//            map.put("movie", movies[1]);
//            map.put("image", images[1]);
//            movieListArray.add(map);
//            map.put("movie", movies[2]);
//            map.put("image", images[2]);
//            movieListArray.add(map);
//            String[] colHEAD = new String[] {"Movie","Image"};
//            int[] dataCell = new int[] {R.id.movie,R.id.image};
//            SimpleAdapter listAdapter = new SimpleAdapter(getContext(), movieListArray, R.layout.movie_list, colHEAD, dataCell);
//            movieList.setAdapter(listAdapter);