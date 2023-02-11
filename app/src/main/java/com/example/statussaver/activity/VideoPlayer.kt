package com.example.statussaver.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.statussaver.R
import com.example.statussaver.util.Method
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player.DefaultEventListener
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class VideoPlayer: AppCompatActivity() {

    lateinit var method: Method
    lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        method = Method(this@VideoPlayer)

        val i = intent
        val videoLink = i.getStringExtra("link").toString()

        val playerView: PlayerView = findViewById(R.id.player_view)
        val progressBar: ProgressBar = findViewById(R.id.progressBar_video_play)
        progressBar.visibility = View.VISIBLE

        val trackSelector = DefaultTrackSelector()
        player = ExoPlayerFactory.newSimpleInstance(this@VideoPlayer, trackSelector)
        playerView.player = player


        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this@VideoPlayer,
            Util.getUserAgent(this@VideoPlayer, resources.getString(R.string.app_name))
        )
        // This is the MediaSource representing the media to be played.
        // This is the MediaSource representing the media to be played.
        val videoSource: MediaSource =
            ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoLink))

        // Prepare the player with the source.
        // Prepare the player with the source.
        player.prepare(videoSource)
        player.playWhenReady = true
        player.addListener(object : DefaultEventListener() {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady) {
                    progressBar.visibility = View.GONE
                }
                super.onPlayerStateChanged(playWhenReady, playbackState)
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                Log.d("show_error", error.toString())
                super.onPlayerError(error)
            }
        })
    }


    override fun onBackPressed() {
        player.playWhenReady = false
        player.stop()
        player.release()
        super.onBackPressed()
    }

    override fun onPause() {
        player.playWhenReady = false
        super.onPause()
    }

    override fun onDestroy() {
        player.playWhenReady = false
        player.stop()
        player.release()
        super.onDestroy()
    }
}