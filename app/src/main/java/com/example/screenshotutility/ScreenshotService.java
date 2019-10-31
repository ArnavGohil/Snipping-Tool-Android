package com.example.screenshotutility;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class ScreenshotService extends Service {

    private WindowManager windowManager , constr;
    WindowManager.LayoutParams params , ConParams ;
    LayoutInflater li;
    View myView , cons ;
    File file;

    static final String EXTRA_RESULT_CODE = "resultCode";
    static final String EXTRA_RESULT_INTENT = "resultIntent";
    static final String ACTION_RECORD = BuildConfig.APPLICATION_ID + ".RECORD";
    static final String ACTION_SHUTDOWN = BuildConfig.APPLICATION_ID + ".SHUTDOWN";
    static final int VIRT_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private MediaProjection projection;
    private VirtualDisplay vdisplay;
    final private HandlerThread handlerThread = new HandlerThread(getClass().getSimpleName(), android.os.Process.THREAD_PRIORITY_BACKGROUND);
    private Handler handler;
    private MediaProjectionManager mgr;
    private WindowManager wmgr;
    private ImageTransmogrifier it;
    private int resultCode;
    private Intent resultData;
    boolean flag = true ;
    /*
    * TRUE - Full Screen
    * FALSE - CLIP SCREEN
    */

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        constr = (WindowManager) getSystemService(WINDOW_SERVICE);

        li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        myView = li.inflate(R.layout.linear_disp, null);
        cons = li.inflate(R.layout.linear_clip_disp , null ) ;

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM ;
        params.y = 300 ;

        ConParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        ConParams.gravity = Gravity.CENTER ;

        constr.addView(cons , ConParams );
        windowManager.addView(myView, params);

        mgr = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        wmgr = (WindowManager) getSystemService(WINDOW_SERVICE);

        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCapture();
        if (myView != null) {
            windowManager.removeView(myView);
            constr.removeView(cons);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }




    public void FullC(View view) {
        myView.setVisibility(View.INVISIBLE);
        cons.setVisibility(View.INVISIBLE );
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startCapture();
                Toast.makeText(getApplicationContext(), "Screenshot Captured", Toast.LENGTH_SHORT).show();
                stopService(new Intent(getApplicationContext(), ScreenshotService.class));
            }
        }, 100);

    }

    public void ClipC(View view) {
        int height = cons.getHeight();
        int width = cons.getWidth() ;
        int startX = (int) cons.getX();
        int startY = (int) cons.getY();
        Toast.makeText(this, getStatusBarHeight() + "" , Toast.LENGTH_SHORT).show();
        //FIXME
        // flag = false ;
       /* myView.setVisibility(View.INVISIBLE);
        cons.setVisibility(View.INVISIBLE );
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startCapture();
                Toast.makeText(getApplicationContext(), "Screenshot Captured", Toast.LENGTH_SHORT).show();
                stopService(new Intent(getApplicationContext(), ScreenshotService.class));
            }
        }, 100);
*/
    }

    public void CloseC(View view) {
        stopService(new Intent(getApplicationContext(), ScreenshotService.class));
    }

    public void lt(final View view)
    {
        Toast.makeText(getApplicationContext() , "Slide to resize" , Toast.LENGTH_SHORT).show();
        view.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = (int) view.getX();
                        initialY = (int) view.getY();
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        ConParams.width =  initialX
                                + (int) (event.getRawX() - initialTouchX);
                        ConParams.height =  initialY
                                + (int) (event.getRawY() - initialTouchY);
                        if ( ConParams.height >= 0 && ConParams.width >= 0)
                            constr.updateViewLayout(cons , ConParams);
                        return true;
                }
                return false;
            }
        });
    }

    public void MoveC(View view)
    {
        Toast.makeText(getApplicationContext() , "Movement Unlocked" , Toast.LENGTH_SHORT).show();
        view.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = ConParams.x;
                        initialY = ConParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        ConParams.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        ConParams.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        constr.updateViewLayout(cons , ConParams);
                        return true;
                }
                return false;
            }
        });
    }



    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        if (i.getAction() == null) {
            resultCode = i.getIntExtra(EXTRA_RESULT_CODE, 1337);
            resultData = i.getParcelableExtra(EXTRA_RESULT_INTENT);
        } else if (ACTION_RECORD.equals(i.getAction())) {
            if (resultData != null) {
                startCapture();
            } else {
                Intent ui =
                        new Intent(this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(ui);
            }
        } else if (ACTION_SHUTDOWN.equals(i.getAction())) {
            stopForeground(true);
            stopSelf();
        }

        return (START_NOT_STICKY);
    }

    WindowManager getWindowManager() {
        return (wmgr);
    }

    Handler getHandler() {
        return (handler);
    }

    private void stopCapture() {
        if (projection != null) {
            projection.stop();
            vdisplay.release();
            projection = null;
        }

    }

    private void startCapture() {
        projection = mgr.getMediaProjection(resultCode, resultData);
        it = new ImageTransmogrifier(this);

        MediaProjection.Callback cb = new MediaProjection.Callback() {
            @Override
            public void onStop() {
                vdisplay.release();
            }
        };

        if (flag)
            vdisplay = projection.createVirtualDisplay("andshooter",
                it.getWidth(), it.getHeight(),
                getResources().getDisplayMetrics().densityDpi,
                VIRT_DISPLAY_FLAGS, it.getSurface(), null, handler);

        projection.registerCallback(cb, handler);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void storeScreenshot(Bitmap bitmap) {
        stopCapture();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy '-' HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        String filename = "ScreenShot - " + currentDateandTime;
        file = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ScreenShots",
                filename + ".jpg");
        OutputStream out = null;

//        FIXME
//        if (!flag)
                bitmap = Bitmap.createBitmap(bitmap ,(int) cons.getLeft() ,(int) cons.getTop() + getStatusBarHeight() , 600 , 600 ) ;

        try {
            out = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
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
        Log.e("STORAGE", file.toString());
        galleryAddPic();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
