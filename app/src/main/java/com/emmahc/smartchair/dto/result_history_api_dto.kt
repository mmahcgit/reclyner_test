package com.emmahc.smartchair.dto

data class result_history_api_dto(
        val uniqueID:Double? = null,
        val recordedTime:Long?=null,
        val stress:Double? = null,
        val heartRate:Double? = null,
        val systolic:Double? = null,
        val diastolic:Double? = null,
        val weight:Double? = null
)