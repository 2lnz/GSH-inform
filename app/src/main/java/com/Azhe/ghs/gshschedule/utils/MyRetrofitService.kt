package com.Azhe.ghs.gshschedule.utils

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP

interface MyRetrofitService {
    @GET("count_html")
    fun getHtmlCount(): Call<ResponseBody>

    @HTTP(method = "POST", path = "apply_html", hasBody = true)
    @FormUrlEncoded
    fun postHtml(@Field("school") school: String,
                 @Field("type") type: String,
                 @Field("html") html: String,
                 @Field("qq") qq: String
    ): Call<ResponseBody>
}
