package com.hotwav.demolive;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.IBinder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.graphics.PixelFormat;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.VideoView;
import android.net.Uri;
import android.os.Environment;
import java.io.File;

public class DemoService extends Service {

    private WindowManager manager;
    private VideoView video;
    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable showAgain = () -> showVideo();

    @Override
    public void onCreate() {
        super.onCreate();

        createNotification();

        manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        showVideo();
    }

    private void createNotification() {
        String channelId = "hotwav_demo";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                channelId,
                "HOTWAV Demo",
                NotificationManager.IMPORTANCE_LOW
            );

            getSystemService(NotificationManager.class)
                .createNotificationChannel(channel);
        }

        Notification notification =
            new Notification.Builder(this, channelId)
                .setContentTitle("HOTWAV DEMO LIVE")
                .setContentText("خدمة عرض الفيديو تعمل")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .build();

        startForeground(1, notification);
    }

    private void showVideo() {
        if (video != null) return;

        File folder = new File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            ), "HOTWAV"
        );

        File file = new File(folder, "demo.mp4");

        if (!file.exists()) return;

        video = new VideoView(this);
        video.setVideoURI(Uri.fromFile(file));

        video.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            video.start();
        });

        video.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideVideo();
                handler.removeCallbacks(showAgain);
                handler.postDelayed(showAgain, 30000);
            }
            return true;
        });

        WindowManager.LayoutParams params =
            new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            );

        params.gravity = Gravity.TOP | Gravity.START;

        manager.addView(video, params);
    }

    private void hideVideo() {
        if (video != null) {
            manager.removeView(video);
            video = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showAgain);
        hideVideo();
        super.onDestroy();
    }
}
