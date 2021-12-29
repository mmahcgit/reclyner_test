package com.emmahc.smartchair.dto

data class History_API_Dto(

        val recordedTime:Long = 0,
        val stress:Int = 0,
        val heartRate:Int = 0,
        val systolic:Int = 0,
        val diastolic:Int = 0,
        val weight: Double = 0.0,
        val uniqueID:Int = 0

)

