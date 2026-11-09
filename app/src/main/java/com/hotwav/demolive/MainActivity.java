package com.hotwav.demolive;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

public class MainActivity extends Activity {

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

            Intent service = new Intent(this, DemoService.class);
            startService(service);
        });

        layout.addView(title);
        layout.addView(permission);
        layout.addView(start);

        setContentView(layout);
    }
}
