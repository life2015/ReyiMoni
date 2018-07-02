package cn.edu.twt.retrox.reyimoni

import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.webkit.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import cn.edu.twt.retrox.reyimoni.extension.log
import cn.edu.twt.retrox.reyimoni.model.MoniManager
import cn.edu.twt.retrox.reyimoni.model.MoniOption
import org.jetbrains.anko.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    lateinit var mWebView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webView = findViewById<WebView>(R.id.webview)
        val refreshBtn: Button = findViewById(R.id.btn_refresh)
        mWebView = webView

        refreshBtn.setOnClickListener {
            mWebView.reload()
        }
        webView.loadUrl("https://m.weibo.cn")
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (request.url.toString().contains("groupChat")) return super.shouldOverrideUrlLoading(view, request) // 蜜汁验证问题
                view.loadUrl(request.url.toString(), request.requestHeaders)
                return true
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest): WebResourceResponse? {
                val lastpath = request.url.lastPathSegment
                if (lastpath == "chat") {
                    val handler = Handler(mainLooper)
                    handler.postDelayed({
                        val getNameJs = assets.open("getName.js").bufferedReader().readText()

                        webView.execJs(getNameJs) { chattitle ->
                            alert {
                                title = "检测到聊天"
                                message = "id: ${request.url.getQueryParameter("group_id")} title; $chattitle"
                                var vipEditText: EditText by Delegates.notNull()
                                customView {
                                    vipEditText = editText {
                                        hint = "输入关注的名字"
                                        horizontalPadding = dip(16)
                                    }
                                }
                                positiveButton("ok") {
                                    val text = vipEditText.text.toString()
                                    Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                                    MoniManager.addOption(MoniOption(
                                            request.url.getQueryParameter("group_id"),
                                            chattitle ?: "FUck",
                                            text,
                                            request.requestHeaders
                                    ))
                                }

                            }.show()
                        }

                    }, 2500)
                }
                log("WebRequestUrl", request.url.toString() + request.requestHeaders?.toString())
                return null
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }

        }
        return super.onKeyDown(keyCode, event)
    }

    fun WebView.execJs(script: String, callback: (String?) -> Unit) {
        evaluateJavascript("javascript:(function(){$script})()", callback)
    }
}
