package com.Azhe.ghs.gshschedule.schedule_import

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.base_view.BaseFragment
import com.Azhe.ghs.gshschedule.databinding.FragmentSchoolInfoBinding
import com.Azhe.ghs.gshschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty

class SchoolInfoFragment : BaseFragment() {

    private var _binding: FragmentSchoolInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ImportViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentSchoolInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(context!!.applicationContext, view.findViewById(R.id.v_status))
        initEvent()
    }

    private fun initEvent() {
        binding.ibBack.setOnClickListener {
            activity!!.finish()
        }

        binding.chipUrp.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isUrp = isChecked
        }

        binding.tvNext.setOnClickListener {
            if (binding.etSchool.text.toString() != "") {
                viewModel.schoolInfo[0] = binding.etSchool.text.toString()
                viewModel.schoolInfo[1] = binding.etType.text.toString()
                viewModel.schoolInfo[2] = binding.etQq.text.toString()
                val fragment = WebViewLoginFragment.newInstance()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.hide(this)
                transaction.add(android.R.id.content, fragment, "webLogin")
                transaction.commit()
            } else {
                Toasty.error(activity!!, "请填写学校全称").show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
