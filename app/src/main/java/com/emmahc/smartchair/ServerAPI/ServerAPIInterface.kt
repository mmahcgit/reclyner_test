package com.emmahc.smartchair.ServerAPI

import com.emmahc.smartchair.dto.API_Result_Dto
import com.emmahc.smartchair.dto.Raw_weightDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ServerAPIInterface {
    @Headers("accept: application/json","content-type: application/json")
    @POST("api/raw")
    fun post(
        @Body rawData: Raw_weightDto
    ): Call<API_Result_Dto>
}
