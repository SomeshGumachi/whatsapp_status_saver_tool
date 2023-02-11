package com.example.statussaver.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.statussaver.fragment.DirectChatFragment
import com.example.statussaver.fragment.DownloadFragment
import com.example.statussaver.fragment.ImageFragment
import com.example.statussaver.fragment.VideoFragment

class TabPagerAdapter(private val fm: FragmentManager, private val numOfTabs: Int, val activity: Context) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return numOfTabs
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> ImageFragment()
            1 -> VideoFragment()
            2 -> DownloadFragment()
            else -> DirectChatFragment()
        }
    }
}