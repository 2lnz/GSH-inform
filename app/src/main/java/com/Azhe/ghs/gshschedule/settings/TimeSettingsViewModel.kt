package com.Azhe.ghs.gshschedule.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.Azhe.ghs.gshschedule.AppDatabase
import com.Azhe.ghs.gshschedule.bean.TableBean
import com.Azhe.ghs.gshschedule.bean.TimeDetailBean
import com.Azhe.ghs.gshschedule.bean.TimeTableBean
import com.Azhe.ghs.gshschedule.utils.CourseUtils

class TimeSettingsViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var table: TableBean

    private val dataBase = AppDatabase.getDatabase(application)
    private val timeDao = dataBase.timeDetailDao()
    private val timeTableDao = dataBase.timeTableDao()
    val timeTableList = arrayListOf<TimeTableBean>()
    val timeList = arrayListOf<TimeDetailBean>()
    val timeSelectList = arrayListOf<String>()

    var entryPosition = 0
    var selectedId = 1

    suspend fun addNewTimeTable(name: String) {
        timeTableDao.initTimeTable(TimeTableBean(id = 0, name = name))
    }

    suspend fun initTimeTableData(id: Int) {
        val timeList = listOf(
                TimeDetailBean(1, "08:00", "08:45", id),
                TimeDetailBean(2, "08:50", "09:35", id),
                TimeDetailBean(3, "09:50", "10:35", id),
                TimeDetailBean(4, "10:40", "11:25", id),
                TimeDetailBean(5, "11:35", "12:20", id),
                TimeDetailBean(6, "14:00", "14:45", id),
                TimeDetailBean(7, "14:50", "15:35", id),
                TimeDetailBean(8, "15:45", "16:30", id),
                TimeDetailBean(9, "16:40", "17:25", id),
                TimeDetailBean(10, "17:35", "18:20", id),
                TimeDetailBean(11, "19:00", "19:45", id),
                TimeDetailBean(12, "19:55", "20:40", id),
                TimeDetailBean(13, "20:50", "21:35", id),
                TimeDetailBean(14, "00:00", "00:00", id),
                TimeDetailBean(15, "00:00", "00:00", id),
                TimeDetailBean(16, "00:00", "00:00", id),
                TimeDetailBean(17, "00:00", "00:00", id),
                TimeDetailBean(18, "00:00", "00:00", id),
                TimeDetailBean(19, "00:00", "00:00", id),
                TimeDetailBean(20, "00:00", "00:00", id),
                TimeDetailBean(21, "00:00", "00:00", id),
                TimeDetailBean(22, "00:00", "00:00", id),
                TimeDetailBean(23, "00:00", "00:00", id),
                TimeDetailBean(24, "00:00", "00:00", id),
                TimeDetailBean(25, "00:00", "00:00", id),
                TimeDetailBean(26, "00:00", "00:00", id),
                TimeDetailBean(27, "00:00", "00:00", id),
                TimeDetailBean(28, "00:00", "00:00", id),
                TimeDetailBean(29, "00:00", "00:00", id),
                TimeDetailBean(30, "00:00", "00:00", id)
        )
        timeDao.insertTimeList(timeList)
    }

    suspend fun deleteTimeTable(timeTableBean: TimeTableBean) {
        timeTableDao.deleteTimeTable(timeTableBean)
    }

    fun getTimeTableList(): LiveData<List<TimeTableBean>> {
        return timeTableDao.getTimeTableList()
    }

    fun getTimeData(id: Int): LiveData<List<TimeDetailBean>> {
        return timeDao.getTimeListLiveData(id)
    }

    suspend fun saveDetailData(tablePosition: Int) {
        timeTableDao.updateTimeTable(timeTableList[tablePosition])
        timeDao.updateTimeDetailList(timeList)
    }

    fun initTimeSelectList() {
        for (i in 6..23) {
            for (j in 0..55 step 5) {
                val h = if (i < 10) "0$i" else i.toString()
                val m = if (j < 10) "0$j" else j.toString()
                timeSelectList.add("$h:$m")
            }
        }
    }

    fun refreshEndTime(min: Int) {
        timeList.forEach {
            it.endTime = CourseUtils.calAfterTime(it.startTime, min)
        }
    }
}