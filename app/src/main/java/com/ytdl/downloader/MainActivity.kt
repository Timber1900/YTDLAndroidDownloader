package com.ytdl.downloader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.chaquo.python.Python


class MainActivity : Activity() {
    private var textView: TextView? = null
    private var url: String? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById<View>(R.id.textView) as TextView

        if (intent.extras != null) {
            val extras = intent.extras
            url = extras!!.getString(Intent.EXTRA_TEXT).toString()
            textView!!.text = url
        }

    }

    fun startProgress(view: View?) {
        textView!!.text = "Running"
        Thread(Task()).start()
    }

    internal inner class Task : Runnable {
        override fun run() {
            val insideurl: String? = url
            runOnUiThread {
                textView!!.text = insideurl
            }

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
        }
    }

}
