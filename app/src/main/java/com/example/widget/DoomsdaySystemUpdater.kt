package com.example.widget

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.Gravity
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.DoomsdaySettings
import com.example.data.GlassPreset
import com.example.data.HorizontalAlignOption
import com.example.data.PreferencesRepository
import com.example.data.VerticalAlignOption
import com.example.domain.CountdownCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DoomsdaySystemUpdater {

    const val ACTION_REFRESH_DOOMSDAY = "com.example.action.REFRESH_DOOMSDAY"
    private const val NOTIFICATION_CHANNEL_ID = "doomsday_lockscreen_channel"
    private const val NOTIFICATION_ID = 20261218
    private const val ALARM_REQUEST_CODE = 181226

    fun triggerImmediateRefresh(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            refreshAllNow(context.applicationContext)
        }
    }

    suspend fun refreshAllNow(context: Context) {
        val repo = PreferencesRepository(context)
        val settings = repo.getSnapshot()
        val snapshot = CountdownCalculator.calculate()

        updateCompactWidgets(context, settings)
        updateLargeWidgets(context, settings)

        if (settings.lockScreenNotificationEnabled) {
            postOrUpdateLockScreenNotification(context, settings)
        } else {
            cancelLockScreenNotification(context)
        }

        // Schedule next minute boundary refresh if any widget is placed or lock notification is active
        if (hasActiveWidgets(context) || settings.lockScreenNotificationEnabled) {
            scheduleNextMinuteTick(context)
        } else {
            cancelMinuteTick(context)
        }
    }

    fun hasActiveWidgets(context: Context): Boolean {
        val manager = AppWidgetManager.getInstance(context)
        val compactIds = manager.getAppWidgetIds(ComponentName(context, CompactWidgetProvider::class.java))
        val largeIds = manager.getAppWidgetIds(ComponentName(context, LargeWidgetProvider::class.java))
        return compactIds.isNotEmpty() || largeIds.isNotEmpty()
    }

    fun getCompactWidgetCount(context: Context): Int {
        val manager = AppWidgetManager.getInstance(context)
        return manager.getAppWidgetIds(ComponentName(context, CompactWidgetProvider::class.java)).size
    }

    fun getLargeWidgetCount(context: Context): Int {
        val manager = AppWidgetManager.getInstance(context)
        return manager.getAppWidgetIds(ComponentName(context, LargeWidgetProvider::class.java)).size
    }

    fun updateCompactWidgets(context: Context, settings: DoomsdaySettings) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, CompactWidgetProvider::class.java))
        if (ids.isEmpty()) return

        val snapshot = CountdownCalculator.calculate()
        val bgRes = resolveWidgetBackgroundDrawable(settings.preset)
        val gravity = resolveGravity(settings.horizontalAlignment, settings.verticalAlignment)
        val textColor = applyAlphaToColor(settings.textColorHex.toInt(), settings.textOpacity)

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            101,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.widget_doomsday_compact)
            views.setInt(R.id.widget_glass_card, "setBackgroundResource", bgRes)
            views.setInt(R.id.widget_glass_card, "setGravity", gravity)
            views.setTextViewText(R.id.widget_title_text, "DOOMSDAY")
            views.setTextViewText(R.id.widget_countdown_digits, snapshot.formattedColonString)
            views.setTextViewText(
                R.id.widget_unit_labels,
                if (snapshot.isNegative) "ELAPSED  •  M | D | H | M | S" else "M  |  D  |  H  |  M  |  S"
            )
            views.setTextColor(R.id.widget_countdown_digits, textColor)
            val scaledSize = (settings.fontSizeSp * 0.62f).coerceIn(13f, 24f)
            views.setFloat(R.id.widget_countdown_digits, "setTextSize", scaledSize)
            views.setOnClickPendingIntent(R.id.widget_root_container, pendingIntent)
            manager.updateAppWidget(id, views)
        }
    }

    fun updateLargeWidgets(context: Context, settings: DoomsdaySettings) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, LargeWidgetProvider::class.java))
        if (ids.isEmpty()) return

        val snapshot = CountdownCalculator.calculate()
        val bgRes = resolveWidgetBackgroundDrawable(settings.preset)
        val gravity = resolveGravity(settings.horizontalAlignment, settings.verticalAlignment)
        val textColor = applyAlphaToColor(settings.textColorHex.toInt(), settings.textOpacity)

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            102,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.widget_doomsday_large)
            views.setInt(R.id.widget_large_glass_card, "setBackgroundResource", bgRes)
            views.setInt(R.id.widget_large_glass_card, "setGravity", gravity)
            views.setTextViewText(R.id.widget_large_title, "DOOMSDAY")
            views.setTextViewText(R.id.widget_large_row_one, snapshot.formattedLargeRowOne)
            views.setTextViewText(R.id.widget_large_row_two, snapshot.formattedLargeRowTwo)
            views.setTextViewText(
                R.id.widget_large_subtitle,
                if (snapshot.isNegative) "TARGET PASSED • ELAPSED SINCE 18 DEC 2026 IST"
                else "TARGET • 18 DEC 2026 • 00:00:00 IST"
            )
            views.setTextColor(R.id.widget_large_row_one, textColor)
            val scaledPrimary = (settings.fontSizeSp * 0.65f).coerceIn(14f, 26f)
            views.setFloat(R.id.widget_large_row_one, "setTextSize", scaledPrimary)
            views.setOnClickPendingIntent(R.id.widget_large_root, pendingIntent)
            manager.updateAppWidget(id, views)
        }
    }

    fun postOrUpdateLockScreenNotification(context: Context, settings: DoomsdaySettings) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.lock_notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.lock_notification_channel_desc)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val snapshot = CountdownCalculator.calculate()
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            201,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (snapshot.isNegative) {
            "DOOMSDAY • ELAPSED TIME"
        } else {
            "DOOMSDAY • ${snapshot.monthsStr}M : ${snapshot.daysStr}D : ${snapshot.hoursStr}H : ${snapshot.minutesStr}M"
        }

        val bigText = buildString {
            append(snapshot.formattedColonString)
            append("\n")
            append("Month | Days | Hours | Minutes | Seconds")
            append("\n")
            append("Target: 18 Dec 2026, 12:00 AM IST")
        }

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_doomsday_notif)
            .setContentTitle(title)
            .setContentText("${snapshot.formattedLargeRowOne} • ${snapshot.hoursStr}h ${snapshot.minutesStr}m")
            .setSubText("18 DEC 2026 IST")
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setUsesChronometer(true)
            .setChronometerCountDown(!snapshot.isNegative)
            .setWhen(snapshot.targetEpochMillis)
            .setContentIntent(pendingIntent)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun cancelLockScreenNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun scheduleNextMinuteTick(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimeAndBootReceiver::class.java).apply {
            action = ACTION_REFRESH_DOOMSDAY
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = System.currentTimeMillis()
        val nextMinute = ((now / 60_000L) + 1L) * 60_000L
        // Use inexact window to be 100% battery-safe and require zero special exact-alarm permissions
        alarmManager.setWindow(
            AlarmManager.RTC,
            nextMinute,
            15_000L,
            pendingIntent
        )
    }

    fun cancelMinuteTick(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimeAndBootReceiver::class.java).apply {
            action = ACTION_REFRESH_DOOMSDAY
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun resolveWidgetBackgroundDrawable(preset: GlassPreset): Int {
        return when (preset) {
            GlassPreset.DARK_GLASS, GlassPreset.FROSTED_GLASS -> R.drawable.bg_widget_glass_dark
            GlassPreset.PURE_GLASS, GlassPreset.SILVER_GLASS -> R.drawable.bg_widget_glass_pure
            GlassPreset.MIDNIGHT -> R.drawable.bg_widget_glass_midnight
            GlassPreset.TRANSPARENT_MINIMAL -> R.drawable.bg_widget_glass_transparent
        }
    }

    private fun resolveGravity(
        horizontal: HorizontalAlignOption,
        vertical: VerticalAlignOption
    ): Int {
        val h = when (horizontal) {
            HorizontalAlignOption.LEFT -> Gravity.START
            HorizontalAlignOption.CENTER -> Gravity.CENTER_HORIZONTAL
            HorizontalAlignOption.RIGHT -> Gravity.END
        }
        val v = when (vertical) {
            VerticalAlignOption.TOP -> Gravity.TOP
            VerticalAlignOption.CENTER -> Gravity.CENTER_VERTICAL
            VerticalAlignOption.BOTTOM -> Gravity.BOTTOM
        }
        return h or v
    }

    private fun applyAlphaToColor(rgbHex: Int, opacity: Float): Int {
        val alpha = (opacity.coerceIn(0.15f, 1.0f) * 255).toInt()
        return (rgbHex and 0x00FFFFFF) or (alpha shl 24)
    }
}
