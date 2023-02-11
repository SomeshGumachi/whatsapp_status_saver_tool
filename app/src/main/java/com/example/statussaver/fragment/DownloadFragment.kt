package com.example.statussaver.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.statussaver.R
import com.example.statussaver.`interface`.OnClick
import com.example.statussaver.activity.ItemViewerActivity
import com.example.statussaver.adapter.ItemListAdapter
import com.example.statussaver.util.Constant
import com.example.statussaver.util.Method
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadFragment : Fragment() {
    lateinit var onClick: OnClick
    private lateinit var method: Method
    lateinit var recyclerView: RecyclerView
    private lateinit var noDataTextView: TextView
    lateinit var progressBar: ProgressBar
    private var itemListAdapter: ItemListAdapter? = null
    private lateinit var file: File

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment,container,false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick = object : OnClick {
            override fun position(position: Int, type: String?) {
                startActivity(
                    Intent(activity, ItemViewerActivity::class.java)
                        .putExtra("position",position)
                        .putExtra("type",type)
                )
            }
        }

        method = Method(requireActivity(), onClick)
        recyclerView = view.findViewById(R.id.recyclerview_fragment)
        noDataTextView = view.findViewById(R.id.tv_no_data_fragment)
        progressBar = view.findViewById(R.id.progressbar_fragment)
        progressBar.visibility = View.GONE

        recyclerView.setHasFixedSize(true)

        getSavedFiles()

    }

    fun getSavedFiles(){
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
            val statusSavedDir = File(downloadDir, "Status")
            val fileList = arrayListOf<String>()
            try {
                if (statusSavedDir.exists()) {
                    for (file in statusSavedDir.listFiles()!!){
                        fileList.add(file.absolutePath)
                    }
                }

                Constant.downloadArray.clear()
                Constant.downloadArray = fileList
            } catch (e: Exception){
                Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
            }


            withContext(Dispatchers.Main){
                if (Constant.downloadArray.size == 0){
                    progressBar.visibility = View.GONE
                    noDataTextView.visibility = View.VISIBLE
                }else {
                    progressBar.visibility = View.VISIBLE
                    noDataTextView.visibility = View.GONE
                    itemListAdapter = ItemListAdapter(requireActivity(), Constant.downloadArray, "all", method)
                    recyclerView.adapter = itemListAdapter
                }
                progressBar.visibility = View.GONE
            }
        }

    }

    override fun onResume() {
        super.onResume()
        itemListAdapter?.notifyDataSetChanged()
        /*Constant.downloadArray.clear()
        //Constant.downloadArray = getListFiles(file) as MutableList<File>
        if (Constant.downloadArray.size == 0) {
            noDataTextView.visibility = View.VISIBLE
        } else {
            noDataTextView.visibility = View.GONE
            //viewAdapter = ViewAdapter(requireActivity(), Constant.downloadArray, "all", method)
            recyclerView.adapter = viewAdapter
        }*/
        if (Constant.downloadArray.size == 0){
            noDataTextView.visibility = View.VISIBLE
        }else {
            noDataTextView.visibility = View.GONE
        }

    }



}

