package com.example.screenshotutility;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class ChatHeadService extends Service
{
    private WindowManager windowManager;
    WindowManager.LayoutParams params;
    LayoutInflater li ;
    View myView;
    File file ;

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
        Toast.makeText(getApplicationContext() , MainActivity.dm.heightPixels + " x " + MainActivity.dm.widthPixels , Toast.LENGTH_SHORT ).show() ;
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

    private void storeScreenshot(Bitmap bitmap)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy '-' HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        String filename = "ScreenShot - " + currentDateandTime ;
        file = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ScreenShots",
                filename + ".jpg");
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (out != null) {
                    out.close();
                }

            } catch (Exception exc) {
            }

        }
        Log.e("STORAGE",file.toString());
        galleryAddPic();
    }

    private void galleryAddPic()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
