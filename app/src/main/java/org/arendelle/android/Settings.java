package org.arendelle.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Settings extends ActionBarActivity {

    // gui objects
    private Spinner mainFunctionSpinner;
    private Spinner colorPaletteSpinner;

    /** project folder */
    private File projectFolder;

    /** config file */
    private File configFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        // get gui objects
        mainFunctionSpinner = (Spinner) findViewById(R.id.settings_main_function_spinner);
        colorPaletteSpinner = (Spinner) findViewById(R.id.settings_color_palette_spinner);

        // set title
        setTitle(R.string.settings_title);

        // activate back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get project folder
        projectFolder = new File(getIntent().getExtras().getString("projectFolder"));

        // get config file
        configFile = new File(projectFolder, "project.config");

        // get all functions for main function spinner
        ArrayList<String> filesList=new ArrayList<String>();
        ArrayList<File> files=new ArrayList<File>();
        Files.getFiles(projectFolder, files);
        for (File f : files) {
            if (f.getName().contains(".arendelle")) {
                String name = Files.getRelativePath(projectFolder, f).replace('/', '.').split(".arendelle")[0];
                filesList.add(name);
            }
        }
        Collections.sort(filesList);

        // fill main function spinner with items
        ArrayAdapter filesListAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, filesList);
        filesListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainFunctionSpinner.setAdapter(filesListAdapter);

        // select current main function
        try {
            File mainFunction = new File(projectFolder, Files.parseConfigFile(configFile).get("mainFunction"));
            String mainFunctionPath = Files.getRelativePath(projectFolder, mainFunction).replace('/', '.').split(".arendelle")[0];
            mainFunctionSpinner.setSelection(filesList.indexOf(mainFunctionPath));
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            mainFunctionSpinner.setSelection(0);
        }

        // fill color palette spinner with items
        ArrayAdapter colorPalettesAdapter = ArrayAdapter.createFromResource(this, R.array.color_palettes, android.R.layout.simple_spinner_item);
        colorPalettesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorPaletteSpinner.setAdapter(colorPalettesAdapter);

        // select current color palette
        try {
            colorPaletteSpinner.setSelection(Integer.valueOf(Files.parseConfigFile(configFile).get("colorPalette")));
        } catch (Exception e) {
            colorPaletteSpinner.setSelection(0);
        }

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
        try {
            HashMap<String, String> properties = Files.parseConfigFile(configFile);

            // main function
            properties.put("mainFunction", mainFunctionSpinner.getSelectedItem().toString().replace('.', '/') + ".arendelle");

            // color palette
            properties.put("colorPalette", String.valueOf(colorPaletteSpinner.getSelectedItemPosition()));
            switch (colorPaletteSpinner.getSelectedItemPosition()) {

                // Arendelle Classic
                case 0:
                    properties.put("colorBackground", "#000000");
                    properties.put("colorFirst", "#FFFFFF");
                    properties.put("colorSecond", "#CECECE");
                    properties.put("colorThird", "#8C8A8C");
                    properties.put("colorFourth", "#424542");
                    break;

                // Sparkling Blue
                case 1:
                    properties.put("colorBackground", "#000000");
                    properties.put("colorFirst", "#49CEE6");
                    properties.put("colorSecond", "#49B3E6");
                    properties.put("colorThird", "#499EE6");
                    properties.put("colorFourth", "#4985E6");
                    break;

                // Arendelle Pink
                case 2:
                    properties.put("colorBackground", "#000000");
                    properties.put("colorFirst", "#E60087");
                    properties.put("colorSecond", "#B800AD");
                    properties.put("colorThird", "#8E00D7");
                    properties.put("colorFourth", "#6600FF");
                    break;

                // Simple Red
                case 3:
                    properties.put("colorBackground", "#FFFFFF");
                    properties.put("colorFirst", "#E70D20");
                    properties.put("colorSecond", "#EC444B");
                    properties.put("colorThird", "#F17E81");
                    properties.put("colorFourth", "#F7BBBE");
                    break;

                // White Legacy
                case 4:
                    properties.put("colorBackground", "#EAEAEA");
                    properties.put("colorFirst", "#030303");
                    properties.put("colorSecond", "#313131");
                    properties.put("colorThird", "#6D6D6D");
                    properties.put("colorFourth", "#B3B3B3");
                    break;

            }

            Files.createConfigFile(configFile, properties);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }
}
