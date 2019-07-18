package com.bytedance.camera.demo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.MediaController
import android.widget.Toast
import com.bytedance.camera.demo.utils.Utils
import kotlinx.android.synthetic.main.activity_record_video.*


class RecordVideoActivity : AppCompatActivity() {

    private var permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    )
    private lateinit var mediaController: MediaController
    private var loop = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_video)
        btn_picture.setOnClickListener {
            if (Utils.isPermissionsReady(this, permissions)) {
                //todo 打开摄像机
                openVideoRecordApp()
            } else {
                //todo 权限检查
                Utils.reuqestPermissions(this, permissions, REQUEST_EXTERNAL_CAMERA)
            }
        }
        mediaController = MediaController(this)
        img.setMediaController(mediaController)
        img.setOnCompletionListener {
            if (loop) {
                img.seekTo(1)
                img.start()
            }
        }


        loopCheckBox.setOnCheckedChangeListener { _, isChecked ->
            loop = isChecked
            val enable = when(loop) {
                true -> {
                    if (!img.isPlaying) {
                        img.seekTo(1)
                        img.start()
                    }
                    "enabled"
                }
                false -> "disabled"
            }
            Toast.makeText(this@RecordVideoActivity, "Looping is now $enable", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            //todo 播放刚才录制的视频
            intent?.also { i ->
                img.setVideoURI(i.data)
                img.start()
            }

        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_CAMERA -> {
                //todo 判断权限是否已经授予
                if (Utils.isPermissionsReady(this, permissions)) {
                    //todo 打开摄像机
                    openVideoRecordApp()
                }
            }
        }
    }

    private fun openVideoRecordApp() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            pausePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        img.stopPlayback()
    }

    private fun pausePlayer() {
        img.pause()
    }


    companion object {
        private const val REQUEST_VIDEO_CAPTURE = 1
        private const val REQUEST_EXTERNAL_CAMERA = 101
    }
}
