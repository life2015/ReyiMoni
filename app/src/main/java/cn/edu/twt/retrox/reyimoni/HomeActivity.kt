package cn.edu.twt.retrox.reyimoni

import android.app.Notification
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import cn.edu.twt.retrox.reyimoni.model.CookieStore
import cn.edu.twt.retrox.reyimoni.model.Message
import cn.edu.twt.retrox.reyimoni.model.MoniManager
import cn.edu.twt.retrox.reyimoni.model.RequestClient
import cn.edu.twt.retrox.reyimoni.service.ReyiMoniService
import org.jetbrains.anko.*

class HomeActivity : AppCompatActivity() {
    var notiId = 2
    val notificationGroupKey = "Group1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scrollView {
            verticalLayout {
                button("Connect 开始守护空降") {
                    setOnClickListener {
                        startService(Intent(this@HomeActivity, ReyiMoniService::class.java))
                    }
                }
                button("DisConnect") {
                    setOnClickListener {
                        stopService(Intent(this@HomeActivity, ReyiMoniService::class.java))
                    }
                }
                button("测试通知") {
                    setOnClickListener {
                        val message = Message("Retrox-", "测试群组", "报告 本人在深海作业过程中不慎跌出船舱  现已被送去抢救  下次打捞任务再回归[doge]＿" +
                                "报告 本人在深海作业过程中不慎跌出船舱  现已被送去抢救  下次打捞任务再回归[doge]＿" +
                                "报告 本人在深海作业过程中不慎跌出船舱  现已被送去抢救  下次打捞任务再回归[doge]＿")
                        val detailText = "空降了！ ${message.userID} 在 ${message.groupName} 说: ${message.content}"
                        val notificationBuilder = NotificationCompat.Builder(this@HomeActivity, "ReyiChatMessage")
                                .setContentTitle("${message.userID}空降了！")
                                .setContentText(detailText)
                                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_foreground_noti))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.ic_launcher_foreground_noti)
                                .setStyle(NotificationCompat.BigTextStyle().bigText(detailText))
                                .setGroup(notificationGroupKey)
                        val notification = notificationBuilder.build()
                        notification.flags = Notification.FLAG_INSISTENT
                        notificationManager.notify(notiId, notification)
                        notiId++

                        val groupNotification = NotificationCompat.Builder(this@HomeActivity, "ReyiChatMessage")
                                .setSmallIcon(R.drawable.ic_launcher_foreground_noti)
                                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_foreground_noti))
                                .setContentTitle("Reyi守护")
                                .setContentText("收到了消息")
                                .setGroupSummary(true)
                                .setGroup(notificationGroupKey)
                                .build()

                        notificationManager.notify(-1, groupNotification)
                    }
                }
                button("绑定或者刷新账号") {
                    setOnClickListener {
                        val intent = Intent(this@HomeActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
                val statusText = textView {
                    setTextIsSelectable(true)
                    text = getStausText()
                }.lparams {
                    horizontalMargin = dip(8)
                }
                button("刷新状态") {
                    setOnClickListener {
                        statusText.text = getStausText()
                    }
                }
            }
        }
    }

    fun getStausText() = "状态报告：\n" +
            "观察状态栏通知情况来查看服务是否被启动\n " +
            "在绑定账号页面里面登录微博，在里面点击到微博群 即可出现弹窗，里面输入你关注的ID 比如说 刘人语Reyi_ 然后点击确认即可 修改或者增加人物关注后 需要重新Disconnect -> Connect " +
            "目前的守护：\n ${MoniManager.options.map { it.value }.map { "${it.vipName} -> ${it.groupName} " }}" +
            "消息池数量：${RequestClient.cacheList.size} \n" +
            "Cookie状态：${CookieStore.cookie}"

}