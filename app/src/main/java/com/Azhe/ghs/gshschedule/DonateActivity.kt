package com.Azhe.ghs.gshschedule

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.Azhe.ghs.gshschedule.base_view.BaseBlurTitleActivity
import splitties.dimensions.dip

class DonateActivity : BaseBlurTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_donate

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayLocalList()
        findViewById<View>(R.id.tv_donate).setOnClickListener {
            showThanksDialog()
        }
    }

    private fun showThanksDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_donate_thanks, null)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()
        view.findViewById<View>(R.id.btn_ok).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun displayLocalList() {
        val donors = listOf(
            "感谢所有支持者!",
            "开源精神永存",
            "用心做好每一个功能"
        )
        for (item in donors) {
            val textView = AppCompatTextView(this)
            val textParams = LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textParams.setMargins(0, 0, 0, dip(8))
            textView.layoutParams = textParams
            textView.text = item
            textView.textSize = 14f
            textView.gravity = Gravity.CENTER
        }
    }
}
