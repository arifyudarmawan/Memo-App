package com.example.pradiptaagus.app_project4.Activity;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.example.pradiptaagus.app_project4.Adapter.ViewPagerAdapter;
import com.example.pradiptaagus.app_project4.FragmentActivity.HomeFragment;
import com.example.pradiptaagus.app_project4.FragmentActivity.PageFragment;
import com.example.pradiptaagus.app_project4.FragmentActivity.ProfilFragment;
import com.example.pradiptaagus.app_project4.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Fragment fragment;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        fab = findViewById(R.id.fab_add_memo);
        fab.setOnClickListener(this);
        fab.hide();

        setupToolbar();
        init();


        // set fragment fab show
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        fab.hide();
                        break;
                    case 1:
                        fab.show();
                        break;
                    case 2:
                        fab.hide();
                        break;
                    default:
                        fab.hide();
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_memo:
                Intent intent = new Intent(this, AddMemoActivity.class);
                startActivity(intent);
        }
    }
}
