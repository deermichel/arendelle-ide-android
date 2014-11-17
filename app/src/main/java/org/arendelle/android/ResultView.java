package org.arendelle.android;

import org.arendelle.java.engine.CodeScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/** custom view to override onDraw method to draw Arendelles screen */
public class ResultView extends SurfaceView implements SurfaceHolder.Callback {
	
	/** paint object */
	private Paint paint = new Paint();

	
	public ResultView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getHolder().addCallback(this);
	}

	public ResultView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
	}

	public ResultView(Context context) {
		super(context);
		getHolder().addCallback(this);
	}

	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}
	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	/** draws Arendelles screen */
	public void draw(CodeScreen screen) {

		Canvas canvas = getHolder().lockCanvas();
		if (canvas == null || screen == null) return;
		
		for (int x = 0; x < screen.width; x++) for (int y = 0; y < screen.height; y++) {
				
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
		
		getHolder().unlockCanvasAndPost(canvas);

	}
	
}
