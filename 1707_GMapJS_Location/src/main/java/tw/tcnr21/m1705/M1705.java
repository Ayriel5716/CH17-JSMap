package tw.tcnr21.m1705;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class M1705 extends AppCompatActivity implements LocationListener {


    private WebView webview;
    private Spinner mSpnLocation;

    private MenuItem action_settings,show_clip;
    private Intent intent = new Intent();

    private static final String MAP_URL = "file:///android_asset/GoogleMap.html";// 自建的html檔名
    //    private static final String clit_URL = "file:///android_asset/youtube.html";// 自建的html檔名
    private Menu menu;
    private static String[][] locations = {
            {"我的位置", "0,0"},
            { "中區職訓", "24.172127,120.610313" },
            { "東海大學路思義教堂", "24.179051,120.600610" },
            { "台中公園湖心亭", "24.144671,120.683981" },
            { "秋紅谷", "24.1674900,120.6398902" },
            { "台中火車站", "24.136829,120.685011" },
            { "國立科學博物館", "24.1579361,120.6659828" } };

    private String Lat,Lon,jcontent;

    //所需要申請的權限數組
    private static final String[] permissionsArray = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private List<String> permissionsList = new ArrayList<String>();

    //申請權限後的返回碼
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;


    /*** GPS*/
    private LocationManager locationMgr;
    private String provider; // 提供資料
    private TextView txtOutput;
    int iSelect;
    String[] sLocation;
    private String TAG = "tcnr21=>";
    private long timeInMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m1705);
        checkRequiredPermission(this);     //  檢查SDK版本, 確認是否獲得權限.
        setupViewComponent();

    }

    private void checkRequiredPermission(Activity activity) {
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size() != 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new
                    String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    private void setupViewComponent() {
        txtOutput = (TextView) findViewById(R.id.txtOutput);
        webview = (WebView)findViewById(R.id.webview);
        mSpnLocation = (Spinner)findViewById(R.id.spnLocation);
        mSpnLocation.getBackground().setAlpha(150);//範圍0-255
        mSpnLocation.setZ(3);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(M1705.this, "AndroidFunction");
        webview.loadUrl(MAP_URL);

//        Location
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_style);
        for (int i = 0;i < locations.length;i++) {
            adapter.add(locations[i][0]);
        }
            adapter.setDropDownViewResource(R.layout.spinner_style);
            mSpnLocation.setAdapter(adapter);
            mSpnLocation.setOnItemSelectedListener(mSpnLocationOnItemSelLis);
    }

    //    private JSONArray ArryToJson() {
    private String ArryToJson() {
        JSONArray jArry = new JSONArray();

        for (int i = 0; i < locations.length; i++) {
            JSONObject jObj = new JSONObject();// 一定要放在這裡
            String[] arr = locations[i][1].split(",");

            try {
                jObj.put("title", locations[i][0]);
                jObj.put("jlat", arr[0]);
                jObj.put("jlon", arr[1]);
                jArry.put(jObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String string_jArry=jArry.toString();
        return string_jArry;
//            return jArry;
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

//        webview.getSettings().setJavaScriptEnabled(true);
//        webview.addJavascriptInterface(M1705.this, "AndroidFunction");//
//        webview.loadUrl(MAP_URL);
    }

    @Override
    public void onLocationChanged(Location location) {
        updateWithNewLocation(location);
    }


    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled");
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                Log.v(TAG, "Status Changed: Out of Service");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.v(TAG, "Status Changed: Temporarily Unavailable");
                break;
            case LocationProvider.AVAILABLE:
                Log.v(TAG, "Status Changed: Available");
                break;
        }
    }




    @Override
    public void onProviderDisabled(String provider) {
        updateWithNewLocation(null);
        Log.d(TAG, "onProviderDisabled");
    }

    private void updateWithNewLocation(Location location) {
        String where = "";
        if (location != null) {

            double lon = location.getLongitude();// 經度
            double lat = location.getLatitude();// 緯度
            float speed = location.getSpeed();// 速度
            long time = location.getTime();// 時間
            String timeString = getTimeString(time);
            Lat = String.valueOf(lat);
            Lon = String.valueOf(lon);

            where = "經度: " + lon + "\n緯度: " + lat + "\n速度: " + speed + "\n時間: "
                    + timeString + "\nProvider: " + provider;
            // 標記"我的位置"
            locations[0][1] = lat + "," + lon; // 用GPS找到的位置更換 陣列的目前位置
            // --- 呼叫 Map JS
            webview.loadUrl(MAP_URL);
            // ---
        } else {
            where = "*位置訊號消失*";
        }
        // 位置改變顯示
        txtOutput.setText(where);
    }

    private String getTimeString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(timeInMilliseconds);

    }


    /* 建立位置改變偵聽器 預先顯示上次的已知位置 */
    private void nowaddress() {
        //檢查是否有權限-------------------------------------------
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationMgr.getLastKnownLocation(provider);
        updateWithNewLocation(location);

        // 監聽 GPS Listener
        locationMgr.addGpsStatusListener(gpsListener);

        // Location Listener
        long minTime = 5000;// ms
        float minDist = 5.0f;// meter
        locationMgr.requestLocationUpdates(provider, minTime, minDist,
                this);
    }
    /* 檢查GPS 設定GPS服務 */
    private boolean initLocationProvider() {
        locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            return true;
        }
        return false;
    }


    @Override
    /* 開啟時先檢查是否有啟動GPS精緻定位 */

    protected void onStart() {
        super.onStart();

        if (initLocationProvider()) {
            nowaddress();
        } else {
            txtOutput.setText("GPS未開啟,請先開啟定位！");
        }
    }

    @Override
    /*    停止時，移除更新位置*/
    protected void onStop() {
        super.onStop();
        locationMgr.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /************************************************
     * GPS部份
     ***********************************************/
    // -------------------------------
    GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
        /* 監聽GPS 狀態 */
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d(TAG, "GPS_EVENT_STARTED");
                    break;

                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d(TAG, "GPS_EVENT_STOPPED");
                    break;

                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d(TAG, "GPS_EVENT_FIRST_FIX");
                    break;

                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.d(TAG, "GPS_EVENT_SATELLITE_STATUS");
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), permissions[i] + "權限申請成功!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "權限被拒絕： " + permissions[i], Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

//去取經緯度
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

    @JavascriptInterface
    public String GetJsonArry() {
        return  ArryToJson();
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