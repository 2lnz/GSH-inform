package com.Azhe.ghs.gshschedule.schedule_import

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.base_view.BaseFragment
import com.Azhe.ghs.gshschedule.databinding.FragmentHtmlImportBinding
import com.Azhe.ghs.gshschedule.schedule_import.Common.TYPE_QZ
import com.Azhe.ghs.gshschedule.schedule_import.Common.TYPE_ZF
import com.Azhe.ghs.gshschedule.utils.Const
import com.Azhe.ghs.gshschedule.utils.Utils
import com.Azhe.ghs.gshschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import splitties.snackbar.longSnack
import java.nio.charset.Charset

class HtmlImportFragment : BaseFragment() {

    private var _binding: FragmentHtmlImportBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ImportViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentHtmlImportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(context!!.applicationContext, view.findViewById(R.id.v_status))

        binding.tvWay.setOnClickListener {
            Utils.openUrl(activity!!, "https://www.jianshu.com/p/4cd071697fed")
        }

        binding.tvType.setOnClickListener {
            startActivityForResult(Intent(activity, SchoolListActivity::class.java).apply {
                putExtra("fromLocal", true)
            }, Const.REQUEST_CODE_CHOOSE_SCHOOL)
        }

        binding.cpUtf.isChecked = true

        binding.cpUtf.setOnClickListener {
            binding.cpUtf.isChecked = true
            binding.cpGbk.isChecked = false
        }

        binding.cpGbk.setOnClickListener {
            binding.cpGbk.isChecked = true
            binding.cpUtf.isChecked = false
        }

        var qzChipId = 0
        binding.cgQz.setOnCheckedChangeListener { chipGroup, id ->
            when (id) {
                R.id.chip_qz1 -> {
                    qzChipId = id
                    viewModel.qzType = 0
                }
                R.id.chip_qz2 -> {
                    qzChipId = id
                    viewModel.qzType = 1
                }
                R.id.chip_qz3 -> {
                    qzChipId = id
                    viewModel.qzType = 2
                }
                R.id.chip_qz4 -> {
                    qzChipId = id
                    viewModel.qzType = 3
                }
                else -> {
                    chipGroup.findViewById<Chip>(qzChipId).isChecked = true
                }
            }
        }

        var zfChipId = 0
        binding.cgZf.setOnCheckedChangeListener { chipGroup, id ->
            when (id) {
                R.id.chip_zf1 -> {
                    zfChipId = id
                    viewModel.zfType = 0
                }
                R.id.chip_zf2 -> {
                    zfChipId = id
                    viewModel.zfType = 1
                }
                else -> {
                    chipGroup.findViewById<Chip>(zfChipId).isChecked = true
                }
            }
        }

        binding.tvSelf.setOnClickListener {
            if (viewModel.importType.equals("html")) {
                getView()?.longSnack("请先点击第二个按钮选择类型哦")
            } else {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/*"
                }
                try {
                    startActivityForResult(intent, Const.REQUEST_CODE_IMPORT_HTML)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.ibBack.setOnClickListener {
            activity!!.finish()
        }

        binding.fabImport.setOnClickListener {
            if (viewModel.htmlUri == null) {
                it.longSnack("还没有选择文件呢>_<")
                return@setOnClickListener
            }
            launch {
                try {
                    val html = withContext(Dispatchers.IO) {
                        activity!!.contentResolver.openInputStream(viewModel.htmlUri!!)!!.bufferedReader(
                                if (binding.cpUtf.isChecked) Charsets.UTF_8 else Charset.forName("gbk")
                        ).readText()
                    }
                    val result = viewModel.importSchedule(html)
                    Toasty.success(activity!!,
                            "成功导入 $result 门课程(ﾟ▽ﾟ)/\n请在右侧栏切换后查看").show()
                    activity!!.setResult(RESULT_OK)
                    activity!!.finish()
                } catch (e: Exception) {
                    Toasty.error(activity!!,
                            "导入失败>_<\n${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Const.REQUEST_CODE_IMPORT_HTML && resultCode == RESULT_OK) {
            viewModel.htmlUri = data?.data
        }
        if (requestCode == Const.REQUEST_CODE_CHOOSE_SCHOOL && resultCode == RESULT_OK) {
            viewModel.importType = data!!.getStringExtra("type")
            when (viewModel.importType) {
                TYPE_ZF -> {
                    binding.chipZf1.isChecked = true
                    binding.cgQz.visibility = View.GONE
                    binding.cgZf.visibility = View.VISIBLE
                }
                TYPE_QZ -> {
                    binding.chipQz1.isChecked = true
                    binding.cgQz.visibility = View.VISIBLE
                    binding.cgZf.visibility = View.GONE
                }
                else -> {
                    binding.cgQz.visibility = View.GONE
                    binding.cgZf.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
