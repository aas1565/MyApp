package com.example.hw_3

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

fun scheduleFavoritePairAlarm(context: Context, name: String, time: String) {
    val parts = time.split(":")
    if (parts.size != 2) return
    val hour = parts[0].toIntOrNull() ?: return
    val minute = parts[1].toIntOrNull() ?: return

    val now = Calendar.getInstance()
    val triggerTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // Если время уже прошло сегодня, переносим на следующий день
    if (triggerTime.before(now)) {
        triggerTime.add(Calendar.DAY_OF_YEAR, 1)
    }

    val intent = Intent(context, FavoritePairReceiver::class.java).apply {
        putExtra(FavoritePairReceiver.EXTRA_NAME, name)
        putExtra(FavoritePairReceiver.EXTRA_TIME, time)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        2002,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Intent для показа информации об аларме (требуется для setAlarmClock)
    val openAppIntent = Intent(context, MainActivity::class.java)
    val showPendingIntent = PendingIntent.getActivity(
        context,
        2003,
        openAppIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val canUseExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }

    if (canUseExact) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime.timeInMillis,
            pendingIntent
        )
    } else {
        // Фолбэк: setAlarmClock работает как точный и разрешён без специального права
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime.timeInMillis, showPendingIntent)
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }
}