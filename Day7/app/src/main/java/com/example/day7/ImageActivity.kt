package com.example.day7

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_image.*
import permissions.dispatcher.*
import java.util.*


@RuntimePermissions
class ImageActivity : AppCompatActivity() {
    private var imageViewModel: ImageViewModel? = null
    private lateinit var adapter: ImagePagerAdapter
    var pages: MutableList<View> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        imageViewModel = ImageViewModel(application)

        adapter = ImagePagerAdapter()

        loadImageFromExtWithPermissionCheck()
        loadImageFromDrawable()

        adapter.setDatas(pages)
        imageViewPager.adapter = adapter
    }


    private fun loadImageFromDrawable() {
        val imageView = layoutInflater.inflate(R.layout.activity_image_item, null) as ImageView
        Glide.with(this)
            .load(R.drawable.default_img)
            .error(R.drawable.ic_error_black_24dp)
            .into(imageView)
        pages.add(imageView)
    }


    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun loadImageFromExt() {
        val ext = imageViewModel!!.getExternalImages()
        if (ext != null) {
            for (i in ext) {
                val imageView = layoutInflater.inflate(R.layout.activity_image_item, null) as ImageView
                Glide.with(this)
                    .load(i)
                    .apply(RequestOptions().centerInside().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .error(R.drawable.ic_error_black_24dp)
                    .into(imageView)
                pages.add(imageView)
            }
        }
        adapter.notifyDataSetChanged()
        Log.d("[PERMISSION]", "load end")
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onReadExtRational(request: PermissionRequest) {
        PermissionUtils.showRationalDialog(
            this,
            request,
            R.string.permission_msg,
            R.string.continue_button,
            R.string.negative_button
        )
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onReadExtDenied() {
        Toast.makeText(this, "Read Permission Denied, display only in app images", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onReadNeverAgain() {
        PermissionUtils.showNVDialog(
            this,
            R.string.permission_msg,
            R.string.go_to_setting_button,
            R.string.negative_button
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("[PERMISSION]", "in callback")
        onRequestPermissionsResult(requestCode, grantResults)
    }
}


fun Context.imageActivityIntent(): Intent {
    return Intent(this, ImageActivity::class.java)
}
