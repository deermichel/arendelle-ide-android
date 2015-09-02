package org.arendelle.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomEditText extends EditText {

    private Rect rect = new Rect();
    private Paint paint = new Paint();
    private int lineNumberLength = 0;


    public CustomEditText(Context context, AttributeSet attrs) {

        super(context, attrs);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(this.getTextSize());

    }

    @Override
    protected void onDraw(Canvas canvas) {

        // add line numbers
        int baseline = getBaseline();
        for (int i = 0; i < getLineCount(); i++) {
            canvas.drawText("" + (i+1), rect.left, baseline, paint);
            baseline += getLineHeight();
        }

        // set padding
        if (String.valueOf(getLineCount()).length() != lineNumberLength) {
            lineNumberLength = String.valueOf(getLineCount()).length();
            this.setPadding((int) paint.measureText(getLineCount() + "__"), 0, 0, 0);
        }

        super.onDraw(canvas);
    }
}