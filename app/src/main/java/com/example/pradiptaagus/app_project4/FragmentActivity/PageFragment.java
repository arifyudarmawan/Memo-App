package com.example.pradiptaagus.app_project4.FragmentActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pradiptaagus.app_project4.R;


public class PageFragment extends Fragment {
    private String title;
    private static final String ARG_TITLE = "title";

    public PageFragment() {
        // Required empty public constructor
    }

    public static PageFragment newInstance(String title) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_page, container, false);
        TextView textView = v.findViewById(R.id.title);
        textView.setText(title);

        return v;
    }
}
