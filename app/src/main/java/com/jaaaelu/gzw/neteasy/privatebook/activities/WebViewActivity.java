package com.jaaaelu.gzw.neteasy.privatebook.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteHtmlHelper;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.jaaaelu.gzw.neteasy.common.app.BaseActivity;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.squareup.okhttp.Response;

import java.io.IOException;

import butterknife.BindView;

import static android.view.KeyEvent.KEYCODE_BACK;
import static com.jaaaelu.gzw.neteasy.common.tools.UiTool.getLayoutTransition;

public class WebViewActivity extends BaseActivity {
    private static final String KEY_NOTE = "KEY_NOTE";
    private static final String KEY_HTML = "KEY_HTML";

    @BindView(R.id.wv_show_web)
    WebView mShowWeb;
    @BindView(R.id.pb_loading)
    ProgressBar mLoading;
    @BindView(R.id.ll_web_view_root)
    LinearLayout mWebViewRoot;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private NoteRef mNoteRef;
    private String mHtml;
    private EvernoteHtmlHelper mEvernoteHtmlHelper;

    /**
     * 跳转到当前 Activity
     *
     * @param context
     */
    public static void show(Context context, NoteRef note, String html) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KEY_NOTE, note);
        intent.putExtra(KEY_HTML, html);
        context.startActivity(intent);
    }

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
        setWebViewSetting();
        mShowWeb.setWebViewClient(new MyWebViewClient());
        mShowWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 100 && mHtml == null) {
                    mLoading.setProgress(newProgress);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mNoteRef = getIntent().getParcelableExtra(KEY_NOTE);
        mHtml = getIntent().getStringExtra(KEY_HTML);

        //  两种情况 一跳转到笔记详情 二跳转到豆瓣推荐
        if (mHtml != null) {
            mToolbar.setTitle(mNoteRef.getTitle());
            String data = getHtmlData(mHtml);
            mShowWeb.loadDataWithBaseURL("", data, "text/html", "UTF-8", null);
        } else {
            mToolbar.setTitle("豆瓣读书评分9分以上榜单");
            mShowWeb.loadUrl("https://www.douban.com/doulist/1264675/");
        }

        initToolbar(mToolbar);
    }

    /**
     * 拼接html字符串片段
     *
     * @param bodyHTML 页面
     * @return 处理过的页面
     */
    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width:100% !important; width:auto; height:auto;}</style>" +
                "</head>";
        return "<html>" + head + "<body style:'height:auto;max-width: 100%; width:auto;'>" + bodyHTML + "</body></html>";
    }

    /**
     * WebView 的各种设置
     */
    private void setWebViewSetting() {
        WebSettings settings = mShowWeb.getSettings();
        settings.setLoadsImagesAutomatically(true);
        settings.setJavaScriptEnabled(true); //启用JavaScript
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && mShowWeb.canGoBack()) {
            mShowWeb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected EvernoteHtmlHelper getEvernoteHtmlHelper() throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
        if (mEvernoteHtmlHelper == null) {
            EvernoteClientFactory clientFactory = EvernoteSession.getInstance().getEvernoteClientFactory();

            if (mNoteRef.isLinked()) {
                mEvernoteHtmlHelper = clientFactory.getLinkedHtmlHelper(mNoteRef.loadLinkedNotebook());
            } else {
                mEvernoteHtmlHelper = clientFactory.getHtmlHelperDefault();
            }
        }

        return mEvernoteHtmlHelper;
    }

    protected WebResourceResponse toWebResource(Response response) throws IOException {
        if (response == null || !response.isSuccessful()) {
            return null;
        }

        String mimeType = response.header("Content-Type");
        String charset = response.header("charset");
        return new WebResourceResponse(mimeType, charset, response.body().byteStream());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShowWeb.clearHistory();
        mShowWeb.clearCache(true);
        mShowWeb.destroy();
    }

    private class MyWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            try {
                Response response = getEvernoteHtmlHelper().fetchEvernoteUrl(url);
                WebResourceResponse webResourceResponse = toWebResource(response);
                if (webResourceResponse != null) {
                    return webResourceResponse;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mHtml == null) {
                mLoading.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mLoading.setVisibility(View.GONE);
        }
    }
}
