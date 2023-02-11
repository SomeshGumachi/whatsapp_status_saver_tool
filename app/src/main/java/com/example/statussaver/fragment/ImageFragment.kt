package com.example.statussaver.fragment

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.util.Log
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


class ImageFragment : Fragment() {

    private var _binding: FragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: ProgressBar
    private lateinit var root: String
    private var itemListAdapter: ItemListAdapter? = null
    private lateinit var method: Method
    private var onClick: OnClick? = null
    lateinit var recyclerView: RecyclerView
    lateinit var noDataTextView: TextView

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
        onClick = object : OnClick {
            override fun position(position: Int, type: String?) {
                startActivity(
                    Intent(activity, ItemViewerActivity::class.java)
                        .putExtra("position", position)
                        .putExtra("type", type)
                )
            }
        }

        method = Method(requireActivity(), onClick as OnClick)
        recyclerView = binding.recyclerviewFragment
        noDataTextView = binding.tvNoDataFragment
        noDataTextView.visibility = View.GONE
        progressBar = binding.progressbarFragment
        recyclerView.setHasFixedSize(true)

        dataImage()
    }

    private fun dataImage(){
        progressBar.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.IO) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                val list = requireActivity().contentResolver.persistedUriPermissions
                val fileDoc = DocumentFile.fromTreeUri(activity?.applicationContext!!, list[0].uri)
                val inFiles = arrayListOf<String>()

                try {
                    for (file: DocumentFile in fileDoc!!.listFiles()) {
                        if (file.name!!.endsWith(".jpg") || file.name!!.endsWith(".gif")
                            || file.name!!.endsWith(".png")
                        ) {
                            inFiles.add(file.uri.toString())
                        }
                    }
                    Constant.imageArray.clear()
                    Constant.imageArray = inFiles
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }

                withContext(Dispatchers.Main) {
                    if (Constant.videoArray.size == 0) {
                        noDataTextView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    } else {
                        progressBar.visibility = View.GONE
                        noDataTextView.visibility = View.GONE
                        itemListAdapter =
                            ItemListAdapter(requireActivity(), Constant.imageArray, "image", method)
                        recyclerView.adapter = itemListAdapter
                    }
                    progressBar.visibility = View.GONE
                }

            } else {
                root = Environment.getExternalStorageDirectory()
                    .toString() + File.separator + "WhatsApp/Media/.Statuses"

                val toFile = File(root)
                val fileList = arrayListOf<String>()

                try {
                    for (file in toFile.listFiles()!!) {
                        if (file.absolutePath.endsWith(".jpg") || file.absolutePath.endsWith("gif")) {
                            fileList.add(file.absolutePath)
                        }
                    }

                    Constant.imageArray.clear()
                    Constant.imageArray = fileList
                }catch (e: Exception){
                    Toast.makeText(requireContext(),"Something went wrong", Toast.LENGTH_SHORT).show()
                }

                withContext(Dispatchers.Main) {
                    if (Constant.imageArray.size == 0) {
                        noDataTextView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    } else {
                        progressBar.visibility = View.VISIBLE
                        noDataTextView.visibility = View.GONE
                        itemListAdapter =
                            ItemListAdapter(requireActivity(), Constant.imageArray, "image", method)
                        recyclerView.adapter = itemListAdapter
                    }
                }
                progressBar.visibility = View.GONE
            }
        }
    }



    override fun onResume() {
        super.onResume()
        itemListAdapter?.notifyDataSetChanged()

        if (Constant.imageArray.size == 0){
            noDataTextView.visibility = View.VISIBLE
        }else {
            noDataTextView.visibility = View.GONE
        }

    }
}

