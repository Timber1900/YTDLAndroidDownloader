package com.ytdl.downloader

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.chaquo.python.Python


class MainActivity : Activity() {
    private var textView: TextView? = null
    private var url: String? = null
    @RequiresApi(Build.VERSION_CODES.M)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE), 1)


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
                    val test = pythonFile.callAttr("test", insideurl).toString()
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

    fun runTest(view: View?) {
        textView!!.text = "Running"
        val insideurl: String? = url
        if (insideurl != null) {
            val python = Python.getInstance()
            val pythonFile = python.getModule("main")
            val test = pythonFile.callAttr("test", insideurl).toString()
            textView!!.text = test

        }
        else {
            textView!!.text = "No URL"
        }
    }

}
