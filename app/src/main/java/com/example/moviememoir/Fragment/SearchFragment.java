package com.example.moviememoir.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.moviememoir.R;
import com.example.moviememoir.networkconnection.NetworkConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchFragment extends Fragment {
    NetworkConnection networkConnection = null;
    String[] images = new String[3];
    String[] links = new String[3];
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
            }
        });

        return view;
    }

    private class SearchTask extends AsyncTask<String, Void, HashMap<String[],Bitmap[]>>
    {

        @Override
        protected HashMap<String[],Bitmap[]> doInBackground(String... strings) {
            //Log.i("strings",strings[0]);
            HashMap<String[],Bitmap[]> result = new HashMap();
            JsonElement jsonElement = new JsonParser().parse(networkConnection.searchMovie(strings[0]));
            JsonObject jobject = jsonElement.getAsJsonObject();
            JsonArray items = jobject.getAsJsonArray("items");
            String[] movies = new String[3];
            Bitmap[] bmps = new Bitmap[3];
            for (int i = 0; i < items.size();i++)
            {
                movies[i]=items.get(i).getAsJsonObject().getAsJsonPrimitive("title").getAsString();
                images[i] = items.get(i).getAsJsonObject().getAsJsonObject("pagemap").getAsJsonArray("cse_thumbnail").get(0).getAsJsonObject().getAsJsonPrimitive("src").getAsString();
                links[i] = items.get(i).getAsJsonObject().getAsJsonPrimitive("formattedUrl").getAsString();
            }
            Log.i("search", movies[0]);
            Log.i("image", images[0]);
            Log.i("link", links[0]);

            for (int i=0; i<images.length;i++) {
                URL url = null;
                try {
                    url = new URL(images[i]);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    bmps[i] = bmp;
//                    imageViews[i].setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            result.put(movies,bmps);


            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String[],Bitmap[]> hm) {
            String[] movies = hm.keySet().iterator().next();
            Bitmap[] bmps = hm.values().iterator().next();

            TextView movie1 = getView().findViewById(R.id.movie1);
            TextView movie2 = getView().findViewById(R.id.movie2);
            TextView movie3 = getView().findViewById(R.id.movie3);
            TextView[] textViews = new TextView[]{movie1,movie2,movie3};

            ImageView image1 = getView().findViewById(R.id.image1);
            ImageView image2 = getView().findViewById(R.id.image2);
            ImageView image3 = getView().findViewById(R.id.image3);
            ImageView[] imageViews = new ImageView[]{image1,image2,image3};
            for(int i=0; i<movies.length;i++)
            {
                textViews[i].setText(movies[i]);
            }
            for (int i=0; i<bmps.length;i++){
                imageViews[i].setImageBitmap(bmps[i]);

            }



        }
    }

}


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