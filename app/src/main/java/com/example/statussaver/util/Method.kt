package com.example.statussaver.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.example.statussaver.`interface`.OnClick
import java.io.File


class Method{
    private var onClick : OnClick? = null
    private lateinit var activity : Activity
    private val myPreference = "status"
    val pref_link = "link"
    private var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    constructor(activity: Activity){
        this.activity = activity
        pref = activity.getSharedPreferences(myPreference, 0) // 0 - for private mode
        editor = pref?.edit()
    }

    constructor(activity: Activity, onClick: OnClick){
        this.activity = activity
        this.onClick = onClick
        pref = activity.getSharedPreferences(myPreference, 0) // 0 - for private mode
        editor = pref?.edit()
    }

    fun click(position: Int, type: String?) {
        onClick?.position(position, type)
    }

    fun isAppWA(): Boolean {
        val packageName = "com.whatsapp"
        val mIntent = activity.packageManager.getLaunchIntentForPackage(packageName)
        return mIntent != null
    }

    fun url_type(): String {
        return when (pref!!.getString(pref_link, null)) {
            "w" -> "w"
            "wb" -> "wb"
            "wball" -> "wball"
            else -> "w"
        }
    }

    fun share(link: String, type: String, context: Context,isDownloadScreen: Boolean){
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        if (type == "image"){
            shareIntent.type = "image/*"
        }else{
            shareIntent.type = "video/*"
        }
        //shareIntent.putExtra(Intent.EXTRA_TEXT,activity.resources.getString(R.string.play_more_app))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !isDownloadScreen ){
            shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(link))
        } else{
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.applicationContext.packageName
                +".provider",File(link)))
        }

        activity.startActivity(Intent.createChooser(shareIntent,"Share to"))
    }

    fun whatsappShare(link: String,type: String, context: Context,isDownloadScreen: Boolean){
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        if (type == "image"){
            whatsappIntent.type = "image/*"
        }else{
            whatsappIntent.type = "video/*"
        }
        whatsappIntent.setPackage("com.whatsapp")
        //whatsappIntent.putExtra(Intent.EXTRA_TEXT,activity.resources.getString(R.string.play_more_app))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !isDownloadScreen  ){
            whatsappIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(link))
        } else{
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.applicationContext.packageName
                    +".provider",File(link)))
        }
        try {
            activity.startActivity(whatsappIntent)
        } catch (_: ActivityNotFoundException) {
        }
    }

}


