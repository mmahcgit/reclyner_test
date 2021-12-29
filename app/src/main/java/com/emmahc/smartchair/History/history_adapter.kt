package com.emmahc.smartchair.History

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emmahc.smartchair.R
import com.emmahc.smartchair.ServerAPI.Send_to_Server_measure_history
import com.emmahc.smartchair.dto.result_history_api_dto

open class history_adapter(val context: Context, private val deviceList:ArrayList<result_history_api_dto>,private val day_standard:Int, private val health_standard:Int):
    RecyclerView.Adapter<history_adapter.history_adapterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): history_adapter.history_adapterHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.history_health_layout, parent,false)
        return history_adapterHolder(view)
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    override fun onBindViewHolder(holder: history_adapter.history_adapterHolder, position: Int) {
        holder.bind(deviceList[position])
    }
    inner class history_adapterHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView){
        //layout
        val date_layout = itemView.findViewById<LinearLayout>(R.id.date_layout)
        val heart_rate_layout = itemView.findViewById<LinearLayout>(R.id.heart_rate_layout)
        val stress_layout = itemView.findViewById<LinearLayout>(R.id.stress_layout)
        val blood_pressure_layout = itemView.findViewById<LinearLayout>(R.id.blood_pressure_layout)

        var date_text = itemView.findViewById<TextView>(R.id.date_text)
        var heart_rate_text = itemView.findViewById<TextView>(R.id.heart_rate_text)
        var stress_text = itemView.findViewById<TextView>(R.id.stress_text)
        var blood_pressure_text = itemView.findViewById<TextView>(R.id.blood_pressure_text)

        fun bind(result_history_api_dto: result_history_api_dto){
            date_text.text = Send_to_Server_measure_history(context).change_Unix_to_time(result_history_api_dto.recordedTime.toString())
            heart_rate_text.text = result_history_api_dto.heartRate.toString()
            stress_text.text = result_history_api_dto.stress.toString()
            val blood_pressure_value = "${result_history_api_dto.systolic}/${result_history_api_dto.diastolic}"
            blood_pressure_text.text = blood_pressure_value
            //"ALL","심박수","스트레스","혈압"
            when(health_standard){
                //all
                0 -> {
                    heart_rate_layout.visibility = View.VISIBLE
                    stress_layout.visibility = View.VISIBLE
                    blood_pressure_layout.visibility = View.VISIBLE
                }
                //심박수
                1 -> {
                    heart_rate_layout.visibility = View.VISIBLE
                    stress_layout.visibility = View.GONE
                    blood_pressure_layout.visibility = View.GONE
                }
                //스트레스
                2 -> {
                    heart_rate_layout.visibility = View.GONE
                    stress_layout.visibility = View.VISIBLE
                    blood_pressure_layout.visibility = View.GONE
                }
                //혈압
                3 -> {
                    heart_rate_layout.visibility = View.GONE
                    stress_layout.visibility = View.GONE
                    blood_pressure_layout.visibility = View.VISIBLE
                }
            }
        }
    }


}