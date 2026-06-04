package com.Azhe.ghs.gshschedule.utils

import android.content.Context
import androidx.core.content.edit
import com.Azhe.ghs.gshschedule.AppDatabase
import com.Azhe.ghs.gshschedule.bean.TableBean
import com.Azhe.ghs.gshschedule.bean.TimeDetailBean
import com.Azhe.ghs.gshschedule.bean.TimeTableBean

object UpdateUtils {

    @Throws(Exception::class)
    fun getVersionCode(context: Context): Int {
        val packageManager = context.packageManager
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        val packInfo = packageManager.getPackageInfo(context.packageName, 0)
        return packInfo.versionCode
    }

    @Throws(Exception::class)
    fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        val packInfo = packageManager.getPackageInfo(context.packageName, 0)
        return packInfo.versionName
    }

    suspend fun tranOldData(context: Context) {
        if (context.getPrefer().getBoolean("has_intro", false) &&
                !context.getPrefer().getBoolean("has_adjust", false)) {
            val tableData = TableBean(
                    tableName = "",
                    itemHeight = context.getPrefer().getInt("item_height", 56),
                    maxWeek = context.getPrefer().getInt("sb_weeks", 30),
                    itemTextSize = context.getPrefer().getInt("sb_text_size", 12),
                    showOtherWeekCourse = context.getPrefer().getBoolean("s_show", false),
                    showTime = context.getPrefer().getBoolean("s_show_time_detail", false),
                    showSat = context.getPrefer().getBoolean("s_show_sat", true),
                    showSun = context.getPrefer().getBoolean("s_show_weekend", true),
                    sundayFirst = context.getPrefer().getBoolean("s_sunday_first", false),
                    nodes = context.getPrefer().getInt("classNum", 11),
                    itemAlpha = context.getPrefer().getInt("sb_alpha", 60),
                    background = context.getPrefer().getString(Const.KEY_OLD_VERSION_BG_URI, "")!!,
                    startDate = context.getPrefer().getString(Const.KEY_OLD_VERSION_TERM_START, "2019-02-25")!!,
                    widgetItemAlpha = context.getPrefer().getInt("sb_widget_alpha", 60),
                    widgetItemHeight = context.getPrefer().getInt("widget_item_height", 56),
                    widgetItemTextSize = context.getPrefer().getInt("sb_widget_text_size", 12),
                    type = 1,
                    id = 1)

            if (!context.getPrefer().getBoolean("s_stroke", true)) {
                tableData.strokeColor = 0x00ffffff
            }

            if (context.getPrefer().getBoolean("s_color", false)) {
                tableData.textColor = 0xff000000.toInt()
            }

            if (context.getPrefer().getBoolean("s_widget_color", false)) {
                tableData.widgetTextColor = 0xff000000.toInt()
            }

            val dataBase = AppDatabase.getDatabase(context)
            val tableDao = dataBase.tableDao()
            val timeDao = dataBase.timeDetailDao()
            val widgetDao = dataBase.appWidgetDao()

            try {
                tableDao.updateTable(tableData)
                widgetDao.updateFromOldVer()
                if (!context.getPrefer().getBoolean("isInitTimeTable", false)) {
                    val timeList = ArrayList<TimeDetailBean>().apply {
                        add(TimeDetailBean(1, "08:30", "09:15", 1))
                        add(TimeDetailBean(2, "09:20", "10:05", 1))
                        add(TimeDetailBean(3, "10:20", "11:05", 1))
                        add(TimeDetailBean(4, "11:05", "11:55", 1))
                        add(TimeDetailBean(5, "12:00", "13:00", 1))
                        add(TimeDetailBean(6, "13:00", "13:55", 1))
                        add(TimeDetailBean(7, "14:00", "14:45", 1))
                        add(TimeDetailBean(8, "14:50", "15:35", 1))
                        add(TimeDetailBean(9, "15:50", "16:35", 1))
                        add(TimeDetailBean(10, "16:40", "17:25", 1))
                        add(TimeDetailBean(11, "17:25", "18:20", 1))
                        add(TimeDetailBean(12, "18:20", "19:05", 1))
                        add(TimeDetailBean(13, "19:10", "19:55", 1))
                        add(TimeDetailBean(14, "20:00", "20:45", 1))
                        add(TimeDetailBean(15, "00:00", "00:00", 1))
                    }
                    timeDao.insertTimeList(timeList)
                }

                context.getPrefer().edit {
                    remove("termStart")
                    remove("item_height")
                    remove("sb_weeks")
                    remove("sb_text_size")
                    remove("s_show")
                    remove("s_show_time_detail")
                    remove("s_show_sat")
                    remove("s_show_weekend")
                    remove("s_sunday_first")
                    remove("classNum")
                    remove("sb_alpha")
                    remove("pic_uri")
                    remove("sb_widget_alpha")
                    remove("widget_item_height")
                    remove("sb_widget_text_size")
                    remove("s_stroke")
                    remove("s_color")
                    remove("s_widget_color")
                }

                context.getPrefer().edit {
                    putBoolean(Const.KEY_HAS_ADJUST, true)
                }
            } catch (e: Exception) {

            }

        }

        if (!context.getPrefer().getBoolean("has_intro", false) &&
                !context.getPrefer().getBoolean("has_adjust", false)) {
            val tableData = TableBean(type = 1, id = 1, tableName = "")
            val dataBase = AppDatabase.getDatabase(context)
            val tableDao = dataBase.tableDao()
            val timeDao = dataBase.timeDetailDao()
            val timeTableDao = dataBase.timeTableDao()
            if (timeTableDao.getTimeTable(1) == null) {
                timeTableDao.insertTimeTable(TimeTableBean(id = 1, name = "默认"))
            }
            val timeList = ArrayList<TimeDetailBean>().apply {
                add(TimeDetailBean(1, "08:30", "09:15", 1))
                add(TimeDetailBean(2, "09:20", "10:05", 1))
                add(TimeDetailBean(3, "10:20", "11:05", 1))
                add(TimeDetailBean(4, "11:05", "11:55", 1))
                add(TimeDetailBean(5, "12:00", "13:00", 1))
                add(TimeDetailBean(6, "13:00", "13:55", 1))
                add(TimeDetailBean(7, "14:00", "14:45", 1))
                add(TimeDetailBean(8, "14:50", "15:35", 1))
                add(TimeDetailBean(9, "15:50", "16:35", 1))
                add(TimeDetailBean(10, "16:40", "17:25", 1))
                add(TimeDetailBean(11, "17:25", "18:20", 1))
                add(TimeDetailBean(12, "18:20", "19:05", 1))
                add(TimeDetailBean(13, "19:10", "19:55", 1))
                add(TimeDetailBean(14, "20:00", "20:45", 1))
                add(TimeDetailBean(15, "00:00", "00:00", 1))
            }
            try {
                timeDao.insertTimeList(timeList)
                tableDao.insertTable(tableData)
            } catch (e: Exception) {

            }
            context.getPrefer().edit {
                putBoolean(Const.KEY_HAS_ADJUST, true)
            }
        }

        // v1.1.8: Force reset default time table to standard 45-min schedule
        // Uses delete+insert instead of update for reliability
        if (!context.getPrefer().getBoolean("time_slots_v118", false)) {
            try {
                val dataBase = AppDatabase.getDatabase(context)
                val timeDao = dataBase.timeDetailDao()
                timeDao.deleteByTimeTable(1)
                val newTimeList = ArrayList<TimeDetailBean>().apply {
                    add(TimeDetailBean(1, "08:00", "08:45", 1))
                    add(TimeDetailBean(2, "08:50", "09:35", 1))
                    add(TimeDetailBean(3, "09:50", "10:35", 1))
                    add(TimeDetailBean(4, "10:40", "11:25", 1))
                    add(TimeDetailBean(5, "11:35", "12:20", 1))
                    add(TimeDetailBean(6, "14:00", "14:45", 1))
                    add(TimeDetailBean(7, "14:50", "15:35", 1))
                    add(TimeDetailBean(8, "15:45", "16:30", 1))
                    add(TimeDetailBean(9, "16:40", "17:25", 1))
                    add(TimeDetailBean(10, "17:35", "18:20", 1))
                    add(TimeDetailBean(11, "19:00", "19:45", 1))
                    add(TimeDetailBean(12, "19:55", "20:40", 1))
                    add(TimeDetailBean(13, "20:50", "21:35", 1))
                    for (i in 14..30) {
                        add(TimeDetailBean(i, "00:00", "00:00", 1))
                    }
                }
                timeDao.insertTimeList(newTimeList)
                context.getPrefer().edit {
                    putBoolean("time_slots_v118", true)
                }
            } catch (_: Exception) {}
        }

        // v1.0.1_beta: Reset default time table to 15-node schedule
        if (!context.getPrefer().getBoolean("time_slots_v101b", false)) {
            try {
                val dataBase = AppDatabase.getDatabase(context)
                val timeDao = dataBase.timeDetailDao()
                val tableDao = dataBase.tableDao()
                timeDao.deleteByTimeTable(1)
                val newTimeList = ArrayList<TimeDetailBean>().apply {
                    add(TimeDetailBean(1, "08:30", "09:15", 1))
                    add(TimeDetailBean(2, "09:20", "10:05", 1))
                    add(TimeDetailBean(3, "10:20", "11:05", 1))
                    add(TimeDetailBean(4, "11:05", "11:55", 1))
                    add(TimeDetailBean(5, "12:00", "13:00", 1))
                    add(TimeDetailBean(6, "13:00", "13:55", 1))
                    add(TimeDetailBean(7, "14:00", "14:45", 1))
                    add(TimeDetailBean(8, "14:50", "15:35", 1))
                    add(TimeDetailBean(9, "15:50", "16:35", 1))
                    add(TimeDetailBean(10, "16:40", "17:25", 1))
                    add(TimeDetailBean(11, "17:25", "18:20", 1))
                    add(TimeDetailBean(12, "18:20", "19:05", 1))
                    add(TimeDetailBean(13, "19:10", "19:55", 1))
                    add(TimeDetailBean(14, "20:00", "20:45", 1))
                    add(TimeDetailBean(15, "00:00", "00:00", 1))
                }
                timeDao.insertTimeList(newTimeList)
                // Cap default table to new defaults (15 nodes, 20 weeks)
                val table = tableDao.getDefaultTable()
                var changed = false
                if (table.nodes > 15) { table.nodes = 15; changed = true }
                if (table.maxWeek > 20) { table.maxWeek = 20; changed = true }
                if (changed) tableDao.updateTable(table)
                context.getPrefer().edit {
                    putBoolean("time_slots_v101b", true)
                }
            } catch (_: Exception) {}
        }
    }
}