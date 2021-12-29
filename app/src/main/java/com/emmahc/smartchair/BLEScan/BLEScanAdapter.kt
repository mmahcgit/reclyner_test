package com.emmahc.smartchair.BLEScan

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emmahc.smartchair.R
import com.emmahc.smartchair.dto.BLEScan_dto
import java.util.*

class BLEScanAdapter(val context: Context, private val device_list: ArrayList<BLEScan_dto>, val itemClick:(BLEScan_dto)->Unit):
        RecyclerView.Adapter<BLEScanAdapter.BLEScanHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BLEScanHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.blescan_item_layout, parent, false)
        return BLEScanHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: BLEScanHolder, position: Int) {
        holder.bind(device_list[position])
    }

    override fun getItemCount(): Int {
        return device_list.size
    }
    inner class BLEScanHolder(itemView:android.view.View,itemClick: (BLEScan_dto) -> Unit): RecyclerView.ViewHolder(itemView){
        val device_name: TextView = itemView.findViewById<TextView>(R.id.device_item_cardView_name)
        val device_address: TextView = itemView.findViewById<TextView>(R.id.device_item_cardView_address)
        fun bind(blescanDto: BLEScan_dto){
            device_name.text = blescanDto.device_name
            device_address.text = blescanDto.device_address
            itemView.setOnClickListener {
                itemClick(blescanDto)
            }
        }
    }
}