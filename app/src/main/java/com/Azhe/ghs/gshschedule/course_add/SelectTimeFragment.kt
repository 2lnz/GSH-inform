package com.Azhe.ghs.gshschedule.course_add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.activityViewModels
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.bean.CourseEditBean
import com.Azhe.ghs.gshschedule.bean.TimeBean
import com.Azhe.ghs.gshschedule.databinding.FragmentSelectTimeBinding

class SelectTimeFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_select_time

    private var _binding: FragmentSelectTimeBinding? = null
    private val binding get() = _binding!!

    var position = -1
    private val dayList = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    private val nodeList = arrayOfNulls<String>(30)
    private val viewModel by activityViewModels<AddCourseViewModel>()
    private lateinit var course: CourseEditBean
    var day = 1
    var start = 1
    var end = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentSelectTimeBinding.inflate(layoutInflater)
        val cardView = root!!.findViewById<com.google.android.material.card.MaterialCardView>(R.id.base_card_view)
        cardView.removeAllViews()
        cardView.addView(binding.root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNodeList()
        binding.wpDay.displayedValues = dayList
        binding.wpStart.displayedValues = nodeList
        binding.wpEnd.displayedValues = nodeList
        course = viewModel.editList[position]
        day = course.time.value!!.day
        start = if (course.time.value!!.startNode > viewModel.nodes) viewModel.nodes else course.time.value!!.startNode
        end = if (course.time.value!!.endNode > viewModel.nodes) viewModel.nodes else course.time.value!!.endNode
        if (start < 1) start = 1
        if (end < 1) end = 1
        initEvent()
    }

    private fun initNodeList() {
        for (i in 1..30) {
            nodeList[i - 1] = "第 $i 节"
        }
    }

    private fun initEvent() {
        binding.wpDay.minValue = 0
        binding.wpDay.maxValue = dayList.size - 1
        if (day < 1) day = 1
        if (day > 7) day = 7
        binding.wpDay.value = day - 1

        binding.wpStart.minValue = 0
        binding.wpStart.maxValue = viewModel.nodes - 1
        if (start < 1) start = 1
        binding.wpStart.value = start - 1

        binding.wpEnd.minValue = 0
        binding.wpEnd.maxValue = viewModel.nodes - 1
        if (start < 1) start = 1
        binding.wpEnd.value = end - 1

        binding.wpDay.setOnValueChangedListener { _, _, newVal ->
            day = newVal + 1
        }

        binding.wpStart.setOnValueChangedListener { _, _, newVal ->
            start = newVal + 1
            if (end < start) {
                binding.wpEnd.smoothScrollToValue(start - 1, false)
                end = start
            }
        }

        binding.wpEnd.setOnValueChangedListener { _, _, newVal ->
            end = newVal + 1
            if (end < start) {
                binding.wpEnd.smoothScrollToValue(start - 1, false)
                end = start
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val result = TimeBean(day, start, end)
            viewModel.editList[position].time.value = result
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: Int) =
                SelectTimeFragment().apply {
                    arguments = Bundle().apply {
                        putInt("position", arg)
                    }
                }
    }
}
