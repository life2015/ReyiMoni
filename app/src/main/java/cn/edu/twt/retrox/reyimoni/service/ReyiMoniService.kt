package cn.edu.twt.retrox.reyimoni.service

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import cn.edu.twt.retrox.reyimoni.R
import cn.edu.twt.retrox.reyimoni.extension.log
import cn.edu.twt.retrox.reyimoni.model.RequestClient
import java.util.concurrent.Future

class ReyiMoniService : Service() {

    var notiId = 2
    val notificationGroupKey = "cn.edu.twt.retrox.reyimoni.service.ReyiMoniService.NotificationKey"
    var future: Future<Unit>? = null
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "ReyiMoni"
            val channelName = "Reyi守护服务"
            val importance = NotificationManager.IMPORTANCE_MIN

            createNotificationChannel(channelId, channelName, importance)

            createNotificationChannel("ReyiChatMessage", "Reyi消息提醒", NotificationManager.IMPORTANCE_MAX)

            createNotificationChannel("ServiceErrorMessage", "守护程序错误提示", NotificationManager.IMPORTANCE_LOW)

        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationManager = NotificationManagerCompat.from(this)
        postStatusNoti("Connecting")

        future = RequestClient.startMonitor(
                errorHandler = {
                    it.printStackTrace()
                    val notificationBuilder = NotificationCompat.Builder(this, "ServiceErrorMessage")
                            .setContentTitle("守护程序出现错误")
                            .setContentText(it.toString())
                            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_foreground_noti))
                            .setPriority(NotificationCompat.PRIORITY_LOW)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_launcher_foreground_noti)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(it.toString()))
                    val notification = notificationBuilder.build()
                    notificationManager.notify(-2, notification)
                },
                sucessHandler = {
                    val detailText = "${it.userID} 在 ${it.groupName} 说: ${it.content}"
                    val notificationBuilder = NotificationCompat.Builder(this, "ReyiChatMessage")
                            .setContentTitle("${it.userID}空降了！")
                            .setContentText(detailText)
                            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_foreground_noti))
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_launcher_foreground_noti)
                            .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle("${it.userID}空降了！").bigText(detailText))
                            .setGroup(notificationGroupKey)
                    val notification = notificationBuilder.build()
                    notification.flags = Notification.FLAG_INSISTENT
                    notificationManager.notify(notiId, notification)
                    notiId++

                    val groupNotification = NotificationCompat.Builder(this, "ReyiChatMessage")
                            .setSmallIcon(R.drawable.ic_launcher_foreground_noti)
                            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_foreground_noti))
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setContentTitle("Reyi守护")
                            .setContentText("收到了消息")
                            .setGroupSummary(true)
                            .setGroup(notificationGroupKey)
                            .build()

                    groupNotification.flags = NotificationCompat.FLAG_INSISTENT
                    notificationManager.notify(-1, groupNotification)

                },
                updateHandler = {
                    log("Service", it)
                    postStatusNoti(it)
                }
        )

        return START_STICKY
    }

    fun postStatusNoti(message: String) {
        val notification = NotificationCompat.Builder(this, "ReyiMoni")
                .setContentTitle("Reyi守护")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground_noti)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_foreground_noti))
                .build()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        future?.cancel(true)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}