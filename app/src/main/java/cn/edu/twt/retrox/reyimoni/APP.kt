package cn.edu.twt.retrox.reyimoni

import android.app.Application
import com.orhanobut.hawk.Hawk
import com.tencent.bugly.Bugly

class APP : Application() {
    override fun onCreate() {
        super.onCreate()
        Bugly.init(applicationContext, "eb55730e9b", false);
        Hawk.init(this).build()
    }
}