package com.bytedance.camera.demo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import com.bytedance.camera.demo.utils.Utils
import kotlinx.android.synthetic.main.activity_take_picture.*
import java.io.File
import java.io.IOException

class TakePictureActivity : AppCompatActivity() {

    private var imageFile: File? = null
    private var permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)
        btn_picture.setOnClickListener {
            if (Utils.isPermissionsReady(this@TakePictureActivity, permissions)) {
                takePicture()
            } else {
                //todo 在这里申请相机、存储的权限
                Utils.reuqestPermissions(this@TakePictureActivity, permissions, REQUEST_EXTERNAL_STORAGE)
            }
        }

    }

    private fun takePicture() {
        //todo 打开相机
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                imageFile = try {
                    Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                imageFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "${BuildConfig.APPLICATION_ID}.mProvider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            //todo 处理返回数据
            setPic()
        }
    }

    private fun setPic() {
        //todo 根据imageView裁剪
        val targetH = img.height
        val targetW = img.width

        //todo 根据缩放比例读取文件，生成Bitmap
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(imageFile!!.absolutePath, options)
        val sample = Utils.calculateInSampleSize(options, targetW, targetH)
        options.apply {
            inJustDecodeBounds = false
            inSampleSize = sample
        }

        BitmapFactory.decodeFile(imageFile!!.absolutePath, options)?.also { bitmap ->
            //todo 如果存在预览方向改变，进行图片旋转
            val exif = ExifInterface(imageFile!!.absolutePath)
            val matrix = Matrix()
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_NORMAL -> matrix.postRotate(0f)
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }
            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            //todo 显示图片
            img.setImageBitmap(rotatedBitmap)

            // add image to gallery
            imageFile?.also { Utils.galleryAddPic(this@TakePictureActivity, it) }
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                //todo 判断权限是否已经授予
                if (Utils.isPermissionsReady(this@TakePictureActivity, permissions)) {
                    takePicture()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_EXTERNAL_STORAGE = 101
    }
}
