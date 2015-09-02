package org.arendelle.android;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeHighlighter {

    // pattern
    private Pattern oneLineCcomments;
    private Pattern multiLineCcomments;
    private Pattern dataTypes;
    private Pattern functions;
    private Pattern numbers;
    private Pattern brackets;


    public CodeHighlighter() {

        // compile pattern
        oneLineCcomments = Pattern.compile("(--|//).*");
        multiLineCcomments = Pattern.compile("(?:/\\\\*(?:[^*]|(?:\\\\*+[^*/]))*\\\\*+/)|(?://.*)");
        dataTypes = Pattern.compile("(\\$[a-zA-Z0-9_\\.]+|(\\#|\\&)[a-zA-Z0-9_]+|\\@[a-zA-Z0-9_]+\\??)");
        functions = Pattern.compile("![a-zA-Z0-9\\._]+");
        numbers = Pattern.compile("[0-9]+(\\.[0-9]+)?");
        brackets = Pattern.compile("[\\(\\)\\[\\]\\{\\}\\<\\>\\,]");

    }

    /** applies code highlighting */
    public void highlight(Context context, EditText editText) {
        highlight(context, editText, 0);
    }

    /** applies code highlighting with offset */
    public void highlight(Context context, EditText editText, int offset) {

        String text = editText.getText().toString();
        Editable editable = editText.getEditableText();
        highlightSingle(editable, text, numbers, context.getResources().getColor(R.color.colorNumbers), offset);
        highlightSingle(editable, text, dataTypes, context.getResources().getColor(R.color.colorDataTypes), offset);
        highlightSingle(editable, text, functions, context.getResources().getColor(R.color.colorFunctions), offset);
        highlightSingle(editable, text, brackets, context.getResources().getColor(R.color.colorBrackets), offset);
        highlightSingle(editable, text, oneLineCcomments, context.getResources().getColor(R.color.colorComments), offset);
        //highlightSingle(editable, text, multiLineCcomments, context.getResources().getColor(R.color.colorComments), offset);

    }

    private void highlightSingle(Editable editable, String text, Pattern pattern, int color, int offset) {

        Matcher matcher = pattern.matcher(text.substring(offset));
        while (matcher.find()) {
            editable.setSpan(new ForegroundColorSpan(color), offset + matcher.start(), offset + matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

    }

}
