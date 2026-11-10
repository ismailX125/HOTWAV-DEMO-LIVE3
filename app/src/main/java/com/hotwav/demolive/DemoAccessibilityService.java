
package com.hotwav.demo;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.View;
import android.graphics.Color;
import android.widget.FrameLayout;
import android.os.Handler;
import android.os.Looper;

public class DemoAccessibilityService extends AccessibilityService {

    private WindowManager windowManager;
    private FrameLayout videoOverlay;
    private Handler handler = new Handler(Looper.getMainLooper());

    private boolean overlayVisible = false;

    private final Runnable showRunnable = () -> showOverlay();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        handler.postDelayed(showRunnable, 30000);
    }

    private void showOverlay() {
        if (overlayVisible || windowManager == null) return;

        videoOverlay = new FrameLayout(this);
        videoOverlay.setBackgroundColor(Color.BLACK);

        videoOverlay.setOnClickListener(v -> {
            removeOverlay();
            performGlobalAction(GLOBAL_ACTION_HOME);
            handler.removeCallbacks(showRunnable);
            handler.postDelayed(showRunnable, 30000);
        });

        WindowManager.LayoutParams params =
            new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.graphics.PixelFormat.OPAQUE
            );

        params.gravity = Gravity.TOP | Gravity.START;

        windowManager.addView(videoOverlay, params);
        overlayVisible = true;
    }

    private void removeOverlay() {
        if (videoOverlay != null && overlayVisible) {
            windowManager.removeView(videoOverlay);
            videoOverlay = null;
            overlayVisible = false;
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!overlayVisible && event != null) {
            int type = event.getEventType();

            if (type == AccessibilityEvent.TYPE_VIEW_CLICKED ||
                type == AccessibilityEvent.TYPE_VIEW_SCROLLED ||
                type == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {

                handler.removeCallbacks(showRunnable);
                handler.postDelayed(showRunnable, 30000);
            }
        }
    }

    @Override
    public void onInterrupt() {
        handler.removeCallbacks(showRunnable);
        removeOverlay();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showRunnable);
        removeOverlay();
        super.onDestroy();
    }
}
