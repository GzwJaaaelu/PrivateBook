package com.jaaaelu.gzw.neteasy.privatebook.activities;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.jaaaelu.gzw.neteasy.common.app.BaseActivity;
import com.jaaaelu.gzw.neteasy.privatebook.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.KeyEvent.KEYCODE_BACK;

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.wv_show_web)
    WebView mShowWeb;
    @BindView(R.id.pb_loading)
    ProgressBar mLoading;
    @BindView(R.id.ll_web_view_root)
    LinearLayout mWebViewRoot;

    /**
     * 跳转到当前 Activity
     *
     * @param context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, WebViewActivity.class));
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initView() {
        super.initView();
        mWebViewRoot.setLayoutTransition(getLayoutTransition());
        mLoading.setProgress(0);
        mShowWeb.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mLoading.setVisibility(View.GONE);
            }
        });
        WebSettings settings = mShowWeb.getSettings();
        settings.setLoadsImagesAutomatically(true);
        settings.setJavaScriptEnabled(true); //启用JavaScript
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        mShowWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 100) {
                    mLoading.setProgress(newProgress);
                }
            }
        });
        mShowWeb.loadUrl("https://www.douban.com/doulist/1264675/");
    }

    private LayoutTransition getLayoutTransition() {
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setAnimator(LayoutTransition.APPEARING, layoutTransition.getAnimator(LayoutTransition.APPEARING));
        layoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, layoutTransition.getAnimator(LayoutTransition.CHANGE_APPEARING));
        layoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, layoutTransition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING));
        layoutTransition.setAnimator(LayoutTransition.CHANGING, layoutTransition.getAnimator(LayoutTransition.CHANGING));
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, layoutTransition.getAnimator(LayoutTransition.DISAPPEARING));
        return layoutTransition;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && mShowWeb.canGoBack()) {
            mShowWeb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShowWeb.clearHistory();
        mShowWeb.clearCache(true);
        mShowWeb.destroy();
    }
}
