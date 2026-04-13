package com.example.memomind.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.memomind.data.local.dao.CardDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cardDao: CardDao,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "memomind_reminder"
        const val NOTIFICATION_ID = 1001

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(
                1, TimeUnit.DAYS,
            )
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "daily_reminder",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request,
                )
        }

        private fun calculateInitialDelay(): Long {
            val now = java.util.Calendar.getInstance()
            val target = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 8)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                if (before(now)) add(java.util.Calendar.DAY_OF_MONTH, 1)
            }
            return target.timeInMillis - now.timeInMillis
        }
    }

    override suspend fun doWork(): Result {
        val reviewCount = cardDao.getTotalReviewCount(System.currentTimeMillis())
        if (reviewCount > 0) {
            createNotificationChannel()
            showNotification(reviewCount)
        }
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Nhắc nhở ôn tập",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Nhắc nhở ôn tập flashcard hàng ngày"
            }
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(count: Int) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("MemoMind - Ôn tập")
            .setContentText("Bạn có $count thẻ cần ôn tập hôm nay!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }
}