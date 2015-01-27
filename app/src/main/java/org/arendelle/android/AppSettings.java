package org.arendelle.android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AppSettings extends ActionBarActivity {

    // gui objects

    // app settings
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_settings_toolbar);
        setSupportActionBar(toolbar);

        // get gui objects

        // set title
        setTitle(R.string.app_settings_title);

        // activate back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load current settings
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // back button in action bar pressed
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        // save all settings
        SharedPreferences.Editor editor = prefs.edit();
        editor.apply();

    }

}
