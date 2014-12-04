package org.arendelle.android;

import org.arendelle.java.engine.CodeScreen;
import org.arendelle.java.engine.MasterEvaluator;
import org.arendelle.java.engine.Reporter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Screen extends ActionBarActivity {
	
	// gui objects
	private ResultView viewResult;
	private TextView textChronometer;

	/** width of grid */
	public static int gridWidth;

	/** height of grid */
	public static int gridHeight;
	
	/** width of cells */
	public static int cellWidth;
	
	/** height of cells */
	public static int cellHeight;

    /** set if errors should be shown to prevent dialog loop */
    private boolean showErrorsDialog = true;
	
	/** compiler thread */
	private Thread compilerThread = new CompilerThread();
	class CompilerThread extends Thread {

		@Override
		public void run() {
			super.run();
			long timestamp = System.nanoTime();
			MasterEvaluator.evaluate(code, screen);
			final long elapsedTime = System.nanoTime() - timestamp;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					textChronometer.setText(String.format("%f ms", elapsedTime / 1000000f));
					// TODO: textError.setText(Reporter.errors);

                    if (Reporter.errors.length() > 0) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(Screen.this);
                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.dialog_errors, null);
                        ((TextView) dialogView.findViewById(R.id.dialog_errors_text_errors)).setText(Reporter.errors);
                        builder.setView(dialogView);
                        builder.setNeutralButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        if(showErrorsDialog) builder.show();
                        showErrorsDialog = false;

                    }

				}
				
			});
		}
		
	};

    /** color palette */
    private int colorPalette[] = new int[5];
	
	/** code */
	private String code;

    /** project folder */
    private File projectFolder;

    /** configFile */
    private File configFile;
	
	/** Arendelles screen */
	private CodeScreen screen;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen);

        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.screen_toolbar);
        setSupportActionBar(toolbar);
		
		// get gui objects
		viewResult = (ResultView) findViewById(R.id.view_result);
		textChronometer = (TextView) findViewById(R.id.text_chronometer);

		// activate back button in action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// get code
		code = getIntent().getExtras().getString("code");

        // get project folder
        projectFolder = new File(getIntent().getExtras().getString("projectFolder"));

        // get config file
        configFile = new File(projectFolder, "project.config");

        // get color palette
        try {
            HashMap<String, String> properties = Files.parseConfigFile(configFile);
            colorPalette[0] = Integer.valueOf(properties.get("colorBackground"));
            colorPalette[1] = Integer.valueOf(properties.get("colorFirst"));
            colorPalette[2] = Integer.valueOf(properties.get("colorSecond"));
            colorPalette[3] = Integer.valueOf(properties.get("colorThird"));
            colorPalette[4] = Integer.valueOf(properties.get("colorFourth"));
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            finish();
        }

        // set color palette
        viewResult.setColorPalette(colorPalette);

    }

	@Override
	protected void onPause() {
		super.onPause();
		
		// stop compiler thread
		if (compilerThread.isAlive()) compilerThread.interrupt();
		
	}

    @Override
    protected void onResume() {
        super.onResume();

        showErrorsDialog = true;

    }

    /** evaluates the code */
	private void evaluate() {
		
		// stop compiler thread
		if (compilerThread.isAlive()) compilerThread.interrupt();

		// reset screen and thread
		screen = new CodeScreen(gridWidth, gridHeight, projectFolder.getAbsolutePath(), false);
		textChronometer.setText("");
		compilerThread = new CompilerThread();
		
		// start thread
		compilerThread.start();
		
		// update screen
		while (compilerThread.isAlive()) {
			viewResult.draw(screen);
			setTitle(screen.title);
		}
		viewResult.draw(screen);
		setTitle(screen.title);

        try {
            Bitmap bitmap = Bitmap.createBitmap(getResources().getDisplayMetrics().widthPixels, cellHeight * 5, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);


            float brightness = -100;
            float contrast = 1;
            ColorMatrix colorMatrix = new ColorMatrix(new float[] {
                    contrast, 0, 0, 0, brightness,
                    0, contrast, 0, 0, brightness,
                    0, 0, contrast, 0, brightness,
                    0, 0, 0, 1, 0 });

            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));

            for (int x = 0; x < screen.width; x++) for (int y = 0; y < 5; y++) {
                paint.setColor(colorPalette[screen.screen[x][y]]);
                canvas.drawRect(x * Screen.cellWidth, y * Screen.cellHeight, x * Screen.cellWidth + Screen.cellWidth, y * Screen.cellHeight + Screen.cellHeight, paint);
            }

            File file = new File(projectFolder, "preview.png");
            Files.saveImage(file, bitmap);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_screen, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		// back button in action bar pressed
		case android.R.id.home:
			finish();
			return true;
			
		// reevaluate
		case R.id.action_rerun:
            showErrorsDialog = true;
			evaluate();
			return true;

        case R.id.action_share:
            share();
            return true;
			
		default:
			return super.onOptionsItemSelected(item);
			
		}
		
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

        // only continue if it is necessary to reevaluate
        if(!hasFocus) return;
        if(!showErrorsDialog) return;

		// set cell and grid size
		cellWidth = 20 * (int)getResources().getDisplayMetrics().density;
		cellHeight = 20 * (int)getResources().getDisplayMetrics().density;
		gridWidth = viewResult.getWidth() / cellWidth;
		gridHeight = viewResult.getHeight() / cellHeight;
		
		// reevaluate the code
		evaluate();
		
	}

    /** shares the current screen */
    private void share() {

        // generate the screen
        Bitmap bitmap = Bitmap.createBitmap(viewResult.getWidth(), viewResult.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        for (int x = 0; x < screen.width; x++) for (int y = 0; y < screen.height; y++) {

            paint.setColor(colorPalette[screen.screen[x][y]]);
            canvas.drawRect(x * Screen.cellWidth, y * Screen.cellHeight, x * Screen.cellWidth + Screen.cellWidth, y * Screen.cellHeight + Screen.cellHeight, paint);

        }

        // save image
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "arendelle_" + sdf.format(new Date()), null);

        // create share intent
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));

        // start share intent
        startActivity(Intent.createChooser(shareIntent, getText(R.string.share_chooser_title)));

    }

}