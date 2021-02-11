package com.iiitd.ucsf.threads

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.iiitd.ucsf.manager.DownloadManager
import java.io.File
import java.net.URL


class LongThread : Runnable {
    var threadNo = 0
    var handler: Handler? = null
    lateinit var fileUrl: String
    lateinit var filename : String
      constructor() {}
    constructor(  threadNo: Int, fileUrl: String?, handler: Handler?,fileName: String?) {
        this.threadNo = threadNo
        this.handler = handler

         if (fileUrl != null) {
            this.fileUrl = fileUrl
        }

        if (fileName != null) {
            this.filename=fileName
        }
    }

    override fun run() {
        Log.i(TAG, "Starting Thread : $threadNo")
        Looper.prepare();

        //getBitmap(fileUrl)
        val folder = File(Environment.getExternalStorageDirectory().toString() + "/" + "All_Audios")
        if (!folder.exists()) {
            folder.mkdirs()
        }
       // DownloadManager.initDownload( this,fileUrl, folder.absolutePath, filename)
        DownloadManager.initDownload( fileUrl, folder.absolutePath, filename)

        Thread.sleep(60000)
        Log.i(TAG, "stopping Thread : $threadNo")
        sendMessage(threadNo, "Thread Completed")
/*
        if(DownloadManager.isThreadCompleted){
          sendMessage(threadNo, "Thread Completed")
            Log.i(TAG, "Thread Completed $threadNo")
        }*/



    }


    private fun sendMessage(what: Int, msg: String?) {
        val message = handler!!.obtainMessage(what, msg)
        message.sendToTarget()
    }

    private fun getBitmap(url: String?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            // Download Image from URL
            val input = URL(url).openStream()
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input)
            // Do extra processing with the bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    companion object {
        const val TAG = "LongThread"
    }
}