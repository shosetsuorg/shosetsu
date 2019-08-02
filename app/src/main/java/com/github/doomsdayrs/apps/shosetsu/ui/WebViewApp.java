package com.github.doomsdayrs.apps.shosetsu.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.doomsdayrs.apps.shosetsu.R;

import java.util.ArrayList;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * shosetsu
 * 31 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class WebViewApp extends AppCompatActivity {


    WebView webView;
    Actions action = Actions.VIEW;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        webView = findViewById(R.id.webview);
        Intent intent = getIntent();
        action = Actions.actions.get(intent.getIntExtra("action", 0));
        webView.getSettings().setJavaScriptEnabled(true);

        switch (action) {
            case VIEW:
                webView.setWebViewClient(new WebViewClient());
                break;
            case CLOUD_FLARE:
                webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        webView.loadUrl("javascript:window.HtmlViewer.showHTML" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                        finish();
                    }
                });

                break;
            default:
                break;
        }

        webView.loadUrl(intent.getStringExtra("url"));
    }

    public enum Actions {
        VIEW(0),
        CLOUD_FLARE(1);
        public static final ArrayList<Actions> actions = new ArrayList<>();

        static {
            actions.add(Actions.VIEW);
            actions.add(Actions.CLOUD_FLARE);
        }

        public final int action;

        Actions(int action) {
            this.action = action;
        }

        public int getAction() {
            return action;
        }
    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html) {
            new AlertDialog.Builder(ctx).setTitle("HTML").setMessage(html)
                    .setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
        }

    }
}
