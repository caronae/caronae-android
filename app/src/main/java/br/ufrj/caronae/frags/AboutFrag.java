package br.ufrj.caronae.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squareup.leakcanary.RefWatcher;

import br.ufrj.caronae.App;
import br.ufrj.caronae.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutFrag extends Fragment {
    @Bind(R.id.webview)
    WebView webView;

    public AboutFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://www.google.com");

        return view;
    }

    /*@Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }*/
}
