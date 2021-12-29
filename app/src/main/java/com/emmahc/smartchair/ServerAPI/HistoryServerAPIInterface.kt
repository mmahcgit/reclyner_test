package com.emmahc.smartchair.ServerAPI

import com.emmahc.smartchair.dto.History_API_Dto
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.*

interface HistoryServerAPIInterface {
    @Headers("accept: application/json","content-type: application/json")
    @GET("api/history")
    fun get_History(
        @Query("start") start:Long,
        @Query("end") end:Long,
        @Query("filterType") filterType:String
    ): Call<JsonArray>
}
