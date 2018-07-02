package cn.edu.twt.retrox.reyimoni.model

import android.webkit.CookieManager
import com.orhanobut.hawk.Hawk

/**
 * 放在请求头里面
 */
object CookieStore {
    val cookie: String
        get() {
            return CookieManager.getInstance().getCookie("m.weibo.cn") ?: ""
        }
}

data class Message(val userID: String, val groupName: String, val content: String)

data class MoniOption(val groupID: String, val groupName: String, val vipName: String, val headers: Map<String, String>)

object MoniManager {
    val options: Map<String, MoniOption>
        get() = _options

    private val _options = Hawk.get("MoniOptions", HashMap<String, MoniOption>())

    fun addOption(option: MoniOption) {
        _options.put(option.groupID, option)
        Hawk.put("MoniOptions", _options)
    }

    fun removeOption(option: MoniOption) {
        _options.remove(option.groupID)
        Hawk.put("MoniOptions", _options)

    }

}

