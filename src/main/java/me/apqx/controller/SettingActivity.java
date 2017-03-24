package me.apqx.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import me.apqx.controller.views.MyGridLayout;

/**
 * Created by apqx on 2017/3/18.
 */

public class SettingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MyGridLayout myGridLayout;
    private SharedPreferences sharedPreferences;
    private int velocity,time;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

        toolbar=(Toolbar)findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View view= LayoutInflater.from(this).inflate(R.layout.layout_main,null);
        myGridLayout=(MyGridLayout)view.findViewById(R.id.myGridLayout);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        onSharedPreferenceChangeListener=new MyPreferenceChangeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private class MyPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("velocity")){
                velocity=Integer.parseInt(sharedPreferences.getString("velocity","0"));
                myGridLayout.setVelocity(velocity);
            }else if (key.equals("time")){
                time=Integer.parseInt(sharedPreferences.getString("time","0"));
                myGridLayout.setTime(time);
            }
        }
    }
}
