package com.Azhe.ghs.gshschedule.schedule

import android.appwidget.AppWidgetManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.card.MaterialCardView
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.bean.CourseBean
import com.Azhe.ghs.gshschedule.course_add.AddCourseActivity
import com.Azhe.ghs.gshschedule.databinding.FragmentCourseDetailBinding
import com.Azhe.ghs.gshschedule.databinding.ItemAddCourseDetailBinding
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.delay
import splitties.activities.start
import splitties.dimensions.dip
import splitties.snackbar.longSnack

class CourseDetailFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_course_detail

    private var _binding: FragmentCourseDetailBinding? = null
    private val binding get() = _binding!!
    private var _detailBinding: ItemAddCourseDetailBinding? = null
    private val detailBinding get() = _detailBinding!!

    private lateinit var course: CourseBean
    private var nested: Boolean = false
    private val viewModel by activityViewModels<ScheduleViewModel>()

    private var makeSure = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            course = it.getParcelable<CourseBean>("course") as CourseBean
            nested = it.getBoolean("nested")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentCourseDetailBinding.inflate(layoutInflater)
        _detailBinding = ItemAddCourseDetailBinding.bind(binding.includeDetail.root)
        if (!nested) {
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.setLayout(context!!.dip(280), ViewGroup.LayoutParams.WRAP_CONTENT)
            val root = inflater.inflate(R.layout.fragment_base_dialog, container, false)
            val cardView = root.findViewById<MaterialCardView>(R.id.base_card_view)
            cardView.addView(binding.root)
            return root
        } else {
            container!!.layoutParams.width = context!!.dip(280)
            val root = inflater.inflate(R.layout.fragment_base_dialog, container, false)
            val cardView = root.findViewById<MaterialCardView>(R.id.base_card_view)
            cardView.setBackgroundColor(Color.TRANSPARENT)
            cardView.addView(binding.root)
            binding.root.findViewById<View>(R.id.include_detail).setBackgroundColor(Color.TRANSPARENT)
            return root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        showData()
        initEvent()
    }

    private fun initView() {
        detailBinding.tvItem.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    }

    private fun showData() {
        detailBinding.tvItem.text = course.courseName
        detailBinding.etTeacher.text = course.teacher
        detailBinding.etRoom.text = course.room
        val type = when (course.type) {
            1 -> "单周"
            2 -> "双周"
            else -> ""
        }
        detailBinding.etWeeks.text = "第${course.startWeek} - ${course.endWeek}周    $type"
        try {
            detailBinding.etTime.text = "第${course.startNode} - ${course.startNode + course.step - 1}节    ${viewModel.timeList[course.startNode - 1].startTime} - ${viewModel.timeList[course.startNode + course.step - 2].endTime}"
        } catch (e: Exception) {
            detailBinding.etTime.longSnack("该课程似乎有点问题哦>_<请修改一下")
        }
    }

    override fun dismiss() {
        if (nested) {
            (parentFragment as DialogFragment).dismiss()
        } else {
            super.dismiss()
        }
    }

    private fun initEvent() {
        detailBinding.ibDelete.setOnClickListener {
            dismiss()
        }

        binding.ibEdit.setOnClickListener {
            dismiss()
            activity!!.start<AddCourseActivity> {
                putExtra("id", course.id)
                putExtra("tableId", course.tableId)
                putExtra("maxWeek", viewModel.table.maxWeek)
                putExtra("nodes", viewModel.table.nodes)
            }
        }

        binding.ibDeleteCourse.setOnClickListener {
            if (makeSure == 0) {
                binding.tvTips.visibility = View.VISIBLE
                makeSure++
                launch {
                    delay(5000)
                    binding.tvTips.visibility = View.GONE
                    makeSure = 0
                }
            } else {
                launch {
                    try {
                        viewModel.deleteCourseBean(course)
                        Toasty.success(context!!.applicationContext, "删除成功").show()
                        val appWidgetManager = AppWidgetManager.getInstance(activity!!.applicationContext)
                        val list = viewModel.getScheduleWidgetIds()
                        list.forEach {
                            when (it.detailType) {
                                0 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_schedule)
                                1 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_course)
                            }
                        }
                        dismiss()
                    } catch (e: Exception) {
                        Toasty.error(context!!.applicationContext, "出现异常>_<\n" + e.message).show()
                    }
                }
            }
        }

        binding.ibDeleteCourse.setOnLongClickListener {
            launch {
                try {
                    viewModel.deleteCourseBaseBean(course.id, course.tableId)
                    Toasty.success(context!!.applicationContext, "删除成功").show()
                    val appWidgetManager = AppWidgetManager.getInstance(activity!!.applicationContext)
                    val list = viewModel.getScheduleWidgetIds()
                    list.forEach {
                        when (it.detailType) {
                            0 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_schedule)
                            1 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_course)
                        }
                    }
                    dismiss()
                } catch (e: Exception) {
                    Toasty.error(context!!.applicationContext, "出现异常>_<\n" + e.message).show()
                }
            }
            return@setOnLongClickListener true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _detailBinding = null
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: CourseBean, arg1: Boolean = false) =
                CourseDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("course", arg)
                        putBoolean("nested", arg1)
                    }
                }
    }
}
