package com.deepseek.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;

/**
 * DeepSeek 极简版 APP - CrossWalk 内核版
 * 兼容 Android 4.4.2 (API 19)
 * 使用 CrossWalk WebView 替代系统 WebView，解决旧系统无法加载现代网页的问题
 */
public class MainActivity extends Activity {

    private static final String DEEPSEEK_URL = "https://chat.deepseek.com/";
    private static final String PREF_NAME = "DeepSeekPrefs";

    private XWalkView xWalkView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全屏
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // 创建布局
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // 创建 CrossWalk WebView
        xWalkView = new XWalkView(this, this);
        xWalkView.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        rootLayout.addView(xWalkView);

        // 创建进度条
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dpToPx(4)
        ));
        rootLayout.addView(progressBar);

        setContentView(rootLayout);

        // 配置 CrossWalk
        setupXWalkView();

        // 加载 DeepSeek
        xWalkView.load(DEEPSEEK_URL, null);
    }

    private void setupXWalkView() {
        // 启用 JavaScript
        xWalkView.getSettings().setJavaScriptEnabled(true);
        xWalkView.getSettings().setDomStorageEnabled(true);
        xWalkView.getSettings().setSupportZoom(true);
        xWalkView.getSettings().setBuiltInZoomControls(true);
        xWalkView.getSettings().setDisplayZoomControls(false);

        // 设置资源客户端（处理加载进度）
        xWalkView.setResourceClient(new XWalkResourceClient(xWalkView) {
            @Override
            public void onLoadStarted(XWalkView view, String url) {
                super.onLoadStarted(view, url);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(100);
            }

            @Override
            public void onProgressChanged(XWalkView view, int progressInPercent) {
                super.onProgressChanged(view, progressInPercent);
                progressBar.setProgress(progressInPercent);
            }

            @Override
            public XWalkWebResourceResponse shouldInterceptLoadRequest(XWalkView view, XWalkWebResourceRequest request) {
                // DeepSeek 链接在内部打开，其他链接在外部浏览器打开
                String url = request.getUrl().toString();
                if (url != null && !url.contains("deepseek.com") && !url.contains("google.com") && !url.contains("recaptcha.net")) {
                    try {
                        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                        intent.setData(android.net.Uri.parse(url));
                        startActivity(intent);
                        return null;
                    } catch (Exception e) {
                        // 忽略
                    }
                }
                return super.shouldInterceptLoadRequest(view, request);
            }
        });

        // 设置 UI 客户端
        xWalkView.setUIClient(new XWalkUIClient(xWalkView) {
            @Override
            public void onReceivedTitle(XWalkView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }

    // 处理返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (xWalkView != null && xWalkView.getNavigationHistory().canGoBack()) {
                xWalkView.getNavigationHistory().navigateBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (xWalkView != null) {
            xWalkView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (xWalkView != null) {
            xWalkView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (xWalkView != null) {
            xWalkView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (xWalkView != null) {
            xWalkView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        if (xWalkView != null) {
            xWalkView.onNewIntent(intent);
        }
    }
}
