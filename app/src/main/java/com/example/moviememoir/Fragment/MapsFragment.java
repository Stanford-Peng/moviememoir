package com.example.moviememoir.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moviememoir.R;
import com.example.moviememoir.networkconnection.NetworkConnection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    //private String postcode;
    private GoogleMap mMap;
    private NetworkConnection networkConnection;
    private List<double[]> positions;
    List<String> addresses;
    SupportMapFragment mapFragment;
//    MapView mapView;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_fragment, container, false);
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Please wait patiently");
        SharedPreferences shared = getActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE);
        String pId = shared.getString("pid", null);
        networkConnection = new NetworkConnection();
        GetPostcode getPostcode = new GetPostcode();
//        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
//                .findFragmentById(R.id.map);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        supportMapFragment.getMapAsync(this);


        getPostcode.execute(pId);



//        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);


        return view;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
//        double[] point = positions.get(1);
//        LatLng position = new LatLng(point[0],point[1]);
//        mMap.addMarker(new MarkerOptions().position(position).title("Home"));
        float zoomLevel = (float) 15.0;
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel));
        for(int i =0;i<positions.size();i++){
            double[] point = positions.get(i);
            LatLng position = new LatLng(point[0],point[1]);
            if( i == 0 ) {
                mMap.addMarker(new MarkerOptions().position(position).title("Home："+ addresses.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel));
            }
            else{
                mMap.addMarker(new MarkerOptions().position(position).title(addresses.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }
        }


        //Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//
//        mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        mMap.addMarker(new MarkerOptions().position(currentlocation).title("My Location"));
//
//        float zoomLevel = (float) 10.0;
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, zoomLevel));

    }

    public double[] getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        double[] p = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            p = new double[]{(double) (location.getLatitude()),
                    (double) (location.getLongitude())};

        }
        catch (Exception e){
            e.printStackTrace();

        }
        return p;
    }

    private class GetPostcode extends AsyncTask<String,Void,List<String>>{

        @Override
        protected void onPreExecute() {

            mProgressDialog.show();
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            addresses = new ArrayList<>();
            String rawData = networkConnection.getPersonAddress(strings[0]);
            JsonElement je = new JsonParser().parse(rawData);
            String address = je.getAsJsonObject().getAsJsonPrimitive("PAddress").getAsString() + ", " + je.getAsJsonObject().getAsJsonPrimitive("PState").getAsString() + " " +je.getAsJsonObject().getAsJsonPrimitive("PPostcode").getAsString();
            addresses.add(address);
            String cinemaRaw = networkConnection.getCinema();
            JsonElement cinemaJson = new JsonParser().parse(cinemaRaw);
            //String cinemaPostcode="";
            for(JsonElement e: cinemaJson.getAsJsonArray()){
                addresses.add(e.getAsJsonObject().getAsJsonPrimitive("CName").getAsString() + " " +e.getAsJsonObject().getAsJsonPrimitive("CPostcode").getAsString() + ", " + "Australia");

            }
            Log.i("addresses",addresses.toString());
            return addresses;
        }

        @Override
        protected void onPostExecute(List<String> s) {
        positions = new ArrayList<>();
        for(String address: s){
            if(getLocationFromAddress(address)!=null) {
                positions.add(getLocationFromAddress(address));
                //Log.i("positions",String.valueOf(getLocationFromAddress(address)[0]));
            }
        }
//        if(mapFragment==null){
//            FragmentManager fm = getActivity().getSupportFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            mapFragment = SupportMapFragment.newInstance();
//            ft.replace(R.id.map,mapFragment).commit();
//
//        }
        mapFragment.getMapAsync(MapsFragment.this);
        mProgressDialog.dismiss();

            //mapView.getMapAsync(this);
        //mapView.getMapAsync(MapsFragment.this);
        }
    }
}
