package com.iamsdt.findmyphone

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by Shudipto Trafder on 8/27/2017.
 * com.iamsdt.findmyphone
 */
class Adapter(val list: ArrayList<UserContract>,
              val clickListener: ClickListener):
    RecyclerView.Adapter<Adapter.MyViewHolder>(){

    interface ClickListener{
        fun onItemClick(position:Int):Unit
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {
        val user:UserContract = list[position]
        viewHolder!!.nameTV.text = user.name
        viewHolder.numberTV.text = user.phone
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, ViewType: Int):
            MyViewHolder {

        val view:View = LayoutInflater.from(parent?.context)
                .inflate(R.layout.tracker_list,parent,false)

        return MyViewHolder(view)
    }

    inner class MyViewHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val cardView: CardView = itemView.findViewById(R.id.tracker_card)
        val nameTV:TextView = itemView.findViewById(R.id.tracker_name)
        val numberTV:TextView = itemView.findViewById(R.id.tracker_number)


        init {
            cardView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            //val position:Int = adapterPosition
            clickListener.onItemClick(adapterPosition)
        }
    }
}