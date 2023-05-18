package com.cider.fourtytwo

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            if ( /* Check if data needs to be processed by long running job */true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }

        if (remoteMessage.notification != null) {
            Log.d(
                TAG, "Message Notification Body: " + remoteMessage.notification!!
                    .body
            )
            val notificationBody = remoteMessage.notification!!.body
            if (remoteMessage.notification!!.body != null) {
                sendNotification(notificationBody)
            }
        }
    }
    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }
    private fun scheduleJob() {
//        val work: OneTimeWorkRequest = Builder(MyWorker::class.java)
//            .build()
//        WorkManager.getInstance(this).beginWith(work).enqueue()
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendRegistrationToServer(token: String) {
    }

    private fun sendNotification(messageBody: String?) {
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}