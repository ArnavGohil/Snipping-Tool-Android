package com.example.screenshotutility;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class ChatHeadService extends Service
{
    private WindowManager windowManager;
    WindowManager.LayoutParams params;
    LayoutInflater li ;
    View myView;

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        myView = li.inflate(R.layout.linear_disp, null);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM ;
        params.x = 0;
        params.y = 100;

        windowManager.addView(myView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myView != null)
            windowManager.removeView(myView);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public void FullC(View view)
    {
        Toast.makeText(getApplicationContext() , "Pressed Button 1" , Toast.LENGTH_SHORT).show();
    }

    public void ClipC(View view)
    {
        Toast.makeText(getApplicationContext() , "Pressed Button 2" , Toast.LENGTH_SHORT).show();
    }

    public void CloseC(View view)
    {
        stopService(new Intent(getApplicationContext(), ChatHeadService.class));
    }

   /* public void MoveC(View view)
    {
        Toast.makeText(getApplicationContext() , "Pressed Button for Movement" , Toast.LENGTH_SHORT).show();
        view.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(myView, params);
                        return true;
                }
                return false;
            }
        });
    }*/



}
