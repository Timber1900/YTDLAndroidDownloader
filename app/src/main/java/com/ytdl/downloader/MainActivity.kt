package com.ytdl.downloader

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chaquo.python.Python



class MainActivity : Activity() {
    private var textView: TextView? = null
    private var url: String? = null
    @RequiresApi(Build.VERSION_CODES.M)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission(Manifest.permission.INTERNET,100)
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,101)



        setContentView(R.layout.activity_main)
        textView = findViewById<View>(R.id.textView) as TextView

        if (intent.extras != null) {
            val extras = intent.extras
            url = extras!!.getString(Intent.EXTRA_TEXT).toString()
        }

    }

        @SuppressLint("SetTextI18n")
        fun startProgress(view: View?) {
            textView!!.text = "Running"
            Thread(Runnable {
                val insideurl: String? = url
                if (insideurl != null) {
                    val python = Python.getInstance()
                    val pythonFile = python.getModule("main")
                    val test = pythonFile.callAttr("run", insideurl).toString()
                    runOnUiThread {
                        textView!!.text = test
                    }
                }
                else {
                    runOnUiThread {
                        textView!!.text = "No URL"
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
        } else {
            Toast.makeText(
                this@MainActivity,
                "Permission already granted",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}
