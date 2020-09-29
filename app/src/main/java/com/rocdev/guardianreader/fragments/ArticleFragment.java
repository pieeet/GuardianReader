package com.rocdev.guardianreader.fragments;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.models.Article;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleFragment extends Fragment {

    private static final String KEY_ARTICLE = "article";
    private Article article;
    private WebView webView;
    private ArticleFragmentListener mListener;


    public ArticleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param article article
     * @return A new instance of fragment ArticleFragment.
     */
    public static ArticleFragment newInstance(Article article) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_ARTICLE, article);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            article = getArguments().getParcelable(KEY_ARTICLE);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article, container, false);
        try {
            webView = (WebView) rootView.findViewById(R.id.articleWebview);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webView.setWebViewClient(new ArticleWebViewClient());
            webView.loadUrl(article.getUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ArticleFragmentListener) {
            mListener = (ArticleFragmentListener) context;
        } else {
            throw new RuntimeException("Activity should implement ArticleFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean goPageBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.stopLoading();
        webView.clearFormData();
        webView.clearAnimation();
        webView.clearDisappearingChildren();
        webView.clearHistory();
        webView.clearCache(true);
        webView.destroyDrawingCache();
        webView.destroy();

    }

    public void reload() {
        webView.reload();
    }


    private class ArticleWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
                view.loadUrl(url);
            }
            return true;
        }


        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mListener != null) {
                mListener.stopDownLoadAnimation();
            }
            webViewLoadComplete();
        }

        private void webViewLoadComplete() {
            webView.clearAnimation();
            webView.clearDisappearingChildren();
            webView.destroyDrawingCache();
        }


    }

    public interface ArticleFragmentListener {
        void startDownloadAnimation();
        void stopDownLoadAnimation();
    }


}
