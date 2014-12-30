package org.arendelle.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

public class Webview extends ActionBarActivity {

    // gui objects
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.webview_toolbar);
        setSupportActionBar(toolbar);
        setTitle(getIntent().getExtras().getString("title"));

        // get gui objects
        webView = (WebView) findViewById(R.id.webview);

        // activate back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setup webview
        webView.setBackgroundColor(Color.BLACK);
        webView.loadUrl(getIntent().getExtras().getString("url"));

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

}