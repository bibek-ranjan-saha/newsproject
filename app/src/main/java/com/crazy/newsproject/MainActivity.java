package com.crazy.newsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.monstertechno.adblocker.AdBlockerWebView;
import com.monstertechno.adblocker.util.AdBlocker;

import static android.view.View.SCROLLBARS_INSIDE_OVERLAY;
import static android.webkit.WebSettings.RenderPriority.*;
import static com.crazy.newsproject.R.*;


public class MainActivity extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBarWeb;
    private String webUrl = "https://www.bbc.com/news";
    ProgressDialog progressDialog;
    RelativeLayout relativeLayout;
    Button btnnoconnection;
//    private Switch darkmode;
//    SwipeRefreshLayout swipeRefreshLayout;
    TextView txtnoconn;
    String myCurrentURL;
//    MenuItem light;
//    MenuItem dark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//is used to remove the status bar or to create full screen mode
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

//this is for gradient animation in button not working as of now
//        AnimationDrawable ad = (AnimationDrawable) btnnoconnection.getBackground ();
//        ad.setEnterFadeDuration(2000);
//        ad.setExitFadeDuration(4000);
//        ad.start();
        setContentView(layout.activity_main);
        //adding gradient to actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setBackgroundDrawable(getResources().getDrawable(drawable.gradientactionnbar));
        }
        webView = findViewById(id.myWebView);
        progressBarWeb = findViewById(id.progressBar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading please wait");
        relativeLayout = findViewById(id.relativeLayout);
        btnnoconnection = findViewById(id.btnnoconnection);
        //swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
        new AdBlockerWebView.init(this).initializeWebView(webView);
        txtnoconn = findViewById(R.id.txtnoconnection);
        //darkmode = findViewById(darkmodetoogle);
//        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED,Color.GREEN);
//
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                webView.reload();
//            }
//        });
        //enabling java script to run
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        //setting dom storage enabled
        webView.getSettings().setDomStorageEnabled(true);
        //setting high network priority
        webView.getSettings().setRenderPriority(HIGH);
        //loading expired cache if no network
//        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //enabling app cache to store
//        webView.getSettings().setAppCacheEnabled(true);
        //it makes all column no wider than the screen possible
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //enabling wide view port
        webView.getSettings().setUseWideViewPort(true);
        //save passwords
        webView.getSettings().setSavePassword(true);
        //save cache from mobile data
//        webView.getSettings().setSaveFormData(true);
        //enabling smooth transition
        webView.getSettings().enableSmoothTransition();
        webView.setHorizontalScrollBarEnabled(true);
        webView.setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);

        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setSavePassword(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setEnableSmoothTransition(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setInitialScale(100);
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT)
        {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        }else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        }


        if (savedInstanceState != null)
            webView.restoreState(savedInstanceState);
        else
            checkConnection();

//this code for darkmode toogle button
//        darkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                }
//                else {
//                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                }
//            }
//        });

//creating function for downloading content
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, long contentLength) {
                Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                request.setMimeType(mimetype);
                                String cookies = CookieManager.getInstance().getCookie(url);
                                request.addRequestHeader("cookie",cookies);
                                request.addRequestHeader("User-Agent",userAgent);
                                request.setDescription("Downloading file...");
                                request.setTitle(URLUtil.guessFileName(url,userAgent,contentDisposition));
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(
                                        Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, userAgent, contentDisposition));
                                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.enqueue(request);
                                Toast.makeText(MainActivity.this,"Downloading file", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();

                            }
                        }).check();
            }
        });


        webView.setWebViewClient(new Browser_home()
        {
            @Override
            public void onPageFinished(WebView view, String url) {
                //swipeRefreshLayout.setRefreshing(false);
                super.onPageFinished(view, url);
                myCurrentURL = url;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
               view.loadUrl(url);
               return true;
            }
        });
        //changing the title with websites title
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                progressBarWeb.setVisibility(View.VISIBLE);
                progressBarWeb.setProgress(newProgress);
                setTitle("loading");
                progressDialog.show();
                if(newProgress==100){
                    progressBarWeb.setVisibility(View.GONE);
                    setTitle(view.getTitle());
                    progressDialog.dismiss();
                }

                super.onProgressChanged(view, newProgress);
            }
        });
        //getting the animation from resourse file
        final Animation animation = AnimationUtils.loadAnimation(this, anim.bounce);
        btnnoconnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
                YoYo.with(Techniques.Tada)
                        .duration(700)
                        .repeat(1)
                        .playOn(txtnoconn);
                //setting the animation
                btnnoconnection.startAnimation(animation);
            }
        });
    }
//over riding the back function to set alert dialouge
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Team-09 project");
            builder.setMessage("Are you sure you want to exit");
            builder.setNegativeButton("No",null);
            builder.setIcon(drawable.ic_baseline_receipt_long_24);
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            }).show();
        }
    }

    //fuction to check connection
    public void checkConnection(){

        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if(wifi.isConnected()){
            Snackbar.make(webView,"you are consuming wifi data",Snackbar.LENGTH_SHORT).show();
            webView.loadUrl(webUrl);
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        }
        else if (mobileNetwork.isConnected()){
            Snackbar.make(webView,"you are consuming mobile data",Snackbar.LENGTH_SHORT).show();
            webView.loadUrl(webUrl);
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        }
//        else if (webView.getSettings().)
//        {
//            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }
        else{
            Snackbar.make(webView,"you are not connected",Snackbar.LENGTH_SHORT).show();
            webView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }
//to add toolbar in actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbarmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case id.nav_previous:
                onBackPressed();
                break;
            case id.nav_next:
                if (webView.canGoForward()){
                    webView.canGoForward();
                }
                break;
            case id.btn_reload:
                checkConnection();
                break;
            case id.menu_share:
                Intent shareindent = new Intent(Intent.ACTION_SEND);
                shareindent.setType("text/plain");
                shareindent.putExtra(Intent.EXTRA_TEXT,myCurrentURL);
                shareindent.putExtra(Intent.EXTRA_SUBJECT,"Copied URL");
                startActivity(Intent.createChooser(shareindent,"Share.."));
                Toast.makeText(MainActivity.this,
                        "share your link",
                        Toast.LENGTH_SHORT).show();
//            case id.menu_light:
//                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
//                    // Turning on the light mode
//                    WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
//                }
//            case id.menu_dark:
//                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
//                    // Turning on the dark mode
//                    WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
//                }
        }

        return super.onOptionsItemSelected(item);
    }
    //darkmode
    public void darkmode(MenuItem item) {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            // Turning on the dark mode
            WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
            this.invalidateOptionsMenu();
            MenuItem menuItem = (MenuItem) findViewById(id.menu_light);
            item.setVisible(false);
            Toast.makeText(MainActivity.this,
                    "dark mode is on",
                    Toast.LENGTH_SHORT).show();
        }
    }
//light mode
    public void lightmode(MenuItem item) {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            // Turning on the light mode
            WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
            MenuItem menuItem = (MenuItem) findViewById(id.menu_dark);
            item.setVisible(false);
            this.invalidateOptionsMenu();
            Toast.makeText(MainActivity.this,
                    "light mode is on",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    public void aboutme(MenuItem item) {
        Intent intent = new Intent(MainActivity.this,aboutme.class);
        startActivity(intent);
//        setContentView(layout.aboutme);
    }


    private class Browser_home extends WebViewClient {

        Browser_home() {}

        @SuppressWarnings("deprecation")
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

            return AdBlockerWebView.blockAds(view,url) ? AdBlocker.createEmptyResource() :
                    super.shouldInterceptRequest(view, url);
        }

    }
    //setting action bar as marquee not working
//    private void setActionBarTitleAsMarquee(){
//        // Get Action Bar's title
//        View v = getWindow().getDecorView();
//        int resId = getResources().getIdentifier("action_bar_title", "id", "android");
//        TextView title = (TextView) v.findViewById(resId);
//
//        // Set the ellipsize mode to MARQUEE and make it scroll only once
//        title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        title.setMarqueeRepeatLimit(-1);
//
//        // In order to start strolling, it has to be focusable and focused
//        title.setFocusable(true);
//        title.setFocusableInTouchMode(true);
//        title.requestFocus();
//    }
}