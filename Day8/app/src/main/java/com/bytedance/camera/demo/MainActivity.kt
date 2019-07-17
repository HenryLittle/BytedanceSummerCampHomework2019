package com.bytedance.camera.demo

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bytedance.camera.demo.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_picture.setOnClickListener { startActivity(Intent(this@MainActivity, TakePictureActivity::class.java)) }

        btn_camera.setOnClickListener { startActivity(Intent(this@MainActivity, RecordVideoActivity::class.java)) }

        btn_custom.setOnClickListener {
            //todo 在这里申请相机、麦克风、存储的权限
            if (Utils.isPermissionsReady(this, permissions)) {
                startActivity(Intent(this@MainActivity, CustomCameraActivity::class.java))
            } else {
                //todo 权限检查
                Utils.reuqestPermissions(this, permissions, REQUEST_EXTERNAL_CAMERA)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_CAMERA -> {
                if (Utils.isPermissionsReady(this, permissions)) {
                    startActivity(Intent(this@MainActivity, CustomCameraActivity::class.java))
                }
            }
        }
    }

    companion object {
        private const val REQUEST_EXTERNAL_CAMERA = 101
    }

}
