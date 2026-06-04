package com.Azhe.ghs.gshschedule.utils

import retrofit2.Retrofit

class MyRetrofitUtils private constructor() {
    private val retrofit = Retrofit.Builder().baseUrl("https://i.wakeup.fun/").build()
    private val myService = retrofit.create(MyRetrofitService::class.java)

    fun getService(): MyRetrofitService {
        return myService
    }

    companion object {
        val instance: MyRetrofitUtils by lazy { MyRetrofitUtils() }
    }
}
