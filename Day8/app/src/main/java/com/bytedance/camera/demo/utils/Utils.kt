package com.bytedance.camera.demo.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Utils {


    const val MEDIA_TYPE_IMAGE = 1
    const val MEDIA_TYPE_VIDEO = 2


    private val NUM_90 = 90
    private val NUM_180 = 180
    private val NUM_270 = 270

    fun convertUriToPath(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val imgPathSel = UriUtils.formatUri(context, uri)
            if (!TextUtils.isEmpty(imgPathSel)) {
                return imgPathSel
            }
        }
        val schema = uri.scheme
        if (TextUtils.isEmpty(schema) || ContentResolver.SCHEME_FILE == schema) {
            return uri.path
        }
        if ("http" == schema) {
            return uri.toString()
        }
        if (ContentResolver.SCHEME_CONTENT == schema) {
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            var cursor: Cursor? = null
            var filePath = ""
            try {
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                if (cursor!!.moveToFirst()) {
                    filePath = cursor.getString(0)
                }
                cursor?.close()
            } catch (e: Exception) {
                // do nothing
            } finally {
                try {
                    cursor?.close()
                } catch (e2: Exception) {
                    // do nothing
                }

            }
            if (TextUtils.isEmpty(filePath)) {
                try {
                    val contentResolver = context.contentResolver
                    val selection = MediaStore.Images.Media._ID + "= ?"
                    var id = uri.lastPathSegment
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !TextUtils.isEmpty(id) && id!!.contains(":")) {
                        id = id.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    }
                    val selectionArgs = arrayOf(id)
                    cursor = contentResolver
                            .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null)
                    if (cursor!!.moveToFirst()) {
                        filePath = cursor.getString(0)
                    }
                    cursor?.close()

                } catch (e: Exception) {
                    // do nothing
                } finally {
                    try {
                        cursor?.close()
                    } catch (e: Exception) {
                        // do nothing
                    }

                }
            }
            return filePath
        }
        return null
    }

    /**
     * Create a File for saving an image or video
     */
    fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo")
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile: File
        mediaFile = when (type) {
            MEDIA_TYPE_IMAGE -> File(mediaStorageDir.path + File.separator +
                    "IMG_" + timeStamp + ".jpg")
            MEDIA_TYPE_VIDEO -> File(mediaStorageDir.path + File.separator +
                    "VID_" + timeStamp + ".mp4")
            else -> return null
        }

        return mediaFile
    }

    fun galleryAddPic(context: Context, file: File) {
        galleryAddPic(context, Uri.fromFile(file))
    }

    fun galleryAddPic(context: Context, uri: Uri) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            mediaScanIntent.data = uri
            context.sendBroadcast(mediaScanIntent)
        }
    }

    fun rotateImage(bitmap: Bitmap, path: String): Bitmap {
        var srcExif: ExifInterface? = null
        try {
            srcExif = ExifInterface(path)
        } catch (e: IOException) {
            e.printStackTrace()
            return bitmap
        }

        val matrix = Matrix()
        var angle = 0
        val orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> angle = NUM_90
            ExifInterface.ORIENTATION_ROTATE_180 -> angle = NUM_180
            ExifInterface.ORIENTATION_ROTATE_270 -> angle = NUM_270
            else -> {
            }
        }
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height

        val mtx = Matrix()
        mtx.postRotate(degree.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
    }

    fun isPermissionsReady(activity: Activity, permissions: Array<String>?): Boolean {
        if (permissions == null || android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        for (i in permissions.indices) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    fun reuqestPermissions(activity: Activity, permissions: Array<String>?, requestCode: Int) {
        if (permissions == null || android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val mPermissionList = ArrayList<String>()
        for (i in permissions.indices) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i])//添加还未授予的权限
            }
        }
        if (mPermissionList.size > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}
