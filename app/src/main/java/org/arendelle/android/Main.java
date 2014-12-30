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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.arendelle.java.engine.Reporter;

public class Main extends ActionBarActivity implements OnItemClickListener, AdapterView.OnItemLongClickListener {
	
	// gui objects
	private ListView listProjects;
	
	
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

        // open welcome dialog on first start
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if (prefs.getBoolean("firstStart", true)) {
            prefs.edit().putBoolean("firstStart", false).apply();
            Intent intent = new Intent(this, Webview.class);
            intent.putExtra("title", getText(R.string.welcome_title));
            intent.putExtra("url", "file:///android_res/raw/welcome.html");
            startActivity(intent);
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

            // show help (book)
            case R.id.action_help:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://book.arendelle.org/"));
                startActivity(intent);
                return true;

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
                    Toast.makeText(Main.this, R.string.toast_enter_name_for_project, Toast.LENGTH_LONG).show();
                    showNewProjectDialog();
                } else if(((EditText) dialogView.findViewById(R.id.dialog_new_project_text_main_function_name)).getText().toString().equals("")) {
                    Toast.makeText(Main.this, R.string.toast_enter_name_for_main_function, Toast.LENGTH_LONG).show();
                    showNewProjectDialog();
                } else {
                    newProject(((EditText) dialogView.findViewById(R.id.dialog_new_project_text_name)).getText().toString(),
                            ((EditText) dialogView.findViewById(R.id.dialog_new_project_text_main_function_name)).getText().toString());
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
        properties.put("colorPalette", "0");
        properties.put("colorBackground", String.valueOf(getResources().getColor(R.color.arendelleClassicBackground)));
        properties.put("colorFirst", String.valueOf(getResources().getColor(R.color.arendelleClassicFirst)));
        properties.put("colorSecond", String.valueOf(getResources().getColor(R.color.arendelleClassicSecond)));
        properties.put("colorThird", String.valueOf(getResources().getColor(R.color.arendelleClassicThird)));
        properties.put("colorFourth", String.valueOf(getResources().getColor(R.color.arendelleClassicFourth)));
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
		
		// check if storage is accessible
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, R.string.toast_storage_not_accessible, Toast.LENGTH_LONG).show();
			return;
		}
		
		// open Arendelle workspace folder (or create it if neccessary)
		File arendelleFolder = new File(Environment.getExternalStorageDirectory() + "/Arendelle/");
		arendelleFolder.mkdir();
		
		// get projects in workspace
		ArrayList<String> projectsList = new ArrayList<String>();
		File file[] = arendelleFolder.listFiles();
		for (File f : file) if (f.isDirectory()) projectsList.add(f.getName());
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

        // open project
		File projectFolder = new File(Environment.getExternalStorageDirectory() + "/Arendelle/" + ((TextView) view.findViewById(R.id.projects_listview_item_text1)).getText().toString());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
		Intent intent = new Intent(this, Editor.class);
		intent.putExtra("projectFolder", projectFolder.getAbsolutePath());
        ActivityCompat.startActivity(this, intent, options.toBundle());

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
                renameProject(projectFolder, ((EditText) dialogView.findViewById(R.id.dialog_rename_text_name)).getText().toString());
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

}
