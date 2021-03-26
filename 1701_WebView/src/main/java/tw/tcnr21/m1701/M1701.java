package tw.tcnr21.m1701;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class M1701 extends AppCompatActivity {

    private WebView webview;

    private static final String MAP_URL = "file:///android_asset/youtube.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m1701);
        setupViewComponent();
    }

    private void setupViewComponent() {
        webview =(WebView)findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(MAP_URL);

    }
}