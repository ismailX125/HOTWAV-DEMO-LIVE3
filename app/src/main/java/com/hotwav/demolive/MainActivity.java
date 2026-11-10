
package com.hotwav.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.VideoView;
import android.net.Uri;
import android.provider.MediaStore;
import android.content.ContentUris;
import android.database.Cursor;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

public class MainActivity extends Activity {

    private VideoView videoView;
    private FrameLayout screen;
    private Handler handler = new Handler();

    private static final long IDLE_TIME = 30000;

    private final Runnable showVideo = () -> playVideo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        screen = new FrameLayout(this);
        videoView = new VideoView(this);

        screen.addView(videoView);

        setContentView(screen);
        hideSystemUI();

        screen.setOnClickListener(v -> {
            handler.removeCallbacks(showVideo);
            videoView.stopPlayback();
            videoView.setVisibility(View.GONE);
            handler.postDelayed(showVideo, IDLE_TIME);
        });

        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO)
            != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                new String[]{Manifest.permission.READ_MEDIA_VIDEO}, 100
            );
        } else {
            playVideo();
        }
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);

        if (requestCode == 100 && results.length > 0 &&
            results[0] == PackageManager.PERMISSION_GRANTED) {
            playVideo();
        }
    }

    private Uri findVideo() {
        String selection =
            MediaStore.Video.Media.DISPLAY_NAME + "=?";

        String[] args = {"demo.mp4"};

        try (Cursor cursor = getContentResolver().query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            new String[]{MediaStore.Video.Media._ID},
            selection,
            args,
            null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(0);
                return ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                );
            }
        }

        return null;
    }

    private void playVideo() {
        Uri uri = findVideo();

        if (uri == null) return;

        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
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
