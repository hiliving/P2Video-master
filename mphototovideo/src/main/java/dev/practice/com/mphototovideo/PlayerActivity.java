package dev.practice.com.mphototovideo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;

import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL;

/**
 * Created by HY on 2017/6/22.
 */

public class PlayerActivity extends AppCompatActivity implements ExoPlayer.EventListener {
    private Uri mUri;
    private SimpleExoPlayerView playerView;
    private DefaultTrackSelector trackSelector;
    private DefaultLoadControl loadControl;
    private SimpleExoPlayer mPlayer;
    private MediaSource videoSource;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        startPlayer();
    }
    /**
     * 播放启动视频
     */
    private void startPlayer() {
        // 0.  set player view
        playerView = (SimpleExoPlayerView) findViewById(R.id.playerView);
        playerView.setUseController(false);
        playerView.getKeepScreenOn();
        playerView.setResizeMode(RESIZE_MODE_FILL);

        // 1. Create a default TrackSelector
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        loadControl = new DefaultLoadControl();

        // 3. Create the mPlayer
        mPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        mPlayer.addListener(this);

        // 4. set player
        playerView.setPlayer(mPlayer);
        mPlayer.setPlayWhenReady(true);

        // 5. prepare to play
        File file = new File(Constants.FILE_VIDEO_FLODER, "jcode.mp4");
        if (file.isFile() && file.exists()) {
            mUri = Uri.fromFile(file);
        } else {
            Toast.makeText(this,"文件未找到",Toast.LENGTH_SHORT).show();
            return;
        }
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "UserAgent");
        videoSource = new ExtractorMediaSource(mUri, dataSourceFactory, extractorsFactory, null, null);

        // 6. ready to play
        mPlayer.prepare(videoSource);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {

            case ExoPlayer.STATE_ENDED:
                //Log.d(TAG, "player state -> STATE_ENDED");
                mPlayer.stop();
                break;

            case ExoPlayer.STATE_IDLE:
                //Log.d(TAG, "player state -> STATE_IDLE");
                break;

            case ExoPlayer.STATE_READY:
                //Log.d(TAG, "player state -> STATE_READY");
                break;

            case ExoPlayer.STATE_BUFFERING:
               // Log.d(TAG, "player state -> STATE_BUFFERING");
                break;
            default:

                break;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
}
