package com.zeoflow.test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zeoflow.zson.Zson;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Zson zson = new Zson();
        zson.newBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}