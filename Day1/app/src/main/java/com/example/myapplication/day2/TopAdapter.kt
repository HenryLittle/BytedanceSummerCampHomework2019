package com.example.myapplication.day2

import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.myapplication.R

class TopAdapter(var list: ArrayList<HotEvent>) : RecyclerView.Adapter<TopAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_rank_recycler_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setData(list[position], position + 1)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(data: HotEvent, rank: Int) {
            val title = itemView.findViewById<TextView>(R.id.item_top_rank_title)
            val number = itemView.findViewById<TextView>(R.id.item_top_rank_number)
            val hotVal = itemView.findViewById<TextView>(R.id.item_top_rank_hot_value)
            title.text = data.title
            // may use string id later
            number.text = "$rank."
            if (Build.VERSION.SDK_INT >= 23) {
                if (rank <= 3) number.setTextColor(itemView.context.getColor(R.color.colorYellowText))
                else number.setTextColor(itemView.context.getColor(R.color.colorWhiteNumberText))
            }
            hotVal.text = "%.1fw".format(data.hotVal)
        }
    }

}