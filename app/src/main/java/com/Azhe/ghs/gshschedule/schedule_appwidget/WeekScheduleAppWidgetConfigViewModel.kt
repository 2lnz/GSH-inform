package com.Azhe.ghs.gshschedule.schedule_appwidget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.Azhe.ghs.gshschedule.AppDatabase
import com.Azhe.ghs.gshschedule.bean.AppWidgetBean
import com.Azhe.ghs.gshschedule.bean.TableBean
import com.Azhe.ghs.gshschedule.bean.TableSelectBean

class WeekScheduleAppWidgetConfigViewModel(application: Application) : AndroidViewModel(application) {
    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()
    private val widgetDao = dataBase.appWidgetDao()

    suspend fun getDefaultTable(): TableBean? {
        return try {
            tableDao.getDefaultTable()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getTableById(id: Int): TableBean? {
        return tableDao.getTableById(id)
    }

    suspend fun insertWeekAppWidgetData(appWidget: AppWidgetBean) {
        widgetDao.insertAppWidget(appWidget)
    }

    suspend fun getTableList(): List<TableSelectBean> {
        return tableDao.getTableSelectList()
    }
}