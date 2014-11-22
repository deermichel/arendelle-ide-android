package org.arendelle.android;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends ActionBarActivity implements OnItemClickListener {
	
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
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_new:
			showNewProjectDialog();
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
                newProject(((EditText) dialogView.findViewById(R.id.dialog_new_project_text_name)).getText().toString(),
                        ((EditText) dialogView.findViewById(R.id.dialog_new_project_text_main_function_name)).getText().toString());
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
			Files.createConfigFile(configFile, properties);
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
            File preview = new File(Environment.getExternalStorageDirectory() + "/Arendelle/" + projectsList.get(i), "preview.png");
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
	
}
