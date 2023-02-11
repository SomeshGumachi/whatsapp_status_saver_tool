package com.example.statussaver.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.statussaver.R
import com.example.statussaver.activity.VideoPlayer

class ItemViewerAdapter(private val activity: Activity, private val list: List<String>, private val type: String) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater =
            (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val view = layoutInflater.inflate(R.layout.item_view_adapter, container, false)

        val touchImageView: ImageView = view.findViewById(R.id.touchimage_adapter)
        val imgPlay: ImageView = view.findViewById(R.id.img_media_view_play)

        if (type == "image"){
            imgPlay.visibility = View.GONE
        } else if(type == "all"){
            if (list[position].toString().contains(".jpg")){
                imgPlay.visibility = View.GONE
            }
        }

        imgPlay.setOnClickListener {
            activity.startActivity(Intent(activity,VideoPlayer::class.java)
                .putExtra("link",list[position].toString()))
        }

        Glide.with(activity).load(list[position].toString())
            .placeholder(R.drawable.placeholder_image_potrait)
            .into(touchImageView)

        container.addView(view)

        return view
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return  view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}
