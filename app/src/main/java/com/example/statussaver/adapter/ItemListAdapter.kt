package com.example.statussaver.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.statussaver.R
import com.example.statussaver.util.Method

class ItemListAdapter(
    val activity: Activity,
    val stringList: List<String>,
    val string: String,
    val method: Method

) : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById<ImageView>(R.id.image_view)
        val imgPlay: ImageView = itemView.findViewById(R.id.img_play)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_adapter,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(activity).load(stringList[position].toString())
            .placeholder(R.drawable.placeholder_image_potrait).into(holder.imageView)

        if (string == "image"){
            holder.imgPlay.visibility = View.GONE
        } else if(string == "all"){
            if (stringList[position].contains(".jpg")){
                holder.imgPlay.visibility = View.GONE
            }
        }

        holder.imageView.setOnClickListener {
            method.click(position, string)
        }

    }

    override fun getItemCount(): Int {
        return  stringList.size
    }
}