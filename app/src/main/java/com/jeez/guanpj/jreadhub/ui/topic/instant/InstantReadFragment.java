package com.jeez.guanpj.jreadhub.ui.topic.instant;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jeez.guanpj.jreadhub.R;
import com.jeez.guanpj.jreadhub.ReadhubApplication;
import com.jeez.guanpj.jreadhub.bean.InstantReadBean;
import com.jeez.guanpj.jreadhub.mvpframe.view.fragment.AbsBaseMvpDialogFragment;
import com.jeez.guanpj.jreadhub.util.Constants;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.OnClick;

public class InstantReadFragment extends AbsBaseMvpDialogFragment<InstantReadPresenter> implements InstantReadContract.View {

    public static final String TAG = "InstantReadFragment";
    @BindView(R.id.txt_topic_instant_title)
    TextView mTxtTopicTitle;
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.txt_instant_source)
    TextView mTxtSource;
    @BindView(R.id.txt_origin_site)
    TextView mTxtGoOrigin;

    private String mTopicId;

    public static InstantReadFragment newInstance(String topicId) {
        InstantReadFragment fragment = new InstantReadFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_TOPIC_ID, topicId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AlertDialogStyle);
    }

    @Override
    protected void performInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_instant_read;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initDataAndEvent() {
        mTopicId = getArguments().getString(Constants.BUNDLE_TOPIC_ID);
        initWebSettings();
        mPresenter.getTopicInstantRead(mTopicId);
    }

    private void initWebSettings() {
        WebSettings mWebSetting = mWebView.getSettings();
        mWebSetting.setJavaScriptEnabled(true);
        mWebSetting.setUseWideViewPort(true);
        mWebSetting.setLoadWithOverviewMode(true);
        mWebSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        mWebSetting.setLoadsImagesAutomatically(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    dismiss();
                    /*((MainActivity) getContext()).findFragment(MainFragment.class)
                            .start(WebViewFragment.newInstance(url));*/
                }
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.endsWith("mobi.min.css")) {
                    //使用本地 css 优化阅读视图
                    WebResourceResponse resourceResponse = null;
                    try {
                        InputStream in = ReadhubApplication.getInstance().getAssets().open("css/mobi.css");
                        resourceResponse = new WebResourceResponse("text/css", "UTF-8", in);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (resourceResponse != null) {
                        return resourceResponse;
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }
        });
    }

    @Override
    public void onRequestStart() {

    }

    @Override
    public void onRequestEnd(InstantReadBean data) {
        if (data == null) return;
        mTxtTopicTitle.setText(data.getTitle());
        mTxtSource.setText(getString(R.string.source_format, data.getSiteName()));
        if (!TextUtils.isEmpty(data.getUrl())) {
            mTxtGoOrigin.setVisibility(View.VISIBLE);
            mTxtGoOrigin.setOnClickListener(v -> {
                dismiss();
                /*getContext().findFragment(MainFragment.class)
                        .start(WebViewFragment.newInstance(data.getUrl()));*/
            });
        }
        String htmlHead = "<head>"
                +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<link rel=\"stylesheet\" href=\"https://unpkg.com/mobi.css/dist/mobi.min.css\">"
                + "<style>"
                + "img{max-width:100% !important; width:auto; height:auto;}"
                + "body {font-size: 110%;word-spacing:110%；}"
                + "</style>"
                + "</head>";
        String htmlContent = "<html>"
                + htmlHead
                + "<body style:'height:auto;max-width: 100%; width:auto;'>"
                + data.getContent()
                + "</body></html>";
        mWebView.loadData(htmlContent, "text/html; charset=UTF-8", null);
    }

    @Override
    public void onRequestError() {
        showShortToast("请求错误");
        dismiss();
    }

    @Override
    public void onFabClick() {

    }

    @OnClick(R.id.img_close)
    void onCloseClick() {
        dismiss();
    }
}