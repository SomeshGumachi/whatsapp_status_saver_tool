package com.example.statussaver.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.statussaver.`interface`.OnClick
import com.example.statussaver.activity.ItemViewerActivity
import com.example.statussaver.adapter.ItemListAdapter
import com.example.statussaver.databinding.FragmentBinding
import com.example.statussaver.util.Constant
import com.example.statussaver.util.Method
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class VideoFragment : Fragment() {
    private var _binding: FragmentBinding? = null
    private val binding get() = _binding!!
    lateinit var onClick: OnClick
    lateinit var method: Method
    lateinit var recyclerView: RecyclerView
    lateinit var noDataTextView: TextView
    lateinit var progressBar: ProgressBar
    var itemListAdapter: ItemListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick = object : OnClick{
            override fun position(position: Int, type: String?) {
                startActivity(
                    Intent(activity,ItemViewerActivity::class.java)
                        .putExtra("position",position)
                        .putExtra("type",type)
                )
            }
        }

        method = Method(requireActivity(), onClick)
        recyclerView = binding.recyclerviewFragment
        noDataTextView = binding.tvNoDataFragment
        noDataTextView.visibility = View.GONE
        progressBar = binding.progressbarFragment
        recyclerView.setHasFixedSize(true)

        dataVideo()
    }

    private fun dataVideo(){
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val list = requireActivity().contentResolver.persistedUriPermissions
                val fileDoc = DocumentFile.fromTreeUri(activity?.applicationContext!!,list[0].uri)
                val inFiles = arrayListOf<String>()

                try {
                    for (file: DocumentFile in fileDoc!!.listFiles()){
                        if (file.name!!.endsWith(".mp4")){
                            inFiles.add(file.uri.toString())
                        }
                    }
                    Constant.videoArray.clear()
                    Constant.videoArray = inFiles
                }catch (e: Exception){
                    Toast.makeText(requireContext(),"Something went wrong", Toast.LENGTH_SHORT).show()
                }

                withContext(Dispatchers.Main){
                    if (Constant.videoArray.size == 0){
                        noDataTextView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    } else {
                        progressBar.visibility = View.GONE
                        noDataTextView.visibility = View.GONE
                        itemListAdapter = ItemListAdapter(requireActivity(), Constant.videoArray, "video", method)
                        recyclerView.adapter = itemListAdapter
                    }
                    progressBar.visibility = View.GONE
                }

            } else {
                val root = Environment.getExternalStorageDirectory().toString() + File.separator + "WhatsApp/Media/.Statuses"
                val toFile = File(root)
                val fileList = arrayListOf<String>()

                try {
                    for (file in toFile.listFiles()!!) {
                        if (file.absolutePath.endsWith(".mp4")) {
                            fileList.add(file.absolutePath)
                        }
                    }

                    Constant.videoArray.clear()
                    Constant.videoArray = fileList
                } catch (e: Exception){
                    Toast.makeText(requireContext(),"Something went wrong", Toast.LENGTH_SHORT).show()
                }

                withContext(Dispatchers.Main){
                    if (Constant.videoArray.size == 0){
                        noDataTextView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    } else {
                        progressBar.visibility = View.VISIBLE
                        noDataTextView.visibility = View.GONE
                        itemListAdapter = ItemListAdapter(requireActivity(),Constant.videoArray,"video",method)
                        recyclerView.adapter = itemListAdapter
                    }
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        itemListAdapter?.notifyDataSetChanged()

        if (Constant.videoArray.size == 0){
            noDataTextView.visibility = View.VISIBLE
        }else {
            noDataTextView.visibility = View.GONE
        }

    }
}