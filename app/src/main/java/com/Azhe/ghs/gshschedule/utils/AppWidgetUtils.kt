package com.Azhe.ghs.gshschedule.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.SplashActivity
import com.Azhe.ghs.gshschedule.bean.TableBean
import com.Azhe.ghs.gshschedule.schedule_appwidget.ScheduleAppWidget
import com.Azhe.ghs.gshschedule.schedule_appwidget.ScheduleAppWidgetService
import com.Azhe.ghs.gshschedule.today_appwidget.TodayCourseAppWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun BroadcastReceiver.goAsync(
    coroutineScope: CoroutineScope = GlobalScope,
    block: suspend () -> Unit
) {
    val result = goAsync()
    coroutineScope.launch {
        try {
            block()
        } finally {
            result.finish()
        }
    }
}

object AppWidgetUtils {
    private val daysArray = arrayOf("日", "一", "二", "三", "四", "五", "六", "日")

    /**
     * Broadcast APPWIDGET_UPDATE to all widget instances so
     * their onUpdate fires and refreshes from stored JSON.
     */
    fun updateWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        try {
            val weekComponent = android.content.ComponentName(context, ScheduleAppWidget::class.java)
            val weekIds = appWidgetManager.getAppWidgetIds(weekComponent)
            if (weekIds.isNotEmpty()) {
                val weekIntent = Intent(context, ScheduleAppWidget::class.java)
                weekIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                weekIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, weekIds)
                context.sendBroadcast(weekIntent)
            }
        } catch (_: Exception) {}
        try {
            val todayComponent = android.content.ComponentName(context, TodayCourseAppWidget::class.java)
            val todayIds = appWidgetManager.getAppWidgetIds(todayComponent)
            if (todayIds.isNotEmpty()) {
                val todayIntent = Intent(context, TodayCourseAppWidget::class.java)
                todayIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                todayIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, todayIds)
                context.sendBroadcast(todayIntent)
            }
        } catch (_: Exception) {}
    }

    /**
     * Convert Room DB courses → JSON and trigger today-widget refresh.
     * Maintains compatibility with existing callers (WeekScheduleAppWidgetConfigActivity,
     * ScheduleSettingsActivity, ScheduleActivity).
     */
    @Suppress("UNUSED_PARAMETER")
    suspend fun refreshTodayWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, table: TableBean? = null, nextDay: Boolean = false) {
        TodayWidgetDataManager.convertAndStore(context)
        val intent = Intent(context, TodayCourseAppWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
        context.sendBroadcast(intent)
    }

    // ── Week schedule widget (unchanged) ─────────────────────

    fun refreshScheduleWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, tableBean: TableBean, nextWeek: Boolean = false) {
        val mRemoteViews = RemoteViews(context.packageName, R.layout.schedule_app_widget)
        var week = CourseUtils.countWeek(tableBean.startDate, tableBean.sundayFirst)
        if (nextWeek) week++
        val date = CourseUtils.getTodayDate()
        val weekDay = CourseUtils.getWeekday()
        mRemoteViews.setTextViewTextSize(R.id.tv_date, TypedValue.COMPLEX_UNIT_SP, tableBean.widgetItemTextSize.toFloat() + 2)
        mRemoteViews.setTextViewTextSize(R.id.tv_week, TypedValue.COMPLEX_UNIT_SP, tableBean.widgetItemTextSize.toFloat())
        mRemoteViews.setTextViewText(R.id.tv_date, date)
        if (tableBean.tableName.isEmpty()) tableBean.tableName = "??? "
        var notStart = false
        if (week > 0) {
            if (nextWeek) {
                mRemoteViews.setTextViewText(R.id.tv_week, "${tableBean.tableName} |  No.${week} ")
            } else {
                mRemoteViews.setTextViewText(R.id.tv_week, "${tableBean.tableName} |  No.${week}     $weekDay")
            }
        } else {
            mRemoteViews.setTextViewText(R.id.tv_week, "${tableBean.tableName} |  ??? ")
            week = 1
            notStart = true
        }
        if (tableBean.showSun) {
            if (tableBean.sundayFirst) {
                mRemoteViews.setViewVisibility(R.id.tv_title7, View.GONE)
                mRemoteViews.setViewVisibility(R.id.tv_title0_1, View.VISIBLE)
            } else {
                mRemoteViews.setViewVisibility(R.id.tv_title7, View.VISIBLE)
                mRemoteViews.setViewVisibility(R.id.tv_title0_1, View.GONE)
            }
        } else {
            mRemoteViews.setViewVisibility(R.id.tv_title7, View.GONE)
            mRemoteViews.setViewVisibility(R.id.tv_title0_1, View.GONE)
        }
        if (tableBean.showSat) {
            mRemoteViews.setViewVisibility(R.id.tv_title6, View.VISIBLE)
        } else {
            mRemoteViews.setViewVisibility(R.id.tv_title6, View.GONE)
        }
        mRemoteViews.setTextColor(R.id.tv_date, tableBean.widgetTextColor)
        mRemoteViews.setTextColor(R.id.tv_week, tableBean.widgetTextColor)
        mRemoteViews.setInt(R.id.iv_next, "setColorFilter", tableBean.widgetTextColor)
        mRemoteViews.setInt(R.id.iv_back, "setColorFilter", tableBean.widgetTextColor)
        val weekDate = CourseUtils.getDateStringFromWeek(CourseUtils.countWeek(tableBean.startDate, tableBean.sundayFirst), week, tableBean.sundayFirst)
        mRemoteViews.setTextColor(R.id.tv_title0, tableBean.widgetTextColor)
        mRemoteViews.setTextViewTextSize(R.id.tv_title0, TypedValue.COMPLEX_UNIT_SP, tableBean.widgetItemTextSize.toFloat())
        mRemoteViews.setTextViewText(R.id.tv_title0, weekDate[0] + "\n?")
        if (nextWeek) {
            if (!notStart) mRemoteViews.setTextViewText(R.id.tv_date, "??")
            mRemoteViews.setViewVisibility(R.id.iv_next, View.GONE)
            mRemoteViews.setViewVisibility(R.id.iv_back, View.VISIBLE)
        } else {
            mRemoteViews.setTextViewText(R.id.tv_date, date)
            mRemoteViews.setViewVisibility(R.id.iv_next, View.VISIBLE)
            mRemoteViews.setViewVisibility(R.id.iv_back, View.GONE)
        }
        val day = CourseUtils.getWeekdayInt()
        if (tableBean.sundayFirst) {
            for (i in 0..6) {
                if (i == day || (i == 0 && day == 7)) {
                    mRemoteViews.setTextColor(R.id.tv_title0_1 + i, tableBean.widgetTextColor)
                } else {
                    mRemoteViews.setTextColor(R.id.tv_title0_1 + i, (tableBean.widgetTextColor and 0x00ffffff) + 0x33000000)
                }
                mRemoteViews.setTextViewTextSize(R.id.tv_title0_1 + i, TypedValue.COMPLEX_UNIT_SP, tableBean.widgetItemTextSize.toFloat())
                mRemoteViews.setTextViewText(R.id.tv_title0_1 + i, daysArray[i] + "\n${weekDate[i + 1]}")
            }
        } else {
            for (i in 0..6) {
                if (i == day - 1) {
                    mRemoteViews.setTextColor(R.id.tv_title1 + i, tableBean.widgetTextColor)
                } else {
                    mRemoteViews.setTextColor(R.id.tv_title1 + i, (tableBean.widgetTextColor and 0x00ffffff) + 0x33000000)
                }
                mRemoteViews.setTextViewTextSize(R.id.tv_title1 + i, TypedValue.COMPLEX_UNIT_SP, tableBean.widgetItemTextSize.toFloat())
                mRemoteViews.setTextViewText(R.id.tv_title1 + i, daysArray[i + 1] + "\n${weekDate[i + 1]}")
            }
        }
        val lvIntent = Intent(context, ScheduleAppWidgetService::class.java)
        lvIntent.data = if (nextWeek) Uri.fromParts("content", "1,${tableBean.id}", null)
        else Uri.fromParts("content", "0,${tableBean.id}", null)
        mRemoteViews.setRemoteAdapter(R.id.lv_schedule, lvIntent)
        val intent = Intent(context, SplashActivity::class.java)
        val pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        mRemoteViews.setOnClickPendingIntent(R.id.tv_date, pIntent)
        val nextIntent = Intent(context, ScheduleAppWidget::class.java)
        nextIntent.action = "WAKEUP_NEXT_WEEK"
        val pi = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        mRemoteViews.setOnClickPendingIntent(R.id.iv_next, pi)
        val backIntent = Intent(context, ScheduleAppWidget::class.java)
        backIntent.action = "WAKEUP_BACK_WEEK"
        val backPi = PendingIntent.getBroadcast(context, 2, backIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        mRemoteViews.setOnClickPendingIntent(R.id.iv_back, backPi)
        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_schedule)
    }
}
