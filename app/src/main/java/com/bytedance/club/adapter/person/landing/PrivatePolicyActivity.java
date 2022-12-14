package com.bytedance.club.adapter.person.landing;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bytedance.club.activtiy.PlotRead;
import com.bytedance.club.R;
import com.bytedance.club.interfaces.InterFace;
import com.bytedance.club.internet.NetRequest;
import com.bytedance.club.publics.BaseWebViewActivity;


public class PrivatePolicyActivity extends BaseWebViewActivity {

    @Override
    protected void initializeView() {
        super.initializeView();
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(LOGIN_STRING_PRIVATE_POLICY);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setRefreshEnable(FALSE);
    }

    @Override
    protected void initializeData() {
        loadUrl();
    }

    private void loadUrl() {
        String url = PlotRead.getINDEX() + NetRequest.path(InterFace.H5_PRIVATE_POLICY, BLANK);
        mWebView.loadUrl(url);
    }

    private final WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            mLoadingLayout.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mWrongLayout.setVisibility(View.VISIBLE);
        }

    };

    private final WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mWebView.setProgress(newProgress);
        }
    };

    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mWrongLayout.setVisibility(View.GONE);
        loadUrl();
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

}
