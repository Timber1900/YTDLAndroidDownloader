package com.ytdl.downloader

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.chaquo.python.Python


@SuppressLint("SetTextI18n")
open class MainActivity : AppCompatActivity() {
    private var videoTitle: TextView? = null
    private var progress: ProgressBar? = null
    private var percentage: TextView? = null
    private var velocity: TextView? = null
    private var audioOnly: SwitchCompat? = null
    private var url: String? = null
    private var quality: String? = null
    private var path: String? = null
    private var filepath: TextView? = null

    private lateinit var mSharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.M)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission(Manifest.permission.INTERNET, 100)
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101)
        setContentView(R.layout.activity_main)
        videoTitle = findViewById(R.id.videoTitle)
        progress = findViewById(R.id.progress)
        percentage = findViewById(R.id.percentage)
        velocity = findViewById(R.id.velocity)
        audioOnly = findViewById(R.id.audio)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        quality = mSharedPreferences.getString(getString(R.string.sp_key_quality_preference), "1080")
        path = mSharedPreferences.getString("filepicker", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/ytdl").toString()
    }


    public override fun onStart() {
        if (intent.extras != null) {
            if (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == 0) {
                val extras = intent.extras
                videoTitle!!.text = "Video Acquired"
                url = extras!!.getString(Intent.EXTRA_TEXT).toString()
            }
        }
        super.onStart()
    }

    fun startProgress(view: View?) {

        Thread(Runnable {
            val insideurl: String? = url
            if (insideurl != null) {
                runOnUiThread {
                    videoTitle!!.text = "Downloading"
                }
                val python = Python.getInstance()
                val pythonFile = python.getModule("main")
                val test = pythonFile.callAttr(
                    "run",
                    this,
                    insideurl,
                    audioOnly!!.isChecked,
                    progress,
                    percentage,
                    velocity,
                    quality,
                    path
                ).toString()

                runOnUiThread {
                    videoTitle!!.text = test
                }
            } else {
                runOnUiThread {
                    videoTitle!!.text = "No video selected"
                }
            }
        }).start()
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission)
            == PackageManager.PERMISSION_DENIED
        ) {

            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(permission),
                requestCode
            )
        }
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preference_main, rootKey)
            val filePicker = findPreference("filepicker") as Preference?
            filePicker!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                i.addCategory(Intent.CATEGORY_DEFAULT)
                startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999)
                return@OnPreferenceClickListener true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.SettingsButton -> openSettings()
            else -> {
                openMain()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openSettings() {
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openMain() {
        setContentView(R.layout.activity_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uri: Uri = data!!.data!!
        if (uri.toString().contains("content://com.android.providers")) {
            Toast.makeText(
                this,
                "Don't use \"Open from Downloads\", select the one with your phone name",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val pathInner = FileUtil.getFullPathFromTreeUri(uri, this)
            mSharedPreferences.edit().putString("filepicker", pathInner).apply()
            path = pathInner
        }
    }

}
