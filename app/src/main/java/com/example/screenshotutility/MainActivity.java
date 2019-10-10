package com.example.screenshotutility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.File;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    public static DisplayMetrics dm = new DisplayMetrics();
    public static MediaProjectionManager mProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String folder_main = "ScreenShots";

        File f = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        else
        {
            checkOverlayPermission();
        }


    }

    private void checkOverlayPermission()
    {
        if (!Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }
        else
            startService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                startService();
            }
        }
    }

    private void startService()
    {
        startService(new Intent(MainActivity.this, ChatHeadService.class));
        this.finishAndRemoveTask();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    checkOverlayPermission();
                    return;
                }
                else
                {
                    Toast.makeText(this, "Storage permission is needed to store the ScreenShots", Toast.LENGTH_LONG).show();
                    this.finishAndRemoveTask();
                }
                return;
            }
        }
    }

}


