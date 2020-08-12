package com.ytdl.downloader

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView

import com.chaquo.python.Python


class MainActivity : Activity() {
    private var textView: TextView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById<View>(R.id.textView) as TextView
    }

    fun startProgress(view: View?) {

        textView!!.text = "Running"

        Thread(Task()).start()
    }

    internal inner class Task : Runnable {
        override fun run() {

            val python = Python.getInstance()
            val pythonFile = python.getModule("main")
            val test = pythonFile.callAttr("test").toString()


            runOnUiThread {
                textView!!.text = test
            }
        }
    }

}
