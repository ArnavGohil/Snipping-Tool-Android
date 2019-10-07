package com.example.screenshotutility;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class ChatHeadService extends Service
{
    private WindowManager windowManager;
    private ImageView chatHead;
    WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.icon);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM ;
        params.x = 0;
        params.y = 100;

        //this code is for dragging the chat head
        chatHead.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), ChatHeadService.class));
                //TODO
            }
        });
        windowManager.addView(chatHead, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null)
            windowManager.removeView(chatHead);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
