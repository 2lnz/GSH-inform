package com.Azhe.ghs.gshschedule.schedule

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.activityViewModels
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.databinding.FragmentExportSettingsBinding
import com.Azhe.ghs.gshschedule.utils.Const
import es.dmoral.toasty.Toasty

class ExportSettingsFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_export_settings

    private var _binding: FragmentExportSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ScheduleViewModel>()

    val tableName by lazy(LazyThreadSafetyMode.NONE) {
        if (viewModel.table.tableName == "") {
            "我的课表"
        } else {
            viewModel.table.tableName
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentExportSettingsBinding.inflate(layoutInflater)
        val cardView = root!!.findViewById<com.google.android.material.card.MaterialCardView>(R.id.base_card_view)
        cardView.removeAllViews()
        cardView.addView(binding.root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        binding.tvExport.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_TITLE, "$tableName.wakeup_schedule")
            }
            Toasty.info(activity!!, "请自行选择导出的地方\n不要修改文件的扩展名哦", Toasty.LENGTH_LONG).show()
            activity?.startActivityForResult(intent, Const.REQUEST_CODE_EXPORT)
            dismiss()
        }

        binding.tvExportIcs.setOnLongClickListener {
            Toasty.info(activity!!, "ICS 文件可导入到系统日历应用中", Toasty.LENGTH_LONG).show()
            return@setOnLongClickListener true
        }

        binding.tvExportIcs.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/calendar"
                putExtra(Intent.EXTRA_TITLE, "日历-$tableName")
            }
            Toasty.info(activity!!, "请自行选择导出的地方\n不要修改文件的扩展名哦", Toasty.LENGTH_LONG).show()
            activity?.startActivityForResult(intent, Const.REQUEST_CODE_EXPORT_ICS)
            dismiss()
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
