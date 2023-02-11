package com.example.statussaver

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.example.statussaver.adapter.TabPagerAdapter
import com.example.statussaver.databinding.ActivityMainBinding
import com.example.statussaver.util.Method
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.*
import java.util.*


class MainActivity : AppCompatActivity()  {

    //private lateinit var method: Method
    private lateinit var toolbar: MaterialToolbar
    private lateinit var viewPager: ViewPager
    private lateinit var binding: ActivityMainBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var navigationView: NavigationView
    private lateinit var pagerAdapter: TabPagerAdapter


    private val REQUEST_PERMISSIONS = 1234
    private val PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private var context: Context? = null
    var method: Method? = null

    var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data!!
            context!!.contentResolver.takePersistableUriPermission(
                data.data!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = applicationContext

        val pageTitle = listOf(
            getString(R.string.image), getString(R.string.video), getString(R.string.saved),getString(R.string.direct)
        )

        toolbar = binding.toolbarMain
        viewPager = binding.viewPager

        toolbar.title = getString(R.string.app_name)

        tabLayout = binding.tabLayout

        for (i in 0 until pageTitle.count()) {
            tabLayout.addTab(tabLayout.newTab().setText(pageTitle[i]))
        }
        tabLayout.tabGravity = GRAVITY_FILL

        pagerAdapter = TabPagerAdapter(supportFragmentManager, tabLayout.tabCount, this)
        viewPager.adapter = pagerAdapter


        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                viewPager.currentItem = tab.position
                //navigationView.menu.getItem(tab.position).isChecked = true
            }

            override fun onTabUnselected(tab: Tab) {

            }
            override fun onTabReselected(tab: Tab) {

            }
        })

        /*val drawer = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(this,drawer,toolbar,
            R.string.nav_drawer_open,R.string.nav_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer)

        navigationView = binding.navView
        navigationView.itemIconTintList = null*/

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS && grantResults.size > 0) {
            if (arePermissionDenied()) {
                (Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE)) as ActivityManager).clearApplicationUserData()
                recreate()
            }
        }
    }

    private fun arePermissionDenied(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return contentResolver.persistedUriPermissions.size <= 0
        }
        for (permissions in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    permissions
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {

            // If Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestPermissionQ()
                return
            }
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS)
            return
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private fun requestPermissionQ() {
        val sm = context!!.getSystemService(STORAGE_SERVICE) as StorageManager
        val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
        val startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
        var scheme = uri.toString()
        scheme = scheme.replace("/root/", "/document/")
        scheme += "%3A$startDir"
        uri = Uri.parse(scheme)

        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        intent.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        activityResultLauncher.launch(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        moveTaskToBack(true)

    }


}