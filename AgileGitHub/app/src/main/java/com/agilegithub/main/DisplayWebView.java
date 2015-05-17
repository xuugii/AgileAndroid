package com.agilegithub.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;



public class DisplayWebView extends Activity {
    WebView web;
    public static String html = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_slide_in_top, 0);
        setContentView(R.layout.display_webview_view);
        web = (WebView) this.findViewById(R.id.webView);
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        html = getIntent().getStringExtra("html");

        web.loadDataWithBaseURL("", parseHtml(html), mimeType, encoding, "");
    }

    public String parseHtml(String code){

        code = "<code>" + code + "</code>";
        return code;
    }

    @Override
    public boolean onTouchEvent (MotionEvent ev) {
        finish();
        DisplayWebView.this.overridePendingTransition(0,R.anim.abc_slide_out_bottom);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        DisplayWebView.this.overridePendingTransition(0,R.anim.abc_slide_out_bottom);
        return true;
    }
}
