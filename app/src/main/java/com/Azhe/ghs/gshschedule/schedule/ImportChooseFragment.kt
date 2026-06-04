package com.Azhe.ghs.gshschedule.schedule

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.schedule_import.LoginWebActivity
import com.Azhe.ghs.gshschedule.schedule_import.SchoolListActivity
import com.Azhe.ghs.gshschedule.utils.Const

class ImportChooseFragment : DialogFragment() {

    private val viewModel by activityViewModels<ScheduleViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_import_choose, null)

        view.findViewById<TextView>(R.id.tv_school)?.setOnClickListener {
            val intent = Intent(activity, SchoolListActivity::class.java)
            dismiss()
            activity?.startActivityForResult(intent, Const.REQUEST_CODE_IMPORT)
        }

        view.findViewById<TextView>(R.id.tv_excel)?.setOnClickListener {
            showSAFTips {
                val intent = Intent(activity, LoginWebActivity::class.java).apply {
                    putExtra("import_type", "excel")
                    putExtra("tableId", viewModel.table.id)
                }
                dismiss()
                activity?.startActivityForResult(intent, Const.REQUEST_CODE_IMPORT)
            }
        }

        view.findViewById<TextView>(R.id.tv_html)?.setOnClickListener {
            showSAFTips {
                val intent = Intent(activity, LoginWebActivity::class.java).apply {
                    putExtra("import_type", "html")
                    putExtra("tableId", viewModel.table.id)
                }
                dismiss()
                activity?.startActivityForResult(intent, Const.REQUEST_CODE_IMPORT)
            }
        }

        view.findViewById<TextView>(R.id.tv_file)?.setOnClickListener {
            showSAFTips {
                val intent = Intent(activity, LoginWebActivity::class.java).apply {
                    putExtra("import_type", "file")
                }
                dismiss()
                activity?.startActivityForResult(intent, Const.REQUEST_CODE_IMPORT)
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .create()
    }

    private fun showSAFTips(block: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("提示")
            .setMessage("本应用采用系统级文件选择器。如找不到路径，请点选择器右上角三个点，选择「显示内部存储设备」。")
            .setPositiveButton(R.string.sure) { _, _ -> block() }
            .setNegativeButton(R.string.cancel, null)
            .setCancelable(false)
            .show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ImportChooseFragment()
    }
}
