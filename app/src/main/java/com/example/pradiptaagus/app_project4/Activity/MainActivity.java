package com.example.pradiptaagus.app_project4.Activity;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.example.pradiptaagus.app_project4.Adapter.ViewPagerAdapter;
import com.example.pradiptaagus.app_project4.FragmentActivity.HomeFragment;
import com.example.pradiptaagus.app_project4.FragmentActivity.PageFragment;
import com.example.pradiptaagus.app_project4.FragmentActivity.ProfilFragment;
import com.example.pradiptaagus.app_project4.R;

public class MainActivity extends AppCompatActivity {

    Fragment fragment;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);

        setupToolbar();
        init();
    }

    private void init() {
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(ProfilFragment.newInstance(), "Profil");
        adapter.addFragment(HomeFragment.newInstance(), "Home");
        adapter.addFragment(PageFragment.newInstance("Tiga"), "Tiga");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu to toolbar
//        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
