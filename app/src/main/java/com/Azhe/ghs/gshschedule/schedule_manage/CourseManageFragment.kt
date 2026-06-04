package com.Azhe.ghs.gshschedule.schedule_manage

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.base_view.BaseFragment
import com.Azhe.ghs.gshschedule.bean.CourseBaseBean
import com.Azhe.ghs.gshschedule.bean.TableSelectBean
import com.Azhe.ghs.gshschedule.course_add.AddCourseActivity
import com.Azhe.ghs.gshschedule.databinding.FragmentListManageBinding
import com.Azhe.ghs.gshschedule.utils.Const
import es.dmoral.toasty.Toasty
import splitties.activities.start
import splitties.dimensions.dip

class CourseManageFragment : BaseFragment() {

    private var _binding: FragmentListManageBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ScheduleManageViewModel>()
    private val table by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getParcelable<TableSelectBean>("selectedTable")
    }
    private lateinit var adapter: CourseListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentListManageBinding.inflate(inflater, container, false)
        if (table == null) {
            return binding.root
        }
        val space = context!!.dip(8)
        binding.rvList.setPadding(space, 0, space, 0)
        launch {
            val courseList = viewModel.getCourseBaseBeanListByTable(table!!.id)
            initRecyclerView(binding.rvList, courseList)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAdd.setOnClickListener {
            val intent = Intent(activity, AddCourseActivity::class.java).apply {
                putExtra("id", -1)
                putExtra("tableId", table!!.id)
                putExtra("maxWeek", table!!.maxWeek)
                putExtra("nodes", table!!.nodes)
            }
            startActivityForResult(intent, Const.REQUEST_CODE_ADD_COURSE)
        }
        (activity as ScheduleManageActivity).subButton?.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("提示")
                    .setMessage("真的要清空课表吗？这将无法恢复。")
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.sure) { _, _ ->
                        launch {
                            try {
                                viewModel.clearTable(table!!.id)
                                adapter.data.clear()
                                adapter.notifyDataSetChanged()
                                Toasty.success(activity!!, "操作成功~").show()
                            } catch (e: Exception) {
                                Toasty.error(activity!!, "操作失败>_<${e.message}").show()
                            }
                        }
                    }
                    .show()
        }
    }

    private fun initRecyclerView(rvCourseList: RecyclerView, data: MutableList<CourseBaseBean>) {
        rvCourseList.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        adapter = CourseListAdapter(R.layout.item_course_list, data)
        rvCourseList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val space = context!!.dip(8)
                outRect.set(space, space, space, space)
            }
        })
        adapter.setOnItemClickListener { _, _, position ->
            activity!!.start<AddCourseActivity> {
                putExtra("id", data[position].id)
                putExtra("tableId", data[position].tableId)
                putExtra("maxWeek", table!!.maxWeek)
                putExtra("nodes", table!!.nodes)
            }
        }
        adapter.setOnItemLongClickListener { _, _, position ->
            MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("提示")
                    .setMessage("确定要删除该课程吗？它的时间段都将会被删除。")
                    .setPositiveButton(R.string.sure) { _, _ ->
                        launch {
                            viewModel.deleteCourse(data[position])
                            adapter.remove(position)
                            Toasty.success(context!!, "删除成功~").show()
                            val list = viewModel.getScheduleWidgetIds()
                            val appWidgetManager = AppWidgetManager.getInstance(activity!!.applicationContext)
                            list.forEach {
                                when (it.detailType) {
                                    0 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_schedule)
                                    1 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_course)
                                }
                            }
                        }
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            return@setOnItemLongClickListener true
        }
        adapter.addHeaderView(AppCompatTextView(context!!).apply {
            text = "轻触编辑，长按删除"
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setPadding(0, dip(8), 0, dip(8))
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        })
        adapter.addFooterView(View(context!!).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(240))
        })
        adapter.setEmptyView(LinearLayoutCompat(context!!).apply {
            id = R.id.anko_empty_view
            orientation = LinearLayoutCompat.VERTICAL
            setPadding(0, dip(72), 0, 0)
            addView(AppCompatImageView(context!!).apply {
                setImageResource(R.drawable.ic_schedule_empty)
            }, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, dip(200))
            addView(AppCompatTextView(context).apply {
                text = "还没有添加任何课程哦"
                gravity = Gravity.CENTER
            }, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dip(16)
            })
        })
        rvCourseList.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Const.REQUEST_CODE_ADD_COURSE) {
            data?.extras?.getParcelable<CourseBaseBean>("course")?.let {
                adapter.addData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
