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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);


        }

        main = findViewById(R.id.main);
        Button btn =findViewById(R.id.full);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.ll).setVisibility(View.INVISIBLE);
                Bitmap b = Screenshot.takeScreenShotOfRootView(main);
                Toast.makeText(MainActivity.this , "ScreenShot Taken" , Toast.LENGTH_SHORT).show();
                main.setBackgroundColor(Color.parseColor("#e5ffff"));
                storeScreenshot(b);
                findViewById(R.id.ll).setVisibility(View.VISIBLE);
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
