package com.cbsd.scan.http

import com.cbsd.scan.Response
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface CodeService {
    @POST
    fun addResult(@Url url: String, @Body result: String): Observable<Response>
}