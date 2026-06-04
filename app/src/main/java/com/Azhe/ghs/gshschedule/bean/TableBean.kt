package com.Azhe.ghs.gshschedule.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(foreignKeys = [(
        ForeignKey(entity = TimeTableBean::class,
                parentColumns = ["id"],
                childColumns = ["timeTable"],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.SET_DEFAULT
        ))],
        indices = [Index(value = ["timeTable"], unique = false)])
data class TableBean(
        @PrimaryKey(autoGenerate = true)
        var id: Int,
        var tableName: String,
        var nodes: Int = 15,
        var background: String = "",
        var timeTable: Int = 1,
        var startDate: String = run {
            val cal = java.util.Calendar.getInstance()
            val month = cal.get(java.util.Calendar.MONTH) + 1
            val year = cal.get(java.util.Calendar.YEAR)
            if (month >= 9) "$year-09-01"
            else if (month >= 3) "$year-03-01"
            else "${year - 1}-09-01"
        },
        var maxWeek: Int = 20,
        var itemHeight: Int = 56,
        var itemAlpha: Int = 60,
        var itemTextSize: Int = 12,
        var widgetItemHeight: Int = 56,
        var widgetItemAlpha: Int = 60,
        var widgetItemTextSize: Int = 12,
        var strokeColor: Int = 0x80ffffff.toInt(),
        var widgetStrokeColor: Int = 0x80ffffff.toInt(),
        var textColor: Int = 0xff000000.toInt(),
        var widgetTextColor: Int = 0xff000000.toInt(),
        var courseTextColor: Int = 0xffffffff.toInt(),
        var widgetCourseTextColor: Int = 0xffffffff.toInt(),
        var showSat: Boolean = true,
        var showSun: Boolean = true,
        var sundayFirst: Boolean = false,
        var showOtherWeekCourse: Boolean = true,
        var showTime: Boolean = false,
        var type: Int = 0
) : Parcelable