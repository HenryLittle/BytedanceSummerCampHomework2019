package com.example.day7

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.day7.player.VideoPlayerIJK
import com.example.day7.player.VideoPlayerListener
import kotlinx.android.synthetic.main.include_volum_control.*
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import android.media.AudioManager





class IJKPlayerActivity : Activity(), View.OnClickListener {
    internal var ijkPlayer: VideoPlayerIJK? = null
    private lateinit var btnSetting: Button
    internal lateinit var btnStop: Button
    internal lateinit var btnPlay: Button
    internal lateinit var seekBar: SeekBar
    private lateinit var tvTime: TextView
    private lateinit var tvLoadMsg: TextView
    private lateinit var pbLoading: ProgressBar
    internal lateinit var rlLoading: RelativeLayout
    private lateinit var tvPlayEnd: TextView
    private lateinit var rlPlayer: RelativeLayout
    private lateinit var rl_bottom: RelativeLayout
    private lateinit var vol_top: RelativeLayout
    internal var mVideoWidth = 0
    internal var mVideoHeight = 0

    private var loop = false
    private var isPortrait = true

    private var handler: Handler? = null

    private var menu_visible = true
    internal var isPlayFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        init()
        initIJKPlayer()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private fun init() {
        btnPlay = findViewById(R.id.btn_play)
        seekBar = findViewById(R.id.seekBar)
        btnSetting = findViewById(R.id.btn_setting)
        btnStop = findViewById(R.id.btn_stop)

        rl_bottom = findViewById(R.id.include_play_bottom)
        vol_top = findViewById(R.id.include_volumn_top)
        val ijkPlayerView = findViewById<VideoPlayerIJK>(R.id.ijkPlayer)

        tvTime = findViewById(R.id.tv_time)
        tvLoadMsg = findViewById(R.id.tv_load_msg)
        pbLoading = findViewById(R.id.pb_loading)
        rlLoading = findViewById(R.id.rl_loading)
        tvPlayEnd = findViewById(R.id.tv_play_end)
        rlPlayer = findViewById(R.id.rl_player)

        btnStop.setOnClickListener(this)
        ijkPlayerView.setOnClickListener(this)
        btnSetting.setOnClickListener(this)
        btnPlay.setOnClickListener(this)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                //进度改变
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //开始拖动
                handler!!.removeCallbacksAndMessages(null)

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //停止拖动
                ijkPlayer!!.seekTo(ijkPlayer!!.duration * seekBar.progress / 100)
                handler!!.sendEmptyMessageDelayed(MSG_REFRESH, 100)
            }
        })

        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_REFRESH -> if (ijkPlayer!!.isPlaying()) {
                        refresh()
                        handler!!.sendEmptyMessageDelayed(MSG_REFRESH, 50)
                    }
                }

            }
        }

        setVolumeSeekBar()
        volumeSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    setVolume(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    setVolume(seekBar.progress)
                }
            }


            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    setVolume(seekBar.progress)
                }
            }
        })

        checkLoop.setOnCheckedChangeListener { _, isChecked ->
            loop = isChecked
        }
    }

    private fun setVolumeSeekBar() {
        val mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.progress = (100 * (currentVolume.toDouble() / maxVolume.toDouble())).toInt()
    }

    private fun setVolume(progress: Int) {
        val mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ((progress.toDouble() / 100.0) * maxVolume.toDouble()).toInt(), 0)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                // do not intercept
                setVolumeSeekBar()
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                setVolumeSeekBar()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun refresh() {
        val current = ijkPlayer!!.getCurrentPosition() / 1000
        val duration = ijkPlayer!!.getDuration() / 1000
        val current_second = current % 60
        val current_minute = current / 60
        val total_second = duration % 60
        val total_minute = duration / 60
        val time = "$current_minute:$current_second/$total_minute:$total_second"
        tvTime.text = time
        if (duration != 0L) {
            seekBar.progress = (current * 100 / duration).toInt()
        }

    }

    private fun initIJKPlayer() {
        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null)
            IjkMediaPlayer.native_profileBegin("libijkplayer.so")
        } catch (e: Exception) {
            this.finish()
        }

        ijkPlayer = findViewById(R.id.ijkPlayer)
        ijkPlayer!!.setListener(VideoPlayerListener())
        //ijkPlayer.setVideoResource(R.raw.yuminhong);
        ijkPlayer!!.setVideoResource(R.raw.big_buck_bunny)

        /*ijkPlayer.setVideoResource(R.raw.big_buck_bunny);
        ijkPlayer.setVideoPath("https://media.w3.org/2010/05/sintel/trailer.mp4");
        ijkPlayer.setVideoPath("http://vjs.zencdn.net/v/oceans.mp4");*/

        ijkPlayer!!.setListener(object : VideoPlayerListener() {
            override fun onBufferingUpdate(mp: IMediaPlayer, percent: Int) {}

            override fun onCompletion(mp: IMediaPlayer) {
                if (loop) {
                    seekBar.progress = 0
                    ijkPlayer!!.stop()
                    ijkPlayer!!.setVideoResource(R.raw.big_buck_bunny)
                    ijkPlayer!!.start()
                } else {
                    seekBar.progress = 100
                    btnPlay.text = "Play"
                    btnStop.text = "Play"
                }
            }

            override fun onError(mp: IMediaPlayer, what: Int, extra: Int): Boolean {
                return false
            }

            override fun onInfo(mp: IMediaPlayer, what: Int, extra: Int): Boolean {
                return false
            }

            override fun onPrepared(mp: IMediaPlayer) {
                refresh()
                handler!!.sendEmptyMessageDelayed(MSG_REFRESH, 50)
                isPlayFinish = false
                mVideoWidth = mp.getVideoWidth()
                mVideoHeight = mp.getVideoHeight()
                videoScreenInit()
                //toggle();
                mp.start()
                rlLoading.visibility = View.GONE
            }

            override fun onSeekComplete(mp: IMediaPlayer) {}

            override fun onVideoSizeChanged(mp: IMediaPlayer, width: Int, height: Int, sar_num: Int, sar_den: Int) {
                mVideoWidth = mp.getVideoWidth()
                mVideoHeight = mp.getVideoHeight()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        handler!!.sendEmptyMessageDelayed(MSG_REFRESH, 1000)
    }

    override fun onStop() {
        super.onStop()
        if (ijkPlayer != null && ijkPlayer!!.isPlaying()) {
            ijkPlayer!!.stop()
        }
        IjkMediaPlayer.native_profileEnd()
        handler!!.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        if (ijkPlayer != null) {
            ijkPlayer!!.stop()
            ijkPlayer!!.release()
            ijkPlayer = null
        }

        super.onDestroy()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ijkPlayer -> if (menu_visible == false) {
                rl_bottom.visibility = View.VISIBLE
                vol_top.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.show_bottom)
                val animation2 = AnimationUtils.loadAnimation(applicationContext, R.anim.show_top)
                rl_bottom.startAnimation(animation)
                vol_top.startAnimation(animation2)
                menu_visible = true
            } else {
                rl_bottom.visibility = View.INVISIBLE
                vol_top.visibility = View.INVISIBLE
                val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.move_bottom)
                val animation2 = AnimationUtils.loadAnimation(applicationContext, R.anim.move_top)
                rl_bottom.startAnimation(animation)
                vol_top.startAnimation(animation2)
                menu_visible = false
            }
            R.id.btn_setting -> toggle()
            R.id.btn_play -> if (btnPlay.text.toString() == resources.getString(R.string.pause)) {
                ijkPlayer!!.pause()
                btnPlay.text = resources.getString(R.string.media_play)
            } else {
                ijkPlayer!!.start()
                btnPlay.text = resources.getString(R.string.pause)
            }
            R.id.btn_stop -> if (btnStop.text.toString() == resources.getString(R.string.stop)) {
                ijkPlayer!!.stop()
                /*ijkPlayer.mMediaPlayer.prepareAsync();
                    ijkPlayer.mMediaPlayer.seekTo(0);*/
                btnStop.text = resources.getString(R.string.media_play)
            } else {
                ijkPlayer!!.setVideoResource(R.raw.big_buck_bunny)
                btnStop.text = resources.getString(R.string.stop)
            }
        }
    }

    private fun videoScreenInit() {
        if (isPortrait) {
            portrait()
        } else {
            lanscape()
        }
    }

    private fun toggle() {
        if (!isPortrait) {
            portrait()
        } else {
            lanscape()
        }
    }

    private fun portrait() {
        ijkPlayer!!.pause()
        isPortrait = true
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val wm = this
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = wm.defaultDisplay.width.toFloat()
        val height = wm.defaultDisplay.height.toFloat()
        var ratio = width / height
        if (width < height) {
            ratio = height / width
        }

        val layoutParams = rlPlayer.layoutParams as RelativeLayout.LayoutParams
        layoutParams.height = (mVideoHeight * ratio).toInt()
        layoutParams.width = width.toInt()
        rlPlayer.layoutParams = layoutParams
        btnSetting.text = resources.getString(R.string.fullScreen)
        ijkPlayer!!.start()
    }

    private fun lanscape() {
        ijkPlayer!!.pause()
        isPortrait = false
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val wm = this
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = wm.defaultDisplay.width.toFloat()
        val height = wm.defaultDisplay.height.toFloat()
        val ratio = width / height

        val layoutParams = rlPlayer.layoutParams as RelativeLayout.LayoutParams

        layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
        layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
        rlPlayer.layoutParams = layoutParams
        btnSetting.text = resources.getString(R.string.smallScreen)
        ijkPlayer!!.start()
    }

    companion object {
        val MSG_REFRESH = 1001
    }
}

fun Context.videoActivityIntent(): Intent {
    return Intent(this, IJKPlayerActivity::class.java)
}