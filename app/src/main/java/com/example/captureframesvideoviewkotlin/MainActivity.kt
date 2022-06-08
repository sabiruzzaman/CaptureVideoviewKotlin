package com.example.captureframesvideoviewkotlin

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    var btnCapture: Button? = null
    var myVideoView: VideoView? = null
    var videoSource = "https://sites.google.com/site/androidexample9/download/RunningClock.mp4"
    var uriVideoSource: Uri? = null
    var myMediaController: MediaController? = null
    var myMediaMetadataRetriever: MediaMetadataRetriever? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myVideoView = findViewById<View>(R.id.vview) as VideoView

        prepareVideo()

        btnCapture = findViewById<View>(R.id.capture) as Button
        btnCapture!!.setOnClickListener(btnCaptureOnClickListener)
    }

    private var btnCaptureOnClickListener = View.OnClickListener {
        val currentPosition = myVideoView!!.currentPosition //in millisecond
        Toast.makeText(
            this@MainActivity,
            "Current Position: $currentPosition (ms)",
            Toast.LENGTH_LONG
        ).show()
        val bmFrame: Bitmap?
        val pos = currentPosition * 1000 //unit in microsecond
        bmFrame = myMediaMetadataRetriever!!.getFrameAtTime(pos.toLong())
        if (bmFrame == null) {
            Toast.makeText(
                this@MainActivity,
                "bmFrame == null!",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val myCaptureDialog = AlertDialog.Builder(this@MainActivity)
            val capturedImageView = ImageView(this@MainActivity)
            capturedImageView.setImageBitmap(bmFrame)
            val capturedImageViewLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            capturedImageView.layoutParams = capturedImageViewLayoutParams
            myCaptureDialog.setView(capturedImageView)
            myCaptureDialog.show()
        }
    }

    private fun prepareVideo() {
        myMediaMetadataRetriever = MediaMetadataRetriever()
        myMediaMetadataRetriever!!.setDataSource(
            videoSource, HashMap()
        )
        myMediaController = MediaController(this@MainActivity)
        myVideoView!!.setMediaController(myMediaController)
        Toast.makeText(this@MainActivity, videoSource, Toast.LENGTH_LONG).show()
        uriVideoSource = Uri.parse(videoSource)
        myVideoView!!.setVideoURI(uriVideoSource)
        myVideoView!!.setOnCompletionListener(myVideoViewCompletionListener)
        myVideoView!!.setOnPreparedListener(MyVideoViewPreparedListener)
        myVideoView!!.setOnErrorListener(myVideoViewErrorListener)
        myVideoView!!.requestFocus()
        myVideoView!!.start()
    }

    private var myVideoViewCompletionListener = OnCompletionListener {
        Toast.makeText(
            this@MainActivity, "End of Video",
            Toast.LENGTH_LONG
        ).show()
    }

    private var MyVideoViewPreparedListener = OnPreparedListener {
        val duration = myVideoView!!.duration.toLong() //in millisecond
        Toast.makeText(
            this@MainActivity,
            "Duration: $duration (ms)",
            Toast.LENGTH_LONG
        ).show()
    }

    private var myVideoViewErrorListener =
        MediaPlayer.OnErrorListener { mp, what, extra ->
            var errWhat = ""
            errWhat = when (what) {
                MediaPlayer.MEDIA_ERROR_UNKNOWN -> "MEDIA_ERROR_UNKNOWN"
                MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "MEDIA_ERROR_SERVER_DIED"
                else -> "unknown what"
            }
            var errExtra = ""
            errExtra = when (extra) {
                MediaPlayer.MEDIA_ERROR_IO -> "MEDIA_ERROR_IO"
                MediaPlayer.MEDIA_ERROR_MALFORMED -> "MEDIA_ERROR_MALFORMED"
                MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "MEDIA_ERROR_UNSUPPORTED"
                MediaPlayer.MEDIA_ERROR_TIMED_OUT -> "MEDIA_ERROR_TIMED_OUT"
                else -> "...others"
            }
            Toast.makeText(
                this@MainActivity,
                """
                      Error!!!
                      what: $errWhat
                      extra: $errExtra
                      """.trimIndent(),
                Toast.LENGTH_LONG
            ).show()
            true
        }
}