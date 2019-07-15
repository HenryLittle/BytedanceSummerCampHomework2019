package com.byted.camp.todolist.ViewController.debug

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.byted.camp.todolist.R

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.LinkedHashMap

class DebugActivity : AppCompatActivity() {

    private val internalPath: String
        get() {
            val dirMap = LinkedHashMap<String, File>()
            dirMap["cacheDir"] = cacheDir
            dirMap["filesDir"] = filesDir
            dirMap["customDir"] = getDir("custom", Context.MODE_PRIVATE)
            return getCanonicalPath(dirMap)
        }

    private val externalPrivatePath: String
        get() {
            val dirMap = LinkedHashMap<String, File>()
            dirMap["cacheDir"] = externalCacheDir!!
            dirMap["filesDir"] = getExternalFilesDir(null)!!
            dirMap["picturesDir"] = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            return getCanonicalPath(dirMap)
        }

    private val externalPublicPath: String
        get() {
            val dirMap = LinkedHashMap<String, File>()
            dirMap["rootDir"] = Environment.getExternalStorageDirectory()
            dirMap["picturesDir"] = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return getCanonicalPath(dirMap)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        setTitle(R.string.action_debug)

        val printBtn = findViewById<Button>(R.id.btn_print_path)
        val pathText = findViewById<TextView>(R.id.text_path)
        val fileText = findViewById<TextView>(R.id.text_file_content)
        val fileButton = findViewById<Button>(R.id.btn_file)
        printBtn.setOnClickListener {
            val sb = StringBuilder()
            sb.append("===== Internal Private =====\n").append(internalPath)
                    .append("===== External Private =====\n").append(externalPrivatePath)
                    .append("===== External Public =====\n").append(externalPublicPath)
            pathText.text = sb
        }

        val permissionBtn = findViewById<Button>(R.id.btn_request_permission)
        permissionBtn.setOnClickListener(View.OnClickListener {
            val state = ActivityCompat.checkSelfPermission(this@DebugActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (state == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@DebugActivity, "already granted",
                        Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            ActivityCompat.requestPermissions(this@DebugActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION)
        })

        fileButton.setOnClickListener {
            val file = File(cacheDir, "temp.txt")
            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(file)
                fileOutputStream.write("This is some random text!".toByteArray())
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    fileOutputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            var fileInputStream: FileInputStream? = null
            val fileContent = StringBuilder()
            try {
                fileInputStream = FileInputStream(file)

                val buffer = ByteArray(1024)
                var n: Int
                while ((n = fileInputStream.read(buffer)) != -1) {
                    fileContent.append(String(buffer, 0, n))
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    fileInputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            fileText.text = fileContent.toString()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.size == 0 || grantResults.size == 0) {
            return
        }
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            val state = grantResults[0]
            if (state == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@DebugActivity, "permission granted",
                        Toast.LENGTH_SHORT).show()
            } else if (state == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this@DebugActivity, "permission denied",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        private val REQUEST_CODE_STORAGE_PERMISSION = 1001

        private fun getCanonicalPath(dirMap: Map<String, File>): String {
            val sb = StringBuilder()
            try {
                for (name in dirMap.keys) {
                    sb.append(name)
                            .append(": ")
                            .append(dirMap[name]!!.canonicalPath)
                            .append('\n')
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return sb.toString()
        }
    }
}
