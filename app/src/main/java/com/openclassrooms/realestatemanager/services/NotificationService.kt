package com.openclassrooms.realestatemanager.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.ui.list.ListActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


private const val CHANNEL_ID = "UPLOADS"
const val NOTIFICATION_UPLOAD_ID = 10

class NotificationService @Inject constructor(@ApplicationContext private val mContext: Context) {

    fun createNotification() {
        val resultIntent = Intent(mContext, ListActivity::class.java)

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(mContext).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val largeIcon = BitmapFactory.decodeResource(mContext.resources, R.drawable.ic_building)
        val content = mContext.getString(R.string.all_properties_has_been_uploaded)

        val builder = NotificationCompat.Builder(mContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_apartment)
            .setLargeIcon(largeIcon)
            .setContentTitle(getTitle())
            .setContentText(content)
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = mContext.getString(R.string.channel_name)
            val description: String = mContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                name,
                importance
            )
            channel.description = description
            val notificationManager: NotificationManager =
                mContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationManager = NotificationManagerCompat.from(mContext)

        notificationManager.notify(NOTIFICATION_UPLOAD_ID, builder.build())
    }

    private fun getTitle(): Spannable {
        val title: String = mContext.getString(R.string.notification_title)
        val spTitle: Spannable = SpannableString(title)
        spTitle.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spTitle
    }
}