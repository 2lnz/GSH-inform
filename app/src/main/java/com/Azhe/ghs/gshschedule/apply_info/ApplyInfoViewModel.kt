package com.Azhe.ghs.gshschedule.apply_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.Azhe.ghs.gshschedule.bean.HtmlCountBean

class ApplyInfoViewModel : ViewModel() {

    val filterList = arrayListOf<HtmlCountBean>()
    val countList = arrayListOf<HtmlCountBean>()
    val countInfo = MutableLiveData<String>()

    fun initData() {
        // 服务器已移除，显示空列表
        countInfo.value = "OK"
    }

    fun search(str: String?) {
        filterList.clear()
        if (str.isNullOrBlank()) {
            filterList.addAll(countList)
        } else {
            filterList.addAll(countList.filter {
                it.school.contains(str)
            })
        }
    }
}
