package com.wms.reporting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private static final String TARGET_URL = "http://192.168.168.40";

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout errorLayout;
    private boolean hasError = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView             = findViewById(R.id.webview);
        progressBar         = findViewById(R.id.progressBar);
        swipeRefreshLayout  = findViewById(R.id.swipeRefresh);
        errorLayout         = findViewById(R.id.errorLayout);
        Button btnRetry     = findViewById(R.id.btnRetry);

        // --- WebSettings ---
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // --- WebViewClient ---
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                hasError = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                                        WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    hasError = true;
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    showError();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Semua URL dimuat di dalam WebView
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        // --- WebChromeClient (progress) ---
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });

        // --- SwipeRefresh ---
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light
        );
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isNetworkAvailable()) {
                webView.reload();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                showNoNetworkDialog();
            }
        });

        // --- Retry button ---
        btnRetry.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                errorLayout.setVisibility(View.GONE);
                loadUrl();
            } else {
                showNoNetworkDialog();
            }
        });

        // --- Load ---
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            loadUrl();
        }
    }

    private void loadUrl() {
        if (isNetworkAvailable()) {
            webView.loadUrl(TARGET_URL);
        } else {
            showNoNetworkDialog();
        }
    }

    private void showError() {
        errorLayout.setVisibility(View.VISIBLE);
        TextView tvErrorMsg = findViewById(R.id.tvErrorMessage);
        tvErrorMsg.setText("Tidak dapat terhubung ke\n" + TARGET_URL +
                "\n\nPastikan perangkat berada\ndalam jaringan WMS yang sama.");
    }

    private void showNoNetworkDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Tidak Ada Koneksi")
                .setMessage("Periksa koneksi WiFi atau jaringan Anda, lalu coba lagi.")
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // Back button: navigasi di dalam WebView
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Keluar Aplikasi")
                    .setMessage("Apakah Anda ingin keluar?")
                    .setPositiveButton("Ya", (d, w) -> finish())
                    .setNegativeButton("Tidak", null)
                    .show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        // Reload jika sebelumnya ada error
        if (hasError && isNetworkAvailable()) {
            hasError = false;
            loadUrl();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
