package cn.edu.twt.retrox.reyimoni.model

import android.util.Log
import cn.edu.twt.retrox.reyimoni.extension.log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

object RequestClient {
    val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build()
    val cacheList = mutableListOf<Message>()

    fun startMonitor(errorHandler: (Exception) -> Unit, sucessHandler: (Message) -> Unit, updateHandler: (String) -> Unit = {}): Future<Unit> {
        val options = MoniManager.options
        val cookie = CookieStore.cookie
        val typeToken = object : TypeToken<List<WeiboMessage>>() {}
        val gson = Gson()

        val future = doAsync {
            while (true) {
                try {
                    options.forEach { group_id, option ->
                        log("Moni", "Option: $option")
                        val builder = Request.Builder().url("https://m.weibo.cn/groupChat/userChat/queryMsg?group_id=${option.groupID}&format=cards")
                        option.headers.forEach { t, u ->
                            builder.addHeader(t, u)
                        }
                        builder.addHeader("Cookie", cookie)
                        val request = builder.build()
                        val response = client.newCall(request).execute()
                        val responseString = response.body()!!.string()
                        val messageList = gson.fromJson<List<WeiboMessage>>(responseString, typeToken.type)
                        messageList.forEach {
                            val message = Message(it.userInfo.screenName, option.groupName, it.text)
                            if (message !in cacheList) {
                                cacheList.add(message)
                                log("UserID", "说话: $message")
                                if (message.userID == option.vipName) {
                                    // 出现了
                                    log("Moni", "出现了: $message")
                                    sucessHandler.invoke(message)
                                }
                            }
                        }

                        updateHandler.invoke("正在刷新:${option.groupName} 消息池:${cacheList.size}")
                        Thread.sleep(4000L) // 微博制裁 只能这个频率
                    }
                } catch (e: Exception) {
                    if (e is InterruptedException) throw e else errorHandler.invoke(e)
                }
            }
        }

        return future

    }
}