package com.Azhe.ghs.gshschedule.intro

import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.Azhe.ghs.gshschedule.R
import com.Azhe.ghs.gshschedule.base_view.BaseBlurTitleActivity
import com.Azhe.ghs.gshschedule.databinding.ActivityIntroYoungBinding
import com.Azhe.ghs.gshschedule.utils.Utils

class IntroYoungActivity : BaseBlurTitleActivity() {
    override val layoutId: Int
        get() = R.layout.activity_intro_young

    private lateinit var binding: ActivityIntroYoungBinding

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroYoungBinding.inflate(layoutInflater, llContent, true)
        Glide.with(this)
                .load("https://ws1.sinaimg.cn/large/006tNbRwgy1fxto1a67fej305c05cwen.jpg")
                .error(R.drawable.net_work_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivLogo)

        binding.tvDownload.setOnClickListener {
            Utils.openUrl(this, "https://www.coolapk.com/apk/com.suda.yzune.youngcommemoration")
        }
    }
}
