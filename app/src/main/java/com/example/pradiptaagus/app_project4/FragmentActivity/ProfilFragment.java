package com.example.pradiptaagus.app_project4.FragmentActivity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pradiptaagus.app_project4.R;

public class ProfilFragment extends Fragment {

    public static ProfilFragment newInstance() {
        return new ProfilFragment();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_profil, container, false);
        return view;


    }
}
