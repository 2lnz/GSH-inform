package com.Azhe.ghs.gshschedule.intro

import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import com.Azhe.ghs.gshschedule.DonateActivity
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.base_view.BaseBlurTitleActivity
import com.Azhe.ghs.gshschedule.utils.UpdateUtils
import splitties.activities.start
import splitties.resources.color

class AboutActivity : BaseBlurTitleActivity() {
    override val layoutId: Int
        get() = R.layout.activity_about

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        tvButton.text = "捐赠"
        tvButton.setTextColor(color(R.color.colorAccent))
        tvButton.setOnClickListener {
            start<DonateActivity>()
        }
        return tvButton
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val tvVersion = findViewById<AppCompatTextView>(R.id.tv_version)
            tvVersion.text = "版本号：${UpdateUtils.getVersionName(this)}"
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
