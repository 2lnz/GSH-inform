package com.Azhe.ghs.gshschedule.course_add

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.databinding.FragmentSelectWeekBinding
import com.Azhe.ghs.gshschedule.widget.SelectedRecyclerView
import es.dmoral.toasty.Toasty
import splitties.resources.styledColor

class SelectWeekFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_select_week

    private var _binding: FragmentSelectWeekBinding? = null
    private val binding get() = _binding!!

    var position = -1
    private val viewModel by activityViewModels<AddCourseViewModel>()
    private val liveData = MutableLiveData<ArrayList<Int>>()
    private val result = ArrayList<Int>()
    private var colorSurface: Int = Color.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
        colorSurface = context!!.styledColor(R.attr.colorOnSurface)
        liveData.observe(this, Observer {
            if (it?.size == viewModel.maxWeek) {
                binding.tvAll.setTextColor(Color.WHITE)
                binding.tvAll.background = ContextCompat.getDrawable(context!!, R.drawable.select_textview_bg)
            }
            if (it?.size != viewModel.maxWeek) {
                binding.tvAll.setTextColor(colorSurface)
                binding.tvAll.background = null
            }
            val flag = viewModel.judgeType(it!!)
            if (flag == 1) {
                binding.tvType1.setTextColor(Color.WHITE)
                binding.tvType1.background = ContextCompat.getDrawable(context!!, R.drawable.select_textview_bg)
            }
            if (flag != 1) {
                binding.tvType1.setTextColor(colorSurface)
                binding.tvType1.background = null
            }
            if (flag == 2) {
                binding.tvType2.setTextColor(Color.WHITE)
                binding.tvType2.background = ContextCompat.getDrawable(context!!, R.drawable.select_textview_bg)
            }
            if (flag != 2) {
                binding.tvType2.setTextColor(colorSurface)
                binding.tvType2.background = null
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentSelectWeekBinding.inflate(layoutInflater)
        val cardView = root!!.findViewById<com.google.android.material.card.MaterialCardView>(R.id.base_card_view)
        cardView.removeAllViews()
        cardView.addView(binding.root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        liveData.value = viewModel.editList[position].weekList.value
        result.addAll(liveData.value!!)
        showWeeks()
        initEvent()
    }

    private fun showWeeks() {
        val adapter = SelectWeekAdapter(R.layout.item_select_week, viewModel.maxWeek, result)
        binding.rvWeek.adapter = adapter
        binding.rvWeek.layoutManager = StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL)
        var prePos = -1
        binding.rvWeek.positionChangedListener = object : SelectedRecyclerView.PositionChangedListener {
            override fun changeState(pos: Int, isDown: Boolean) {
                if (prePos != pos || isDown) {
                    if (pos in 0 until viewModel.maxWeek) {
                        if (!result.contains(pos + 1)) {
                            result.add(pos + 1)
                            adapter.getViewByPosition(pos, R.id.tv_num)
                                    ?.setBackgroundResource(R.drawable.week_selected_bg)
                            (adapter.getViewByPosition(pos, R.id.tv_num) as AppCompatTextView)
                                    .setTextColor(Color.WHITE)
                        } else {
                            result.remove(pos + 1)
                            adapter.getViewByPosition(pos, R.id.tv_num)?.background = null
                            (adapter.getViewByPosition(pos, R.id.tv_num) as AppCompatTextView)
                                    .setTextColor(colorSurface)
                        }
                        liveData.value = result
                    }
                    if (prePos != pos) {
                        prePos = pos
                    }
                }
            }
        }
    }

    private fun initEvent() {
        binding.tvAll.setOnClickListener {
            if (binding.tvAll.background == null) {
                result.clear()
                for (i in 1..viewModel.maxWeek) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            } else {
                result.clear()
                showWeeks()
                liveData.value = result
            }
        }

        binding.tvType1.setOnClickListener {
            if (binding.tvType1.background == null) {
                result.clear()
                for (i in 1..viewModel.maxWeek step 2) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            }
        }

        binding.tvType2.setOnClickListener {
            if (binding.tvType2.background == null) {
                result.clear()
                for (i in 2..viewModel.maxWeek step 2) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            if (result.size == 0) {
                Toasty.error(context!!.applicationContext, "请至少选择一周").show()
            } else {
                viewModel.editList[position].weekList.value = result
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(arg: Int) =
                SelectWeekFragment().apply {
                    arguments = Bundle().apply {
                        putInt("position", arg)
                    }
                }
    }
}
