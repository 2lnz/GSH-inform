package com.Azhe.ghs.gshschedule.schedule_import

import android.app.Activity.RESULT_OK
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.base_view.BaseFragment
import com.Azhe.ghs.gshschedule.databinding.FragmentLoginWebBinding
import com.Azhe.ghs.gshschedule.schedule_import.exception.CheckCodeErrorException
import com.Azhe.ghs.gshschedule.schedule_import.exception.PasswordErrorException
import com.Azhe.ghs.gshschedule.schedule_import.exception.UserNameErrorException
import com.Azhe.ghs.gshschedule.schedule_import.login_school.hust.MobileHub
import com.Azhe.ghs.gshschedule.schedule_import.login_school.jlu.UIMS
import com.Azhe.ghs.gshschedule.schedule_import.login_school.suda.SudaXK
import com.Azhe.ghs.gshschedule.utils.Utils
import es.dmoral.toasty.Toasty
import jahirfiquitiva.libs.textdrawable.TextDrawable
import kotlinx.coroutines.delay
import splitties.dimensions.dip
import java.io.IOException
import java.util.*

class LoginWebFragment : BaseFragment() {

    private var _binding: FragmentLoginWebBinding? = null
    private val binding get() = _binding!!

    val fabLogin get() = binding.fabLogin

    private var year = ""
    private var term = ""
    private var shanghaiPort = 0

    private val viewModel by activityViewModels<ImportViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginWebBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = viewModel.school
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.inputId.setAutofillHints(View.AUTOFILL_HINT_USERNAME)
            binding.inputPwd.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
        }
        if (viewModel.school != "苏州大学") {
            binding.inputCode.visibility = View.INVISIBLE
            binding.rlCode.visibility = View.INVISIBLE
            binding.tvTip.visibility = View.GONE
        } else {
            viewModel.sudaXK = SudaXK()
            refreshCode()
            binding.tvTip.setOnClickListener {
                Utils.openUrl(context!!, "https://yzune.github.io/2018/08/13/%E4%BD%BF%E7%94%A8FortiClient%E8%BF%9E%E6%8E%A5%E6%A0%A1%E5%9B%AD%E7%BD%91/")
            }
        }
        if (viewModel.school == "上海大学") {
            binding.btgPorts.visibility = View.VISIBLE
            binding.tvThanks.text = "感谢 @Deep Sea\n能导入贵校课程离不开他无私贡献代码"
            binding.btgPorts.addOnButtonCheckedListener { group, checkedId, isChecked ->
                if (isChecked) {
                    shanghaiPort = checkedId - R.id.btn_port1
                }
                if (!isChecked && shanghaiPort == checkedId - R.id.btn_port1) {
                    group.findViewById<MaterialButton>(checkedId).isChecked = true
                }
            }
        }
        if (viewModel.school == "清华大学") {
            binding.inputId.hint = "用户名"
            binding.tvThanks.text = "感谢 @RikaSugisawa\n能导入贵校课程离不开他无私贡献代码"
            binding.etId.inputType = InputType.TYPE_CLASS_TEXT
        }
        if (viewModel.school == "吉林大学") {
            binding.tvThanks.text = "感谢 @颩欥殘膤\n能导入贵校课程离不开他无私贡献代码"
        }
        if (viewModel.school == "华中科技大学") {
            binding.etId.inputType = InputType.TYPE_CLASS_TEXT
            binding.tvThanks.text = "感谢 @Lyt99\n能导入贵校课程离不开他无私贡献代码"
        }
        if (viewModel.school == "西北工业大学") {
            binding.etId.inputType = InputType.TYPE_CLASS_TEXT
            binding.tvThanks.text = "感谢 @ludoux\n能导入贵校课程离不开他无私贡献代码"
        }
        initEvent()
    }

    private fun TextInputLayout.showError(str: String, dur: Long = 3000) {
        launch {
            this@showError.error = str
            delay(dur)
            this@showError.error = null
        }
    }

    private fun initEvent() {

        val textDrawable = TextDrawable
                .builder()
                .textColor(Color.WHITE)
                .fontSize(context!!.dip(24))
                .useFont(ResourcesCompat.getFont(context!!, R.font.iconfont)!!)
                .buildRect("", Color.TRANSPARENT)

        binding.fabLogin.setImageDrawable(textDrawable)

        binding.ivCode.setOnClickListener {
            refreshCode()
        }

        binding.ivError.setOnClickListener {
            refreshCode()
        }

//        sheet.setOnClickListener {
//            fab_login.isExpanded = false
//        }

        binding.btnToSchedule.setOnClickListener {
            when (viewModel.school) {
                "苏州大学" -> getSudaSchedule()
                "西北工业大学" -> getNWPUSchedule()
            }
        }

        binding.btnCancel.setOnClickListener {
            refreshCode()
            binding.fabLogin.isExpanded = false
        }

        binding.fabLogin.setOnClickListener {
            when {
                binding.etId.text!!.isEmpty() -> binding.inputId.showError("学号不能为空")
                binding.etPwd.text!!.isEmpty() -> binding.inputPwd.showError("密码不能为空")
                binding.etCode.text!!.isEmpty() && viewModel.school == "苏州大学" -> binding.inputCode.showError("验证码不能为空")
                else -> launch { login() }
            }
        }
    }

    private suspend fun login() {
        var exception: Exception? = null
        var result = 0
        when (viewModel.school) {
            "苏州大学" -> {
                binding.pbLoading.visibility = View.VISIBLE
                binding.llDialog.visibility = View.INVISIBLE
                binding.fabLogin.isExpanded = true
                viewModel.sudaXK?.id = binding.etId.text.toString()
                viewModel.sudaXK?.password = binding.etPwd.text.toString()
                viewModel.sudaXK?.code = binding.etCode.text.toString()
                try {
                    viewModel.sudaXK?.login()
                    binding.pbLoading.visibility = View.GONE
                    cardC2Dialog(viewModel.sudaXK?.years!!)
                } catch (e: IOException) {
                    Toasty.error(activity!!, "请检查是否连接校园网", Toast.LENGTH_LONG).show()
                    delay(500)
                    binding.fabLogin.isExpanded = false
                } catch (e: Exception) {
                    when (e) {
                        is UserNameErrorException -> {
                            binding.etId.requestFocus()
                            binding.inputId.showError(e.message ?: "", 5000)
                            refreshCode()
                        }
                        is PasswordErrorException -> {
                            binding.etPwd.requestFocus()
                            binding.inputPwd.showError(e.message ?: "", 5000)
                            refreshCode()
                        }
                        is CheckCodeErrorException -> {
                            binding.inputCode.showError(e.message ?: "", 5000)
                            refreshCode()
                        }
                        else -> Toasty.error(activity!!, e.message
                                ?: "再试一次看看哦", Toast.LENGTH_LONG).show()
                    }
                    delay(500)
                    binding.fabLogin.isExpanded = false
                }
            }
            "清华大学" -> {
                try {
                    result = viewModel.loginTsinghua(binding.etId.text.toString(),
                            binding.etPwd.text.toString())
                } catch (e: Exception) {
                    exception = e
                }
            }
            "上海大学" -> {
                try {
                    result = viewModel.loginShanghai(binding.etId.text.toString(),
                            binding.etPwd.text.toString(), shanghaiPort)
                } catch (e: Exception) {
                    exception = e
                }
            }
            "吉林大学" -> {
                val uims = UIMS(binding.etId.text.toString(), binding.etPwd.text.toString())
                try {
                    uims.connectToUIMS()
                    uims.login()
                    uims.getCurrentUserInfo()
                    uims.getCourseSchedule()
                    result = viewModel.convertJLU(uims.courseJSON)
                } catch (e: Exception) {
                    exception = e
                }
            }
            "华中科技大学" -> {
                val hub = MobileHub(binding.etId.text.toString(), binding.etPwd.text.toString())
                try {
                    hub.login()
                    hub.getCourseSchedule()
                    result = viewModel.convertHUST(hub.courseHTML)
                } catch (e: Exception) {
                    exception = e
                }
            }
            "西北工业大学" -> {
                Toasty.info(activity!!.applicationContext, "年份为学年的起始年，学期[秋、春、夏]分别对应[1、2、3]\n例如[2019-2020春] 选择[2019 2]", Toast.LENGTH_LONG).show()
                binding.pbLoading.visibility = View.INVISIBLE
                binding.fabLogin.isExpanded = true
                val year = Calendar.getInstance().get(Calendar.YEAR)
                val list = mutableListOf<String>()
                for (index in year - 7..year) {
                    list.add(index.toString())
                }
                cardC2Dialog(list, true)
            }
        }
        if (viewModel.school == "苏州大学" || viewModel.school == "西北工业大学") return
        when (exception) {
            null -> {
                showSuccess(result)
            }
            is UserNameErrorException -> {
                binding.etId.requestFocus()
                binding.inputId.showError(exception.message ?: "", 5000)
            }
            is PasswordErrorException -> {
                binding.etPwd.requestFocus()
                binding.inputPwd.showError(exception.message ?: "", 5000)
            }
            else -> Toasty.error(activity!!, exception.message
                    ?: "再试一次看看哦", Toast.LENGTH_LONG).show()
        }
    }

    private fun getNWPUSchedule() {
        launch {
            try {
                if (term.isEmpty()) {
                    term = "1"
                }
                val result = viewModel.loginNWPU(binding.etId.text.toString(), binding.etPwd.text.toString(), year, term)
                showSuccess(result)
            } catch (e: Exception) {
                binding.fabLogin.isExpanded = false
                when (e) {
                    is UserNameErrorException -> {
                        binding.etId.requestFocus()
                        binding.inputId.showError(e.message ?: "", 5000)
                        refreshCode()
                    }
                    is PasswordErrorException -> {
                        binding.etPwd.requestFocus()
                        binding.inputPwd.showError(e.message ?: "", 5000)
                        refreshCode()
                    }
                    is CheckCodeErrorException -> {
                        binding.inputCode.showError(e.message ?: "", 5000)
                        refreshCode()
                    }
                    else -> Toasty.error(activity!!, e.message
                            ?: "再试一次看看哦", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getSudaSchedule() {
        viewModel.importType = Common.TYPE_ZF
        launch {
            try {
                val result = viewModel.importSchedule(viewModel.sudaXK?.toSchedule(year, term)!!)
                showSuccess(result)
            } catch (e: Exception) {
                Toasty.error(activity!!,
                        "导入失败>_<\n${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun refreshCode() {
        launch {
            binding.etCode.setText("")
            binding.progressBar.visibility = View.VISIBLE
            binding.ivCode.visibility = View.INVISIBLE
            binding.ivError.visibility = View.INVISIBLE
            try {
                val bitmap = viewModel.sudaXK?.getCheckCode()
                binding.progressBar.visibility = View.GONE
                binding.ivCode.visibility = View.VISIBLE
                binding.ivError.visibility = View.INVISIBLE
                binding.ivCode.setImageBitmap(bitmap)
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.ivCode.visibility = View.INVISIBLE
                binding.ivError.visibility = View.VISIBLE
                Toasty.error(context!!, "请检查是否连接校园网", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showSuccess(result: Int) {
        Toasty.success(activity!!,
                "成功导入 $result 门课程(ﾟ▽ﾟ)/\n请在右侧栏切换后查看", Toast.LENGTH_LONG).show()
        activity!!.setResult(RESULT_OK)
        activity!!.finish()
    }

    private fun cardC2Dialog(years: List<String>, selectLastYear: Boolean = false) {
        binding.llDialog.visibility = View.VISIBLE
        val terms = arrayOf("1", "2", "3")
        binding.wpTerm.displayedValues = terms
        binding.wpTerm.value = 0
        binding.wpTerm.minValue = 0
        binding.wpTerm.maxValue = terms.size - 1

        binding.wpYears.displayedValues = years.toTypedArray()
        binding.wpYears.minValue = 0
        binding.wpYears.maxValue = years.size - 1
        if (!selectLastYear) {
            binding.wpYears.value = 0
        } else {
            binding.wpYears.value = binding.wpYears.maxValue
        }

        binding.wpYears.setOnValueChangedListener { _, _, newVal ->
            year = years[newVal]
            Log.d("选中", "选中学年$year")
        }
        binding.wpTerm.setOnValueChangedListener { _, _, newVal ->
            term = terms[newVal]
            Log.d("选中", "选中学期$term")
        }
    }

    override fun onDestroyView() {
        binding.btgPorts.clearOnButtonCheckedListeners()
        super.onDestroyView()
        _binding = null
    }

}
