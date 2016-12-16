package com.amap.assisth5location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
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
        startAssistLocation();
        initWebSettings(mWebView);
        mWebView.setWebViewClient(new CommonWebClient());
        mWebView.setWebChromeClient(new CommonWebChromeWebClient());
        mWebView.loadUrl("file:///android_asset/amaph5.html");

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
        // 允许webview执行javaScript脚本
        webSettings.setJavaScriptEnabled(true);
        // 设置是否允许定位，这里为了使用H5辅助定位，设置为false。
        //设置为true不一定会进行H5辅助定位，设置为true时只有H5定位失败后才会进行辅助定位
        webSettings.setGeolocationEnabled(false);
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
    class CommonWebChromeWebClient extends WebChromeClient {
        // 处理javascript中的alert
        public boolean onJsAlert(WebView view, String url, String message,
        final JsResult result) {
            return true;
        };

        // 处理javascript中的confirm
         public boolean onJsConfirm(WebView view, String url,
                               String message, final JsResult result) {
            return true;
         };

        // 处理定位权限请求
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                   GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        @Override
        // 设置网页加载的进度条
        public void onProgressChanged(WebView view, int newProgress) {
            MainActivity.this.getWindow().setFeatureInt(
                    Window.FEATURE_PROGRESS, newProgress * 100);
            super.onProgressChanged(view, newProgress);
        }

        // 设置应用程序的标题title
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    }

    /**
     * 启动H5辅助定位
     */
    void startAssistLocation(){
        if(null == locationClientSingle){
            locationClientSingle = new AMapLocationClient(this.getApplicationContext());
        }
        locationClientSingle.startAssistantLocation();

        Toast.makeText(MainActivity.this, "正在定位...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClientSingle) {
            locationClientSingle.stopAssistantLocation();
            locationClientSingle.onDestroy();
        }
        if(null != mWebView){
            mWebView.destroy();
        }
    }

}
