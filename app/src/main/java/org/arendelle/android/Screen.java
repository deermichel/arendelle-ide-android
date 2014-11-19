package org.arendelle.android;

import org.arendelle.java.engine.CodeScreen;
import org.arendelle.java.engine.MasterEvaluator;
import org.arendelle.java.engine.Reporter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.audiofx.EnvironmentalReverb;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

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
	
	/** code */
	private String code;

    /** project folder */
    private File projectFolder;
	
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

                switch (screen.screen[x][y]) {

                    case 0:
                        paint.setColor(Color.BLACK);
                        break;

                    case 1:
                        paint.setColor(Color.WHITE);
                        break;

                    case 2:
                        paint.setColor(Color.LTGRAY);
                        break;

                    case 3:
                        paint.setColor(Color.GRAY);
                        break;

                    case 4:
                        paint.setColor(Color.DKGRAY);
                        break;

                }

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
	
}