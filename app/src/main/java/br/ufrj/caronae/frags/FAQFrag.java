package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class FAQFrag extends Fragment {

    @BindView(R.id.webview_faq)
    WebView webView;


    public FAQFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        ButterKnife.bind(this, view);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(App.getHost() + "static_pages/faq.html");

        return view;
    }
}
