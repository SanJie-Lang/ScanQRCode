package com.cbsd.scan.http

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request();
        val builder = request.newBuilder()
        builder.addHeader("X-Bmob-Application-Id", "84279047c425f1d2a19395778d9a37af")
        builder.addHeader("X-Bmob-REST-API-Key", "71617bcb85eeddfc27b2fc9a20f0f357")
        builder.addHeader("Content-Type", "application/json")
        return chain.proceed(builder.build())
    }
}