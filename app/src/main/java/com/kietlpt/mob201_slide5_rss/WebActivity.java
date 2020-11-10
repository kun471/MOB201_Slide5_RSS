package com.kietlpt.mob201_slide5_rss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class WebActivity extends AppCompatActivity {
    WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        wv=findViewById(R.id.webView);
        Intent intent = getIntent();
        String url = intent.getStringExtra("link");
        wv.loadUrl(url);
    }
}
