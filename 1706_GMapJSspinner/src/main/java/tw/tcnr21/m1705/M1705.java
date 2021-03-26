package tw.tcnr21.m1705;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class M1705 extends AppCompatActivity {

    private static String[][] locations = {
            { "中區職訓", "24.172127,120.610313" },
            { "東海大學路思義教堂", "24.179051,120.600610" },
            { "台中公園湖心亭", "24.144671,120.683981" },
            { "秋紅谷", "24.1674900,120.6398902" },
            { "台中火車站", "24.136829,120.685011" },
            { "國立科學博物館", "24.1579361,120.6659828" } };

    private WebView webview;
    private static final String MAP_URL = "file:///android_asset/GoogleMap.html";// 自建的html檔名
//    private static final String clit_URL = "file:///android_asset/youtube.html";// 自建的html檔名
    private Menu menu;
    private MenuItem action_settings,show_clip;
    private Intent intent = new Intent();
    private Spinner mSpnLocation;
    private String Lat,Lon,jcontent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m1705);
        setupViewComponent();

    }

    private void setupViewComponent() {
        webview = (WebView)findViewById(R.id.webview);
        mSpnLocation = (Spinner)findViewById(R.id.spnLocation);
        mSpnLocation.setZ(3);
//        Location
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_style);
        for (int i = 0;i < locations.length;i++) {
            adapter.add(locations[i][0]);
        }
            adapter.setDropDownViewResource(R.layout.spinner_style);
            mSpnLocation.setAdapter(adapter);
            mSpnLocation.setOnItemSelectedListener(mSpnLocationOnItemSelLis);
    }


    private AdapterView.OnItemSelectedListener mSpnLocationOnItemSelLis = new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setMapLocation();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
};

    private void setMapLocation() {
        int iSelect = mSpnLocation.getSelectedItemPosition();
//        用逗點拆字
        String[] sLocation = locations[iSelect][1].split(",");

        Lat = sLocation[0];//南北緯
        Lon = sLocation[1];//東西經

        jcontent = locations[iSelect][0];//地名

        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(M1705.this, "AndroidFunction");//
        webview.loadUrl(MAP_URL);
    }

    @JavascriptInterface
    public String GetLat(){
        return Lat;
    }

    @JavascriptInterface
    public String GetLon(){
        return Lon;
    }

    @JavascriptInterface
    public String Getjcontent(){
        return jcontent;
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
//            case R.id.show_clip:
//                webview.loadUrl(clit_URL);
//             break;
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