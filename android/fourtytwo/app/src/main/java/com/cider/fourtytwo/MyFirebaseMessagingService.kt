package com.cider.fourtytwo

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
//    /**
//     * Called when message is received.
//     *
//     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
//     */
//    // [START receive_message]
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        // [START_EXCLUDE]
//        // There are two types of messages data messages and notification messages. Data messages
//        // are handled
//        // here in onMessageReceived whether the app is in the foreground or background. Data
//        // messages are the type
//        // traditionally used with GCM. Notification messages are only received here in
//        // onMessageReceived when the app
//        // is in the foreground. When the app is in the background an automatically generated
//        // notification is displayed.
//        // When the user taps on the notification they are returned to the app. Messages
//        // containing both notification
//        // and data payloads are treated as notification messages. The Firebase console always
//        // sends notification
//        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
//        // [END_EXCLUDE]
//
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: " + remoteMessage.from)
//
//        // Check if message contains a data payload.
//        if (remoteMessage.data.size > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.data)
//            if ( /* Check if data needs to be processed by long running job */true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.notification != null) {
//            Log.d(
//                TAG, "Message Notification Body: " + remoteMessage.notification!!
//                    .body
//            )
//            val notificationBody = remoteMessage.notification!!.body
//            if (remoteMessage.notification!!.body != null) {
//                sendNotification(notificationBody)
//            }
//        }
//
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated. See sendNotification method below.
//    }
//    // [END receive_message]
//    // [START on_new_token]
//    /**
//     * There are two scenarios when onNewToken is called:
//     * 1) When a new token is generated on initial app startup
//     * 2) Whenever an existing token is changed
//     * Under #2, there are three scenarios when the existing token is changed:
//     * A) App is restored to a new device
//     * B) User uninstalls/reinstalls the app
//     * C) User clears app data
//     */
//    override fun onNewToken(token: String) {
//        Log.d(TAG, "Refreshed token: $token")
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // FCM registration token to your app server.
//        sendRegistrationToServer(token)
//    }
//    // [END on_new_token]
//    /**
//     * Schedule async work using WorkManager.
//     */
//    private fun scheduleJob() {
//        // [START dispatch_job]
//        val work: OneTimeWorkRequest = Builder(MyWorker::class.java)
//            .build()
//        WorkManager.getInstance(this).beginWith(work).enqueue()
//        // [END dispatch_job]
//    }
//
//    /**
//     * Handle time allotted to BroadcastReceivers.
//     */
//    private fun handleNow() {
//        Log.d(TAG, "Short lived task is done.")
//    }
//
//    /**
//     * Persist token to third-party servers.
//     *
//     * Modify this method to associate the user's FCM registration token with any
//     * server-side account maintained by your application.
//     *
//     * @param token The new token.
//     */
//    private fun sendRegistrationToServer(token: String) {
//        // TODO: Implement this method to send token to your app server.
//    }
//
//    /**
//     * Create and show a simple notification containing the received FCM message.
//     *
//     * @param messageBody FCM message body received.
//     */
//    private fun sendNotification(messageBody: String?) {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0 /* Request code */, intent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//        val channelId = getString(R.string.default_notification_channel_id)
//        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder: NotificationCompat.Builder =
//            NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.ic_stat_ic_notification)
//                .setContentTitle(getString(R.string.fcm_message))
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//        val notificationManager =
//            getSystemService<Any>(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Channel human readable title",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
//    }
//
//    companion object {
//        private const val TAG = "MyFirebaseMsgService"
//    }
}
//
//class MyFirebaseMessagingService : FirebaseMessagingService() {
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Log.d(TAG, "onNewToken: ${token}")
//    }
//
//    /**
//     * 디바이스가 FCM을 통해서 메시지를 받으면 수행된다.
//     * @remoteMessage: FCM에서 보낸 데이터 정보들을 저장한다.
//     */
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//
//        // FCM을 통해서 전달 받은 정보에 Notification 정보가 있는 경우 알림을 생성한다.
//        if (remoteMessage.notification != null){
//            sendNotification(remoteMessage)
//        }else{
//            Log.d(TAG, "수신 에러: Notification이 비어있습니다.")
//        }
//    }
//
//    /**
//     * FCM에서 보낸 정보를 바탕으로 디바이스에 Notification을 생성한다.
//     * @remoteMessage: FCM에서 보
//     */
//    private fun sendNotification(remoteMessage: RemoteMessage){
//        val id = 0
//        var title = remoteMessage.notification!!.title
//        var body = remoteMessage.notification!!.body
//
//        var intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_ONE_SHOT)
//
//        val channelId = "Channel ID"
//        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle(title)
//            .setContentText(body)
//            .setAutoCancel(true)
//            .setSound(soundUri)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_HIGH)
//
//        notificationManager.createNotificationChannel(channel)
//        notificationManager.notify(id, notificationBuilder.build())
//    }
//}
//}