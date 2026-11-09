package com.hotwav.demolive;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

public class MainActivity extends Activity {

    private static final int VIDEO_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 50, 35, 35);
        layout.setBackgroundColor(Color.BLACK);

        TextView title = new TextView(this);
        title.setText("HOTWAV DEMO LIVE");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);

        Button permission = new Button(this);
        permission.setText("تفعيل الظهور فوق التطبيقات");

        permission.setOnClickListener(v -> {
            Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
            );
            startActivity(intent);
        });

        Button start = new Button(this);
        start.setText("تشغيل العرض");

        start.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                permission.performClick();
                return;
            }

            if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO)
                != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                    new String[]{Manifest.permission.READ_MEDIA_VIDEO},
                    VIDEO_PERMISSION
                );
                return;
            }

            startDemoService();
        });

        layout.addView(title);
        layout.addView(permission);
        layout.addView(start);

        setContentView(layout);
    }

    private void startDemoService() {
        Intent service = new Intent(this, DemoService.class);

        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(service);
        } else {
            startService(service);
        }
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        String[] permissions,
        int[] grantResults
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        );

        if (requestCode == VIDEO_PERMISSION &&
            grantResults.length > 0 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startDemoService();
        }
    }
}
