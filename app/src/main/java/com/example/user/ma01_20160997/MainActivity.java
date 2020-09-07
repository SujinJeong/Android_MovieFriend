package com.example.user.ma01_20160997;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonMovie:
                Intent intent=new Intent(MainActivity.this,SearchMovie.class);
                startActivity(intent);
                break;
            case R.id.buttonNearby:
                intent = new Intent(MainActivity.this, SearchMovieTheater.class);
                startActivity(intent);
                break;
            case R.id.buttonRank:
                intent = new Intent(MainActivity.this, SearchMovieRank.class);
                startActivity(intent);
                break;

        }

    }




}