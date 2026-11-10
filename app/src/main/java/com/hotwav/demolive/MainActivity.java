
package com.hotwav.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.VideoView;
import android.net.Uri;
import android.os.Environment;
import java.io.File;

public class MainActivity extends Activity {

    private VideoView videoView;
    private FrameLayout screen;
    private Handler handler = new Handler();

    private static final long IDLE_TIME = 30000;

    private final Runnable showVideo = new Runnable() {
        @Override
        public void run() {
            playVideo();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        screen = new FrameLayout(this);

        videoView = new VideoView(this);

        screen.addView(videoView,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        );

        setContentView(screen);

        hideSystemUI();
        playVideo();

        screen.setOnClickListener(v -> {
            handler.removeCallbacks(showVideo);
            videoView.setVisibility(View.GONE);
            handler.postDelayed(showVideo, IDLE_TIME);
        });
    }

    private void playVideo() {
        File file = new File(
            Environment.getExternalStorageDirectory(),
            "Movies/HOTWAV/demo.mp4"
        );

        if (!file.exists()) return;

        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.fromFile(file));

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(showVideo);
        super.onDestroy();
    }
}
