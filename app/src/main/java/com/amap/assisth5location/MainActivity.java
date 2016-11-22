package com.amap.assisth5location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class MainActivity extends AppCompatActivity {

    private WebView mWebView;

    private AMapLocationClient locationClientSingle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.mWebView);
        initWebSettings(mWebView);
        mWebView.setWebViewClient(new CommonWebClient());

        mWebView.loadUrl("file:///android_asset/amaph5.html");

    }

    // 单次定位
    public void singleLocation(View view) {
        startSingleLocation();
    }

    /**
     * 初始化webview设置
     *
     * @param mWebView
     */
    public void initWebSettings(WebView mWebView) {
        if (null == mWebView) {
            return;
        }
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // 设置UserAgent
        String userAgent = webSettings.getUserAgentString();
        mWebView.getSettings().setUserAgentString(userAgent);

        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setHorizontalScrollbarOverlay(true);
        mWebView.addJavascriptInterface(new JavaScriptCallBack(), "callBack");

    }

    class CommonWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }

    }

    /**
     * 启动单次客户端定位
     */
    void startSingleLocation(){
        if(null == locationClientSingle){
            locationClientSingle = new AMapLocationClient(this.getApplicationContext());
        }

        AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
        //使用单次定位
        locationClientOption.setOnceLocation(true);
        // 地址信息
        locationClientOption.setNeedAddress(true);
        locationClientSingle.setLocationOption(locationClientOption);
        locationClientSingle.setLocationListener(locationSingleListener);
        locationClientSingle.startLocation();

        Toast.makeText(MainActivity.this, "正在定位...", Toast.LENGTH_LONG).show();
    }

    /**
     * 停止单次客户端
     */
    void stopSingleLocation(){
        if(null != locationClientSingle){
            locationClientSingle.stopLocation();
        }
    }

    /**
     * 单次客户端的定位监听
     */
    AMapLocationListener locationSingleListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            long callBackTime = System.currentTimeMillis();
            StringBuffer sb = new StringBuffer();
            sb.append("单次定位完成\n");
            sb.append("回调时间: " + Utils.formatUTC(callBackTime, null) + "\n");
            if(null == location){
                sb.append("定位失败：location is null!!!!!!!");
            } else {
                sb.append(Utils.getLocationStr(location));
            }

            Log.e("ggb", sb.toString());
            mWebView.loadUrl("javascript:addLocationMarker(" + location.getLongitude() + ", "+ location.getLatitude() +")");
        }
    };


    class JavaScriptCallBack {

        @JavascriptInterface
        public void callLocation() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startSingleLocation();
                }
            });
        }

    }


}
