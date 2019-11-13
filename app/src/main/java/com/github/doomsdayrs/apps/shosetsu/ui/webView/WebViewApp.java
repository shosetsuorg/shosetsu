package com.github.doomsdayrs.apps.shosetsu.ui.webView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.doomsdayrs.apps.shosetsu.R;

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * shosetsu
 * 31 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class WebViewApp extends AppCompatActivity {


    private WebView webView;
    private Actions action = Actions.VIEW;

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
                webView.addJavascriptInterface(new JSInterface(this), "HtmlViewer");
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


}
