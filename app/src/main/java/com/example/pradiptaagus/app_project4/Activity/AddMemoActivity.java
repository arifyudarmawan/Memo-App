package com.example.pradiptaagus.app_project4.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pradiptaagus.app_project4.Api.ApiInterface;
import com.example.pradiptaagus.app_project4.Fragment.HomeFragment;
import com.example.pradiptaagus.app_project4.R;

public class AddMemoActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etMemoTitle;
    private EditText etMemoDetail;
    private Button btnSave;
    private Button btnDiscard;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeFragment.class));
            }
        });

        // declare share preference
        final SharedPreferences userPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);

        // get token from shared preference
        String token = userPreference.getString("token", "missing");

        // get user input
        etMemoTitle = (EditText) findViewById(R.id.et_memo_title);
        etMemoDetail = (EditText) findViewById(R.id.et_memo_detail);

        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        btnDiscard = (Button) findViewById(R.id.btn_discard);
        btnDiscard.setOnClickListener(this);

        if (token == "missing") {
            loginActivity();
        }
    }

    private void loginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                Toast.makeText(this, "save", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_discard:
                //Toast.makeText(this, "discard", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
