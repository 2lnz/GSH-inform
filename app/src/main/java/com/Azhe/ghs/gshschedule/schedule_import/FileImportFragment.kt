package com.Azhe.ghs.gshschedule.schedule_import

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.base_view.BaseFragment
import com.Azhe.ghs.gshschedule.databinding.FragmentFileImportBinding
import com.Azhe.ghs.gshschedule.utils.Const
import com.Azhe.ghs.gshschedule.utils.ViewUtils

class FileImportFragment : BaseFragment() {

    private var _binding: FragmentFileImportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFileImportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(context!!.applicationContext, view.findViewById(R.id.v_status))

        binding.tvSelf.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            try {
                activity?.startActivityForResult(intent, Const.REQUEST_CODE_IMPORT_FILE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.ibBack.setOnClickListener {
            activity!!.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
