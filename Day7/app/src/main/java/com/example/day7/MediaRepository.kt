package com.example.day7

import android.app.Application
import android.os.Environment
import android.util.Log
import java.io.File

class MediaRepository(application: Application) {

    private val externalPublicPath: String?
        get() {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)?.canonicalPath
        }

    private val supportedImageTypes = arrayOf("jpg", "png", "gif")
    private val imageFiles: MutableList<String> = ArrayList()

    fun getExternalPublicImages(): List<String> {
        if (externalPublicPath != null) {
            val file = File(externalPublicPath!!)
            listFiles(file)
        }
        return imageFiles
    }

    private fun listFiles(file: File) {
        val files = file.listFiles()
        if (files != null) {
            for (f in files) {
                if (f != null) {
                    if (f.isDirectory) {
                        listFiles(f)
                    } else {
                        if (f.extension in supportedImageTypes) {
                            Log.d("[FILE]", "path:" + f.canonicalPath)
                            imageFiles.add(f.canonicalPath)
                        }
                    }
                }
            }
        }
    }

}