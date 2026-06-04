package com.Azhe.ghs.gshschedule.schedule

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.activityViewModels
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.databinding.FragmentMultiCourseBinding
import splitties.dimensions.dip

class MultiCourseFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_multi_course

    private var _binding: FragmentMultiCourseBinding? = null
    private val binding get() = _binding!!

    private var week = 0
    private var day = 0
    private var startNode = 0
    private val viewModel by activityViewModels<ScheduleViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            week = it.getInt("week")
            day = it.getInt("day")
            startNode = it.getInt("startNode")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentMultiCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewpager.pageMargin = context!!.dip(32)
        binding.viewpager.offscreenPageLimit = 3
        binding.viewpager.adapter = MultiCourseAdapter(childFragmentManager, viewModel.getMultiCourse(week, day, startNode))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(weekParam: Int, dayParam: Int, startNodeParam: Int) = MultiCourseFragment().apply {
            arguments = Bundle().apply {
                putInt("week", weekParam)
                putInt("day", dayParam)
                putInt("startNode", startNodeParam)
            }
        }
    }
}
