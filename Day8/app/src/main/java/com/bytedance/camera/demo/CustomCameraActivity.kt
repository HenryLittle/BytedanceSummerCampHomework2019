package com.bytedance.camera.demo

import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.Parameters
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.Toast
import com.bytedance.camera.demo.utils.Utils
import com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_IMAGE
import com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_VIDEO
import com.bytedance.camera.demo.utils.Utils.getOutputMediaFile
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_custom_camera.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.abs
import kotlin.math.min


class CustomCameraActivity : AppCompatActivity() {

    private val TAG = CustomCameraActivity::class.java.simpleName
    private var mCamera: Camera? = null

    private var CAMERA_TYPE = CameraInfo.CAMERA_FACING_BACK

    private var isRecording = false
    private var recordingPaused = false

    private var rotationDegree = 0

    private var size: Camera.Size? = null


    private var mMediaRecorder: MediaRecorder? = null

    private var videoFile: File? = null


    private lateinit var orientationEventListener: OrientationEventListener

    private var surfaceW: Int = 0
    private var surfaceH: Int = 0


    private val mPicture = Camera.PictureCallback { data, _ ->
        val pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE) ?: run {
            Log.d("[TAKE PICTURE]", ("Error creating media file, check storage permissions"))
            return@PictureCallback
        }
        Single.just(data).subscribeOn(Schedulers.io())
                .subscribe { array ->
                    Log.d("[TAKE PICTURE]", Thread.currentThread().name)
                    try {
                        val fos = FileOutputStream(pictureFile)
                        fos.write(array)
                        fos.close()
                        Utils.galleryAddPic(this@CustomCameraActivity, pictureFile)
                    } catch (e: FileNotFoundException) {
                        Log.d("[TAKE PICTURE]", "File not found: ${e.message}")
                    } catch (e: IOException) {
                        Log.d("[TAKE PICTURE]", "Error accessing file: ${e.message}")
                    }
                }
        mCamera?.apply { startPreview() }
        Toast.makeText(this@CustomCameraActivity, "Capture success!", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_custom_camera)

        //todo 给SurfaceHolder添加Callback
        img.holder.apply {
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
            addCallback(object : SurfaceHolder.Callback {
                override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                    Log.i(TAG, "surface Changed -> size: ${img.width} ${img.height}")
                    surfaceH = height
                    surfaceW = width
                    releaseCameraAndPreview()
                    holder?.also { startPreview(it) }
                }

                override fun surfaceDestroyed(holder: SurfaceHolder?) = releaseCameraAndPreview()

                override fun surfaceCreated(holder: SurfaceHolder?) {
                    holder?.also {
                        Log.i(TAG, "surface created -> size: ${img.width} ${img.height}")
                        startPreview(it)
                    }
                }
            })
        }

        // monitoring the orientation of the device
        orientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                var orientation = orientation
                if (orientation == ORIENTATION_UNKNOWN) return
                val info = CameraInfo()
                Camera.getCameraInfo(CAMERA_TYPE, info)
                orientation = (orientation + 45) / 90 * 90
                var rotation = 0
                if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                    rotation = (info.orientation - orientation + 360) % 360
                } else {  // back-facing camera
                    rotation = (info.orientation + orientation) % 360
                }

                mCamera?.also {
                    it.parameters?.also { param ->
                        param.setRotation(rotation)
                        it.parameters = param
                    }
                }
            }
        }.apply { enable() }

        btn_picture.setOnClickListener {
            //todo 拍一张照片
            mCamera?.takePicture(null, null, mPicture)
        }

        btn_record.setOnClickListener {
            //todo 录制，第一次点击是start，第二次点击是stop
            recordingPaused = false
            btn_pause.visibility = View.INVISIBLE
            if (isRecording) {
                //todo 停止录制
                isRecording = false
                releaseMediaRecorder()
            } else {
                //todo 录制
                prepareVideoRecorder()
                isRecording = true
                btn_pause.visibility = View.VISIBLE
                btn_pause.text = "PAUSE"
            }
        }

        btn_pause.setOnClickListener {
            if (recordingPaused) {
                if (mMediaRecorder != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        recordingPaused = false
                        btn_pause.text = "PAUSE"
                        mMediaRecorder!!.resume()
                    }

                }
            } else {
                if (mMediaRecorder != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        recordingPaused = true
                        btn_pause.text = "RESUME"
                        mMediaRecorder!!.pause()
                    } else {
                        Toast.makeText(this@CustomCameraActivity, "Pause not supported", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btn_facing.setOnClickListener {
            //todo 切换前后摄像头
            releaseCameraAndPreview()
            CAMERA_TYPE = when (CAMERA_TYPE) {
                CameraInfo.CAMERA_FACING_BACK -> CameraInfo.CAMERA_FACING_FRONT
                CameraInfo.CAMERA_FACING_FRONT -> CameraInfo.CAMERA_FACING_BACK
                else -> CameraInfo.CAMERA_FACING_BACK
            }
            startPreview(img.holder)
        }

        btn_zoom_in.setOnClickListener {
            //todo 调焦，需要判断手机是否支持
            mCamera?.apply {
                parameters?.apply {
                    if (isZoomSupported) {
                        val z = zoom + 10
                        if (z < maxZoom) {
                            if (isSmoothZoomSupported) startSmoothZoom(z)
                            else zoom = z
                            parameters = this
                        }
                    }
                }
            }
        }

        btn_zoom_out.setOnClickListener {
            //todo 调焦，需要判断手机是否支持
            mCamera?.apply {
                parameters?.apply {
                    if (isZoomSupported) {
                        val z = zoom - 10
                        if (z >= 0) {
                            if (isSmoothZoomSupported) startSmoothZoom(z)
                            else zoom = z
                            parameters = this
                        }
                    }
                }
            }
        }

        btn_flash.setOnClickListener {
            // todo flash mode switching
            mCamera?.apply {
                parameters?.also {
                    when (it.flashMode) {
                        Parameters.FLASH_MODE_TORCH -> {
                            it.flashMode = Parameters.FLASH_MODE_AUTO
                            btn_flash.text = "MODE:AUTO"
                        }
                        Parameters.FLASH_MODE_AUTO -> {
                            it.flashMode = Parameters.FLASH_MODE_OFF
                            btn_flash.text = "MODE:OFF"
                        }
                        Parameters.FLASH_MODE_OFF -> {
                            it.flashMode = Parameters.FLASH_MODE_TORCH
                            btn_flash.text = "MODE:TORCH"
                        }
                        else -> {
                        }
                    }
                    parameters = it
                }
            }
        }
    }

    private fun getCamera(position: Int): Camera {
        CAMERA_TYPE = position
        if (mCamera != null) {
            releaseCameraAndPreview()
        }

        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等
        val camera = Camera.open(position)
        getCameraDisplayOrientation(position).also {
            camera.setDisplayOrientation(it)  // make the display orientation right
            rotationDegree = it
        }

        // get the optimal preview size and resize the surface view to avoid stretching
        size = getOptimalPreviewSize(camera.parameters.supportedPreviewSizes, img.width, img.height)?.also {
            camera.parameters?.also { param ->
                param.setPreviewSize(it.width, it.height)
                camera.parameters = param
            }
            setSurfaceSize(it.height, it.width)
        }

        // enable auto focus if possible
        val params: Camera.Parameters? = camera?.parameters
        params?.apply {
            if (supportedFocusModes?.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) == true) {
                focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            }
            camera.parameters = this
            Log.i(TAG, "enable auto focus succeeded")
        } ?: run {
            Log.e(TAG, "enable auto focus failed")
        }

        return camera
    }

    private fun setSurfaceSize(width: Int, height: Int) {
        // avoid stretch on device with special aspect ratio
        // scale it in case some device have a low res cam
        val scale = min(img.width.toDouble() / width.toDouble(), img.height.toDouble() / height.toDouble())
        img.layoutParams.height = (height.toDouble() * scale).toInt()
        img.layoutParams.width = (width.toDouble() * scale).toInt()
        Log.d("[CAM]", "set img size: ${img.width} ${img.height}")
    }


    private fun getCameraDisplayOrientation(cameraId: Int): Int {
        val info = CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = DEGREE_90
            Surface.ROTATION_180 -> degrees = DEGREE_180
            Surface.ROTATION_270 -> degrees = DEGREE_270
        }

        var result: Int
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360
            result = (DEGREE_360 - result) % DEGREE_360  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360
        }
        return result
    }


    private fun releaseCameraAndPreview() {
        //todo 释放camera资源
        mCamera?.also { camera ->
            camera.stopPreview()
            camera.release()
            mCamera = null
        }
    }

    private fun startPreview(holder: SurfaceHolder) {
        //todo 开始预览
        mCamera ?: run { mCamera = getCamera(CAMERA_TYPE) }
        mCamera?.apply {
            try {
                setPreviewDisplay(holder)
                startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun prepareVideoRecorder(): Boolean {
        //todo 准备MediaRecorder
        mMediaRecorder = MediaRecorder()
        orientationEventListener.disable() // sensor listener will conflict with media recorder
        mCamera?.let { camera ->
            // Step 1: Unlock and set camera to MediaRecorder
            camera.unlock()

            mMediaRecorder?.run {
                setCamera(camera)

                // Step 2: Set sources
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setVideoSource(MediaRecorder.VideoSource.CAMERA)

                // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
                setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
//                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
//                setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)

                // Step 4: Set output file
                videoFile = getOutputMediaFile(MEDIA_TYPE_VIDEO)
                Log.d("[CAM]", videoFile.toString())
                setOutputFile(videoFile!!.absolutePath)

                // Step 5: Set the preview output
                setPreviewDisplay(img.holder.surface)
                setOrientationHint(rotationDegree)


                // Step 6: Prepare configured MediaRecorder
                return try {
                    prepare()
                    start()
                    isRecording = true
                    true
                } catch (e: IllegalStateException) {
                    Log.e(TAG, "IllegalStateException preparing MediaRecorder: ${e.message}")
                    releaseMediaRecorder()
                    false
                } catch (e: IOException) {
                    Log.e(TAG, "IOException preparing MediaRecorder: ${e.message}")
                    releaseMediaRecorder()
                    false
                }
            }

        }
        return false
    }


    private fun releaseMediaRecorder() {
        //todo 释放MediaRecorder
        if (mMediaRecorder != null) {
            isRecording = false
            try {
                mMediaRecorder!!.stop()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            mMediaRecorder!!.reset()
            mMediaRecorder!!.release()
            mMediaRecorder = null
            Utils.galleryAddPic(this, videoFile!!)
        }
        mCamera?.apply { lock() }
        orientationEventListener.enable()
    }

    override fun onResume() {
        super.onResume()
        Log.d("[CAM]", "on resume")
        mCamera?.apply {
            setPreviewDisplay(img.holder)
            startPreview()
        }
        orientationEventListener.enable()
    }

    override fun onPause() {
        super.onPause()
        Log.d("[CAM]", "on pause")
        mCamera?.apply { stopPreview() }
        releaseMediaRecorder()
        orientationEventListener.disable()
    }

    override fun onStop() {
        super.onStop()
        Log.d("[CAM]", "on stop")
        releaseCameraAndPreview()
        releaseMediaRecorder()
        orientationEventListener.disable()

    }

    override fun onRestart() {
        super.onRestart()
        startPreview(img.holder)
        orientationEventListener.enable()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCameraAndPreview()
        releaseMediaRecorder()
        orientationEventListener.disable()
    }

    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = h.toDouble() / w

        if (sizes == null) return null

        var optimalSize: Camera.Size? = null
        var minDiff = java.lang.Double.MAX_VALUE

        val targetHeight = min(w, h)

        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
            if (abs(size.height - targetHeight) < minDiff) {
                optimalSize = size
                minDiff = abs(size.height - targetHeight).toDouble()
            }
        }

        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size
                    minDiff = abs(size.height - targetHeight).toDouble()
                }
            }
        }
        return optimalSize
    }


    companion object {
        private const val DEGREE_90 = 90
        private const val DEGREE_180 = 180
        private const val DEGREE_270 = 270
        private const val DEGREE_360 = 360
    }

}
