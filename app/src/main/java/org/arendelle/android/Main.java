package org.arendelle.android;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.arendelle.java.engine.CodeScreen;
import org.arendelle.java.engine.MasterEvaluator;
import org.arendelle.java.engine.Reporter;

public class Main extends ActionBarActivity implements OnItemClickListener, AdapterView.OnItemLongClickListener {
	
	// gui objects
	private ListView listProjects;

    // app settings
    private SharedPreferences prefs;

    // Arendelle workspace folder
    private File arendelleFolder;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
		
		// get gui objects
		listProjects = (ListView) findViewById(R.id.list_projects);
		
		// setup projects list
		listProjects.setOnItemClickListener(this);
        listProjects.setOnItemLongClickListener(this);

        // check if storage is accessible
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.toast_storage_not_accessible, Toast.LENGTH_LONG).show();
            finish();
        }

        // open Arendelle workspace folder (or create it if neccessary)
        arendelleFolder = new File(Environment.getExternalStorageDirectory() + "/Arendelle/");
        arendelleFolder.mkdir();

        // first start
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("firstStart", true)) {
            prefs.edit().putBoolean("firstStart", false).apply();

            // create example projects
            createExampleProjects();

            // open welcome dialog
            Intent intent = new Intent(this, Webview.class);
            intent.putExtra("title", getText(R.string.welcome_title));
            intent.putExtra("url", "file:///android_res/raw/welcome.html");
            startActivity(intent);

        }

        // if not touched so far -> setup book button footer
        if (prefs.getBoolean("showBookButton", true)) {

            // create preview image if necessary
            File bookButtonPreview = new File(arendelleFolder, ".bookButtonPreview.png");
            if (!bookButtonPreview.exists()) {
                try {
                    Files.saveImage(bookButtonPreview, createPreviewImage("nn[ #i / 3 + 1 , [ 2 , prd ] [ 3 , pld ] [ 5 , u ] [ 4 , r ] nnn { #n = 3 , nnn } ] id [ #i / 2 - 7 , r ] [ 3 , [ 14 , cr ] [ 14 , l ] d ]"));
                } catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            View footer = getLayoutInflater().inflate(R.layout.projects_listview_item, null);
            ((TextView) footer.findViewById(R.id.projects_listview_item_text1)).setText(R.string.action_learn);
            ((ImageView) footer.findViewById(R.id.projects_listview_item_preview)).setImageBitmap(BitmapFactory.decodeFile(bookButtonPreview.getAbsolutePath()));
            listProjects.addFooterView(footer, null, true);
        }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
		switch (item.getItemId()) {

            // new project
            case R.id.action_new:
                showNewProjectDialog();
                return true;

            // show welcome screen
            case R.id.action_basics:
                intent = new Intent(this, Webview.class);
                intent.putExtra("title", getText(R.string.action_basics));
                intent.putExtra("url", "file:///android_res/raw/welcome.html");
                startActivity(intent);
                return true;

            // learn Arendelle (book)
            case R.id.action_learn:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://web.arendelle.org/book"));
                startActivity(intent);
                return true;

            // show help
            case R.id.action_help:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://web.arendelle.org/book/getting_started/arendelle_android_app.html"));
                startActivity(intent);
                return true;

            /* open app settings
            case R.id.action_settings:
                startActivity(new Intent(this, AppSettings.class));
                return true;*/

            // rate app
            case R.id.action_rate:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=org.arendelle.android"));
                startActivity(intent);
                return true;

            // report bug
            case R.id.action_report_bug:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://reporter.arendelle.org/bug/android"));
                startActivity(intent);
                return true;

            // show about screen
            case R.id.action_about:
                intent = new Intent(this, Webview.class);
                intent.putExtra("title", getText(R.string.action_about));
                intent.putExtra("url", "file:///android_res/raw/about.html");
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

		}
		
	}
	
	/** shows dialog for new Arendelle project */
	private void showNewProjectDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_project, null);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
                // check input
                if (((EditText) dialogView.findViewById(R.id.dialog_new_project_text_name)).getText().toString().equals("")) {
                    showNewProjectDialog();
                } else {
                    newProject(((EditText) dialogView.findViewById(R.id.dialog_new_project_text_name)).getText().toString(), "main");
                }
			}
		});
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

	}
	
	/** creates new Arendelle project */
	private void newProject(String projectName, String mainFunctionName) {
		
		// create project folder
		File projectFolder = new File(Environment.getExternalStorageDirectory() + "/Arendelle/" + projectName);
		projectFolder.mkdir();
		
		// create default config file
		File configFile = new File(projectFolder, "project.config");
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("mainFunction", mainFunctionName + ".arendelle");
		properties.put("currentFunction", properties.get("mainFunction"));
        try {
            properties.put("ide", "android;" + String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
        } catch (Exception e) {
            properties.put("ide", "android;0");
        }
        properties.put("colorPalette", "0");
        properties.put("colorBackground", "#000000");
        properties.put("colorFirst", "#FFFFFF");
        properties.put("colorSecond", "#CECECE");
        properties.put("colorThird", "#8C8A8C");
        properties.put("colorFourth", "#424542");
		try {
			Files.createConfigFile(configFile, properties);
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
			return;
		}

        // create preview image
        Bitmap bitmap = Bitmap.createBitmap(getResources().getDisplayMetrics().widthPixels, 20 * (int)getResources().getDisplayMetrics().density * 5, Bitmap.Config.ARGB_8888);
        File file = new File(projectFolder, ".preview.png");
        try {
            Files.saveImage(file, bitmap);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        // start editor
		Intent intent = new Intent(this, Editor.class);
		intent.putExtra("projectFolder", projectFolder.getAbsolutePath());
		startActivity(intent);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// get projects in workspace
		ArrayList<String> projectsList = new ArrayList<String>();
		File file[] = arendelleFolder.listFiles();
		for (File f : file) if (f.isDirectory()) {
            if (new File(f, "project.config").exists()) {
                projectsList.add(f.getName());
            }
        }
		Collections.sort(projectsList);

        /////////////////////////// EXPERIMENTAL

		ProjectsListItem items[] = new ProjectsListItem[projectsList.size()];
		for (int i = 0; i < items.length; i++) {
            File preview = new File(Environment.getExternalStorageDirectory() + "/Arendelle/" + projectsList.get(i), ".preview.png");
	        items[i] = new ProjectsListItem(projectsList.get(i), BitmapFactory.decodeFile(preview.getAbsolutePath()));
		}

        ProjectsListAdapter adapter = new ProjectsListAdapter(this, R.layout.projects_listview_item, items);
		
		// display projects and previews in the list
		listProjects.setAdapter(adapter);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // check if footer is selected
        if (listProjects.getFooterViewsCount() > 0 && position == listProjects.getCount() - 1) {
            prefs.edit().putBoolean("showBookButton", false).apply();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://web.arendelle.org/book"));
            startActivity(intent);
        }

        // open project
        else {
            File projectFolder = new File(Environment.getExternalStorageDirectory() + "/Arendelle/" + ((TextView) view.findViewById(R.id.projects_listview_item_text1)).getText().toString());
            ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
            Intent intent = new Intent(this, Editor.class);
            intent.putExtra("projectFolder", projectFolder.getAbsolutePath());
            ActivityCompat.startActivity(this, intent, options.toBundle());
        }

	}

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

        // create project options dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.dialog_file_options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                File projectFolder = new File(Environment.getExternalStorageDirectory() + "/Arendelle/" + ((TextView) view.findViewById(R.id.projects_listview_item_text1)).getText().toString());

                switch(which) {

                    // rename project
                    case 0:
                        showRenameProjectDialog(projectFolder);
                        break;

                    // delete project
                    case 1:
                        Files.delete(projectFolder);
                        onResume();
                        break;

                }

            }

        });
        builder.show();

        return false;
    }

    /** shows dialog for rename project */
    private void showRenameProjectDialog(final File projectFolder) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_rename, null);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // check input
                if (((EditText) dialogView.findViewById(R.id.dialog_rename_text_name)).getText().toString().equals("")) {
                    showRenameProjectDialog(projectFolder);
                } else {
                    renameProject(projectFolder, ((EditText) dialogView.findViewById(R.id.dialog_rename_text_name)).getText().toString());
                }
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

    }

    /** renames a project */
    private void renameProject(File projectFolder, String newName) {

        projectFolder.renameTo(new File(projectFolder.getParent() + "/" + newName));
        onResume();

    }

    /** creates a preview image */
    private Bitmap createPreviewImage(String code) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int cellWidth = 20 * (int)getResources().getDisplayMetrics().density;
        int cellHeight = 20 * (int)getResources().getDisplayMetrics().density;
        CodeScreen screen = new CodeScreen(getResources().getDisplayMetrics().widthPixels / cellWidth, 5, "", false);
        MasterEvaluator.evaluate(code, screen);

        return Screen.createPreviewImage(this, screen, cellWidth, cellHeight);
    }

    /** creates example projects */
    private void createExampleProjects() {

        Object examples[][] = { {   R.raw.example_10print,  "10print.arendelle",    "10 PRINT",     "2" },
                                {   R.raw.example_qbert,    "qbert.arendelle",      "Q-Bert",       "0" },
                                {   R.raw.example_basic1,   "basic1.arendelle",     "Basic 1",      "0" }
        };

        for (Object example[] : examples) {

            // create project folder
            File projectFolder = new File(Environment.getExternalStorageDirectory() + "/Arendelle/" + example[2]);
            projectFolder.mkdir();

            // copy files
            try {
                Files.copyFromRawToPath(this, (int)example[0], new File(projectFolder, (String)example[1]));
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }

            // create config file
            File configFile = new File(projectFolder, "project.config");
            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("mainFunction", (String)example[1]);
            properties.put("currentFunction", properties.get("mainFunction"));
            properties.put("colorPalette", (String)example[3]);
            try {
                properties.put("ide", "android;" + String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
            } catch (Exception e) {
                properties.put("ide", "android;0");
            }
            switch (Integer.valueOf(properties.get("colorPalette"))) {

                // Arendelle Classic
                case 0:
                    properties.put("colorBackground", "#000000");
                    properties.put("colorFirst", "#FFFFFF");
                    properties.put("colorSecond", "#CECECE");
                    properties.put("colorThird", "#8C8A8C");
                    properties.put("colorFourth", "#424542");
                    break;

                // Arendelle Pink
                case 2:
                    properties.put("colorBackground", "#000000");
                    properties.put("colorFirst", "#E60087");
                    properties.put("colorSecond", "#B800AD");
                    properties.put("colorThird", "#8E00D7");
                    properties.put("colorFourth", "#6600FF");
                    break;

            }
            try {
                Files.createConfigFile(configFile, properties);
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }

            // create preview image
            try {
                Files.saveImage(new File(projectFolder, ".preview.png"), createPreviewImage(Files.read(new File(projectFolder, properties.get("mainFunction")))));
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }

        }

    }

}
