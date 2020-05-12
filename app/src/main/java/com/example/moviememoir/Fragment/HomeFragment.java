package com.example.moviememoir.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moviememoir.R;

public class HomeFragment extends Fragment {

    public HomeFragment( ) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        TextView textView=view.findViewById(R.id.tv);
        String firstName = getArguments().getString("firstName");
        textView.setText("Welcome!" + firstName);
        return view;
    }
}