package com.emmahc.smartchair.dto

import java.sql.Time

data class API_Result_Dto(
        var uniqueID:Int = 0,
        var recordedTime:Long = 0,
        var stress:Double = 0.0,
        var heartRate:Double = 0.0,
        var systolic:Double = 0.0,
        var diastolic:Double = 0.0,
        var weight:Double = 0.0
)

//{"uniqueID":141,"recordedTime":1607315495766,"stress":12,"heartRate":85,"systolic":118,"diastolic":77,"weight":73.0}