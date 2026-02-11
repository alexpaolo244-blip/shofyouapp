package com.shofyou.android

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isConnected()) {
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        swipeRefresh = findViewById(R.id.swipeRefresh)

        CookieManager.getInstance().setAcceptCookie(true)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            mediaPlaybackRequiresUserGesture = false
            databaseEnabled = true
        }

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                showPopup(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                swipeRefresh.isRefreshing = false

                // تعطيل السحب فقط في صفحة الريلز
                if (url != null && url.contains("reels")) {
                    swipeRefresh.isEnabled = false
                } else {
                    swipeRefresh.isEnabled = true
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@MainActivity.filePathCallback = filePathCallback
                startActivityForResult(fileChooserParams?.createIntent(), 100)
                return true
            }

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                customView = view
                customViewCallback = callback
                setContentView(view)
            }

            override fun onHideCustomView() {
                customView = null
                customViewCallback?.onCustomViewHidden()
                setContentView(R.layout.activity_main)
            }
        }

        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }

        webView.loadUrl("https://shofyou.com")
    }

    private fun showPopup(url: String) {
        val dialog = Dialog(this)
        val popupWebView = WebView(this)

        popupWebView.settings.javaScriptEnabled = true
        popupWebView.loadUrl(url)

        dialog.setContentView(popupWebView)
        dialog.show()
    }

    private fun isConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }
}
