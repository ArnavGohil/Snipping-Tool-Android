package com.example.screenshotutility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {


    private View main;
    private ImageView imageView;
    private static File file=null;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }
        if ( !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }

        startService(new Intent(MainActivity.this , ChatHeadService.class));
        this.finishAndRemoveTask();

        main = findViewById(R.id.main);
        Button btnFull = findViewById(R.id.full);
        Button btnClip =findViewById(R.id.clip);

        btnFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this , ChatHeadService.class));


                findViewById(R.id.ll).setVisibility(View.INVISIBLE);
                Bitmap b = Screenshot.takeScreenShotOfRootView(main);
                Toast.makeText(MainActivity.this , "ScreenShot Taken" , Toast.LENGTH_SHORT).show();
                storeScreenshot(b);
                main.setBackgroundColor(Color.parseColor("#e5ffff"));
                findViewById(R.id.ll).setVisibility(View.VISIBLE);
            }
        });

        btnClip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, ChatHeadService.class));
            }

        });



    }

    private void storeScreenshot(Bitmap bitmap)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy '-' HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        String filename = "ScreenShot - " + currentDateandTime ;
        file = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    Toast.makeText(this , "Storage permission is needed to store the ScreenShots" , Toast.LENGTH_LONG).show();
                    this.finishAndRemoveTask();
                }
                return;
            }
        }
    }

}


