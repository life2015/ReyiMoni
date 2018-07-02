package cn.edu.twt.retrox.reyimoni.extension

import android.util.Log
import cn.edu.twt.retrox.reyimoni.BuildConfig

fun log(tag: String = "ReyiMoni", content: String) {
    if (BuildConfig.DEBUG) Log.e(tag, content)
}