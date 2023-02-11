package com.example.statussaver.activity

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.statussaver.R
import com.example.statussaver.adapter.ItemViewerAdapter
import com.example.statussaver.databinding.ActivityItemViewerBinding
import com.example.statussaver.util.Constant
import com.example.statussaver.util.Method
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ItemViewerActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    private var selectedPosition: Int? =null
    private var type: String = ""
    lateinit var method: Method
    var itemArray: List<String> = arrayListOf()
    private var binding: ActivityItemViewerBinding? = null
    private lateinit var viewPager: ViewPager
    lateinit var linearLayoutShare: LinearLayout
    lateinit var linearLayoutDownload: LinearLayout
    lateinit var linearLayoutDelete: LinearLayout
    lateinit var linearLayoutWhatsappShare: LinearLayout
    lateinit var itemViewerAdapter: ItemViewerAdapter
    private var toolbar: MaterialToolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemViewerBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val i = intent
        selectedPosition = i.getIntExtra("position",0)
        type = i.getStringExtra("type")!!

        method = Method(this@ItemViewerActivity)
        /*method.APP_DIR = getExternalFilesDir("Status")!!.path
        val file = File(method.APP_DIR!!)
        if (!file.exists()) {
            if (!file.mkdirs()) {
                //Snackbar.make(binding!!.root, "Something went wrong", Snackbar.LENGTH_SHORT).show()
                Log.e("dirError","directory not created")
            }
        }*/
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val newDirectory = File(downloadDir, "Status")

        if (!newDirectory.exists()) {
            newDirectory.mkdirs()
        }
        //method.APP_DIR = getExternalFilesDir("Status")!!.path


        viewPager = binding!!.viewpagerImageShow
        linearLayoutShare = binding!!.linearlayoutShare
        linearLayoutDownload = binding!!.linearlayoutDownload
        linearLayoutDelete = binding!!.linearlayoutDelete
        linearLayoutWhatsappShare = binding!!.linearlayoutWhatsappShare
        linearLayoutDelete.visibility = View.GONE

        when(type){
            "image" -> itemArray = Constant.imageArray
            "video" -> itemArray = Constant.videoArray
            else -> {
                itemArray = Constant.downloadArray
                linearLayoutDownload.visibility = View.GONE
            }

        }

        progressBar = ProgressBar(this)
        itemViewerAdapter = ItemViewerAdapter(this,itemArray,type)
        viewPager.adapter = itemViewerAdapter
        viewPager.addOnPageChangeListener(addPageChangeListener)

        setCurrentItem(selectedPosition!!)
        checkImage(selectedPosition!!)

        linearLayoutDownload.setOnClickListener {

            downloadImage()
            Toast.makeText(
                this@ItemViewerActivity,
                resources.getString(R.string.downloading),
                Toast.LENGTH_LONG
            ).show()
        }

        linearLayoutDelete.setOnClickListener {
            if (itemArray.isNotEmpty()){
                val files = File(itemArray[viewPager.currentItem].toString())
                files.delete()
                Constant.downloadArray.removeAt(selectedPosition!!)

                //(itemArray as MutableList<File>).remove(itemArray[viewPager.currentItem])
                Toast.makeText(
                    this@ItemViewerActivity,
                    resources.getString(R.string.deleted),
                    Toast.LENGTH_SHORT
                ).show()
                this.finish()
            }
        }

        linearLayoutShare.setOnClickListener {
            when (type) {
                "image" -> method.share(itemArray[viewPager.currentItem], "image",this,false)
                "video" -> method.share(itemArray[viewPager.currentItem], "video",this,false)
                "all" -> {
                    if (itemArray[viewPager.currentItem].endsWith(".mp4")){
                        method.share(itemArray[viewPager.currentItem], "video",this,true)
                    }else{
                        method.share(itemArray[viewPager.currentItem], "image",this,true)
                    }
                }
            }
            Toast.makeText(this@ItemViewerActivity, resources.getString(R.string.share), Toast.LENGTH_SHORT)
                .show()
        }

        linearLayoutWhatsappShare.setOnClickListener {
            when (type) {
                "image" -> method.whatsappShare(itemArray[viewPager.currentItem], "image",this,false)
                "video" -> method.whatsappShare(itemArray[viewPager.currentItem], "video",this,false)
                "all" -> {
                    if (itemArray[viewPager.currentItem].endsWith(".mp4")){
                        method.whatsappShare(itemArray[viewPager.currentItem], "video",this,true)
                    }else{
                        method.whatsappShare(itemArray[viewPager.currentItem], "image",this,true)
                    }
                }
            }
            Toast.makeText(this@ItemViewerActivity, resources.getString(R.string.share), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private val addPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            checkImage(position)
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }


    fun checkImage(selectedPosition: Int){
        when(type){
            "video" -> null
            "image"-> null
            "all"-> linearLayoutDelete.visibility = View.VISIBLE
        }
    }

    private fun setCurrentItem(position: Int) {
        viewPager.setCurrentItem(position, false)
    }

    private fun downloadImage(){
        lifecycleScope.launch(Dispatchers.IO) {
            if (type == "image") {
                try {
                    val uri: String = itemArray[viewPager.currentItem]
                    val fileName = "${System.currentTimeMillis()}.jpg"
                    var fos: OutputStream?
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                        val bitmap =
                            MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(uri))
                        contentResolver.also { resolver ->
                            val contentValues = ContentValues().apply {
                                put(MediaStore.DownloadColumns.DISPLAY_NAME, fileName)
                                put(MediaStore.DownloadColumns.MIME_TYPE, "image/jpg")
                                put(MediaStore.DownloadColumns.RELATIVE_PATH, "Download/Status")
                            }
                            val imageUri: Uri? = resolver.insert(
                                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                contentValues
                            )
                            fos = imageUri?.let { resolver.openOutputStream(it) }
                            fos.use {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                            }
                            fos?.close()

                        }
                    } else {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            FileProvider.getUriForFile(
                                this@ItemViewerActivity, this@ItemViewerActivity.applicationContext.packageName
                                        + ".provider", File(uri)
                            )
                        )
                        val imageDir =
                            Environment.getExternalStoragePublicDirectory("Download/Status")
                        val image = File(imageDir, fileName)
                        fos = FileOutputStream(image)
                        fos.use {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        }
                        fos?.close()
                    }

                } catch (e: Exception) {
                    Log.e("copyFileError", e.toString())
                    Toast.makeText(this@ItemViewerActivity, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (type == "video"){
                try {
                    val filePath: String = itemArray[viewPager.currentItem]
                    val fileName = "${System.currentTimeMillis()}.mp4"
                    val fos: OutputStream?
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val inputStream = contentResolver.openInputStream(Uri.parse(filePath))

                        val contentValues = ContentValues().apply {
                            put(MediaStore.DownloadColumns.DISPLAY_NAME, fileName)
                            put(MediaStore.DownloadColumns.MIME_TYPE, "video/mp4")
                            put(MediaStore.DownloadColumns.RELATIVE_PATH, "Download/Status")
                        }

                        val videoUri: Uri? = contentResolver.insert(
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues
                        )
                        fos = videoUri?.let { contentResolver.openOutputStream(it) }
                        if (inputStream!= null){
                            fos?.write(inputStream.readBytes())
                        }
                        fos?.close()
                        inputStream?.close()
                    }else{
                        val fileUri = FileProvider.getUriForFile(this@ItemViewerActivity,
                            this@ItemViewerActivity.applicationContext.packageName + ".provider",File(filePath))
                        val inputStreamVideo = contentResolver.openInputStream(fileUri)
                        val videoDir =
                            Environment.getExternalStoragePublicDirectory("Download/Status")
                        val video = File(videoDir, fileName)
                        fos = FileOutputStream(video)

                        if (inputStreamVideo != null) {
                            fos.write(inputStreamVideo.readBytes())
                        }

                        inputStreamVideo?.close()
                        fos.close()
                    }

                }catch (e: Exception){
                    Log.e("copyFileError", e.toString())
                    Toast.makeText(this@ItemViewerActivity, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

}