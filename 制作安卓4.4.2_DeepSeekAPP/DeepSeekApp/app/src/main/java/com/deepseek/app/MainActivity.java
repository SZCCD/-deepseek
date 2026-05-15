package com.deepseek.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * DeepSeek 极简版APP - 主Activity
 * 兼容 Android 4.4.2 (API 19)
 */
public class MainActivity extends Activity {

    private static final String DEEPSEEK_URL = "https://chat.deepseek.com/";
    private static final String PREF_NAME = "DeepSeekPrefs";
    private static final String KEY_COOKIES = "saved_cookies";

    private WebView webView;
    private ProgressBar progressBar;
    private FrameLayout fullscreenContainer;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置全屏
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // 创建布局
        setupLayout();

        // 配置WebView
        setupWebView();

        // 加载DeepSeek
        loadDeepSeek();
    }

    private void setupLayout() {
        // 创建根布局
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // 创建WebView
        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        rootLayout.addView(webView);

        // 创建进度条
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dpToPx(4)
        ));
        rootLayout.addView(progressBar);

        // 全屏容器（用于视频播放）
        fullscreenContainer = new FrameLayout(this);
        fullscreenContainer.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        rootLayout.addView(fullscreenContainer);

        setContentView(rootLayout);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();

        // 基础设置
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        // 缓存设置
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheEnabled(true);

        // 兼容性设置
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        // Android 5.0+ 的HTTPS设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        // 启用JavaScript窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 设置User Agent（模拟桌面浏览器以获得更好体验）
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent + " DeepSeekApp/1.0");

        // 设置WebViewClient
        webView.setWebViewClient(new DeepSeekWebViewClient());

        // 设置WebChromeClient
        webView.setWebChromeClient(new DeepSeekWebChromeClient());

        // 恢复Cookie
        restoreCookies();
    }

    private void loadDeepSeek() {
        webView.loadUrl(DEEPSEEK_URL);
    }

    // WebViewClient实现
    private class DeepSeekWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            // 只在WebView内打开DeepSeek相关链接
            if (url.contains("deepseek.com")) {
                return false;
            }
            // 其他链接在外部浏览器打开
            try {
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(request.getUrl());
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "无法打开链接", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        // Android 4.4兼容的方法
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("deepseek.com")) {
                return false;
            }
            try {
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "无法打开链接", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);

            // 保存Cookie以保持登录状态
            saveCookies();

            // 注入CSS优化移动端显示
            injectCSS();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            showErrorDialog("网络错误", "无法连接到DeepSeek，请检查网络连接后重试。");
        }
    }

    // WebChromeClient实现
    private class DeepSeekWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // 全屏模式（用于视频等）
            if (customView != null) {
                callback.onCustomViewHidden();
                return;
            }
            customView = view;
            customViewCallback = callback;
            webView.setVisibility(View.GONE);
            fullscreenContainer.setVisibility(View.VISIBLE);
            fullscreenContainer.addView(view);
        }

        @Override
        public void onHideCustomView() {
            if (customView == null) {
                return;
            }
            webView.setVisibility(View.VISIBLE);
            fullscreenContainer.setVisibility(View.GONE);
            fullscreenContainer.removeView(customView);
            customView = null;
            if (customViewCallback != null) {
                customViewCallback.onCustomViewHidden();
            }
            customViewCallback = null;
        }
    }

    // 保存Cookie
    private void saveCookies() {
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            String cookies = cookieManager.getCookie(DEEPSEEK_URL);
            if (cookies != null && !cookies.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                prefs.edit().putString(KEY_COOKIES, cookies).apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 恢复Cookie
    private void restoreCookies() {
        try {
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String cookies = prefs.getString(KEY_COOKIES, "");
            if (!cookies.isEmpty()) {
                CookieManager cookieManager = CookieManager.getInstance();
                String[] cookieArray = cookies.split(";");
                for (String cookie : cookieArray) {
                    String trimmed = cookie.trim();
                    if (!trimmed.isEmpty()) {
                        cookieManager.setCookie(DEEPSEEK_URL, trimmed);
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 注入CSS优化显示
    private void injectCSS() {
        String css = "javascript:(function() {" +
            "var style = document.createElement('style');" +
            "style.type = 'text/css';" +
            "style.innerHTML = '" +
            "body { font-size: 14px !important; }" +
            ".mobile-optimised { padding: 8px !important; }" +
            "';" +
            "document.head.appendChild(style);" +
            "})()";
        webView.loadUrl(css);
    }

    // 显示错误对话框
    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("重试", (dialog, which) -> loadDeepSeek())
            .setNegativeButton("取消", null)
            .show();
    }

    // 处理返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 如果在全屏模式，退出全屏
            if (customView != null) {
                onHideCustomView();
                return true;
            }
            // 如果WebView可以后退，则后退
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    // dp转px
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
