package com.ytdl.downloader

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chaquo.python.Python

@SuppressLint("SetTextI18n")
class MainActivity : Activity() {
    private var videoTitle: TextView? = null
    private var progress: ProgressBar? = null
    private var percentage: TextView? = null
    private var velocity: TextView? = null
    private  var audioOnly: Switch? = null
    private var url: String? = null
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
    }

    public override fun onStart() {
        if (intent.extras != null) {
            val extras = intent.extras
            videoTitle!!.text = "Video Acquired"
            url = extras!!.getString(Intent.EXTRA_TEXT).toString()
            videoTitle!!.text = url
        }
        super.onStart()
    }

    fun startProgress(view: View?) {
        videoTitle!!.text = "Downloading"
        Thread(Runnable {
            val insideurl: String? = url
            if (insideurl != null) {
                val python = Python.getInstance()
                val pythonFile = python.getModule("main")
                val test = pythonFile.callAttr(
                    "run",
                    this,
                    insideurl,
                    audioOnly!!.isChecked,
                    progress,
                    percentage,
                    velocity
                ).toString()

                runOnUiThread {
                    videoTitle!!.text = test
                }
            } else {
                runOnUiThread {
                    videoTitle!!.text = "No URL"
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
}
