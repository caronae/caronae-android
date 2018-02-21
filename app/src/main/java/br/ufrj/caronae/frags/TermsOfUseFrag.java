package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import br.ufrj.caronae.App;
import br.ufrj.caronae.Constants;
import br.ufrj.caronae.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsOfUseFrag extends Fragment {
    @BindView(R.id.webview)
    WebView webView;

    public TermsOfUseFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(Constants.CARONAE_URL_BASE + "termos_mobile.html");

        return view;
    }
}
