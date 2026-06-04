package com.Azhe.ghs.gshschedule.schedule_appwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.base_view.BaseBlurTitleActivity
import com.Azhe.ghs.gshschedule.bean.AppWidgetBean
import com.Azhe.ghs.gshschedule.bean.TableSelectBean
import com.Azhe.ghs.gshschedule.databinding.ActivityWeekScheduleAppWidgetConfigBinding
import com.Azhe.ghs.gshschedule.utils.AppWidgetUtils
import es.dmoral.toasty.Toasty
import splitties.snackbar.longSnack

class WeekScheduleAppWidgetConfigActivity : BaseBlurTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_week_schedule_app_widget_config

    private lateinit var binding: ActivityWeekScheduleAppWidgetConfigBinding

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        return null
    }

    private val viewModel by viewModels<WeekScheduleAppWidgetConfigViewModel>()
    private var mAppWidgetId = 0
    private var isTodayType = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeekScheduleAppWidgetConfigBinding.inflate(layoutInflater, llContent, true)

        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        //Log.d("包名", appWidgetManager.getAppWidgetInfo(mAppWidgetId).provider.shortClassName)
        val widgetInfo = appWidgetManager.getAppWidgetInfo(mAppWidgetId)
        if (widgetInfo == null) {
            Toasty.error(applicationContext, "小部件信息读取失败，请重试").show()
            finish()
            return
        }
        val what = widgetInfo.provider.shortClassName
        isTodayType = (what == ".today_appwidget.TodayCourseAppWidget" || what == "com.Azhe.ghs.gshschedule.today_appwidget.TodayCourseAppWidget")
        if (isTodayType) {
            Glide.with(this)
                    .load("https://ws2.sinaimg.cn/large/0069RVTdgy1fv5ypjuqs1j30u01hcdlt.jpg")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivTip)
        } else {
            binding.tvGotIt.visibility = View.GONE
            binding.ivTip.visibility = View.GONE
            val list = ArrayList<TableSelectBean>()
            val adapter = WidgetTableListAdapter(R.layout.item_table_list, list)
            adapter.setOnItemClickListener { _, _, position ->
                launch {
                    viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 0, "${list[position].id}"))
                    val table = viewModel.getTableById(list[position].id)

                    if (table == null) {
                        Toasty.error(applicationContext, "该课表读取错误>_<").show()
                        finish()
                    } else {
                        AppWidgetUtils.refreshScheduleWidget(applicationContext, appWidgetManager, mAppWidgetId, table)
                        val resultValue = Intent()
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                        setResult(Activity.RESULT_OK, resultValue)
                        finish()
                    }
                }
            }
            binding.rvList.adapter = adapter
            binding.rvList.layoutManager = LinearLayoutManager(this)
            launch {
                list.clear()
                list.addAll(viewModel.getTableList())
                adapter.notifyDataSetChanged()
            }
        }


        binding.tvGotIt.setOnClickListener {
            launch {
                // Log.d("包名", appWidgetManager.getAppWidgetInfo(mAppWidgetId).provider.shortClassName)
                viewModel.insertWeekAppWidgetData(AppWidgetBean(mAppWidgetId, 0, 1, ""))
                val table = viewModel.getDefaultTable()
                if (table == null) {
                    Toasty.error(applicationContext, "请先在主页面创建课表后再添加小部件").show()
                    finish()
                    return@launch
                }
                AppWidgetUtils.refreshTodayWidget(applicationContext, appWidgetManager, mAppWidgetId, table)
                val resultValue = Intent()
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                setResult(Activity.RESULT_OK, resultValue)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        binding.root.longSnack(
                if (isTodayType) {
                    "请阅读文字后点击「我知道啦」"
                } else {
                    "请从列表中选择需要放置的课表"
                })
    }
}
