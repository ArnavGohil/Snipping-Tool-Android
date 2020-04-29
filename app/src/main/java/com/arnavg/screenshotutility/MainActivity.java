package com.arnavg.screenshotutility;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private static final int REQUEST_SCREENSHOT = 59706;
    static boolean DeX = false;
    private MediaProjectionManager mgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String folder_main = "Snip ScreenShots";


        File f = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        Configuration config = getResources().getConfiguration();
        try {
            Class configClass = config.getClass();
            if (configClass.getField("SEM_DESKTOP_MODE_ENABLED").getInt(configClass) ==
                    configClass.getField("semDesktopModeEnabled").getInt(config)) {
                DeX = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mgr = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            checkOverlayPermission();
        }


    }

    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else
            startService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                startService();
            } else {
                Toast.makeText(this, "Overlay permission is needed to take ScreenShots", Toast.LENGTH_LONG).show();
                this.finishAndRemoveTask();
            }
        }
        if (requestCode == REQUEST_SCREENSHOT) {
            if (resultCode == RESULT_OK) {
                Intent i =
                        new Intent(this, ScreenshotService.class)
                                .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                                .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data);

                this.finishAndRemoveTask();
                startService(i);
            } else {
                Toast.makeText(this, "Permission is needed to take ScreenShots", Toast.LENGTH_LONG).show();
                this.finishAndRemoveTask();
            }
        }

    }

    private void startService() {
        startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_SCREENSHOT);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkOverlayPermission();
                    return;
                } else {
                    Toast.makeText(this, "Storage permission is needed to store the ScreenShots", Toast.LENGTH_LONG).show();
                    this.finishAndRemoveTask();
                }
                return;
            }
        }
    }

}


