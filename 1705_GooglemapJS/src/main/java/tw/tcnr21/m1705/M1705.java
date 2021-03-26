package tw.tcnr21.m1705;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class M1705 extends AppCompatActivity {

    private WebView webview;
    private static final String MAP_URL = "file:///android_asset/GoogleMap.html";// 自建的html檔名
    private static final String clit_URL = "file:///android_asset/youtube.html";// 自建的html檔名
    private Menu menu;
    private MenuItem action_settings,show_clip;
    private Intent intent = new Intent();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m1701a);
        setupViewComponent();

    }

    private void setupViewComponent() {
        webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(MAP_URL);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                finish();
                break;
            case R.id.show_clip:
                webview.loadUrl(clit_URL);
             break;
            case R.id.map:
                webview.loadUrl(MAP_URL);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        this.menu = menu;
        action_settings = (MenuItem)findViewById(R.id.action_settings);
        show_clip = (MenuItem)findViewById(R.id.show_clip);

        return super.onCreateOptionsMenu(menu);
    }
}