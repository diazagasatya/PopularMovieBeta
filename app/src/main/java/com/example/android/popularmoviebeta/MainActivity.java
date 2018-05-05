package com.example.android.popularmoviebeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.popularmoviebeta.Utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL url = NetworkUtils.buildURL();

    }
}
