package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import br.ufrj.caronae.Constants;
import br.ufrj.caronae.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class FAQFrag extends Fragment {

    @BindView(R.id.webview_faq)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    public FAQFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        ButterKnife.bind(this, view);

        progressBar.getIndeterminateDrawable().setColorFilter(0xFF000000, android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view,url);
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.loadUrl(Constants.CARONAE_URL_BASE + "faq.html?mobile");
        webView.getSettings().setJavaScriptEnabled(true);
        return view;
    }
}
