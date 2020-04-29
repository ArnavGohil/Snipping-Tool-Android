package com.arnavg.screenshotutility;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.Image;
import android.media.ImageReader;
import android.view.Display;
import android.view.Surface;

import java.nio.ByteBuffer;

public class ImageTransmogrifier implements ImageReader.OnImageAvailableListener {
    private final int width;
    private final int height;
    private final ImageReader imageReader;
    private final ScreenshotService svc;
    private Bitmap latestBitmap = null;

    ImageTransmogrifier(ScreenshotService svc) {
        this.svc = svc;

        Display display = svc.getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getRealSize(size);

        //For Normal Mobile .
        int width = size.x;
        int height = size.y;

        if (MainActivity.DeX) {
            //For Dex Mode .
            width = 1920;
            height = 1080;
        }

        this.width = width;
        this.height = height;

        imageReader = ImageReader.newInstance(width, height,
                PixelFormat.RGBA_8888, 2);
        imageReader.setOnImageAvailableListener(this, svc.getHandler());
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        final Image image = imageReader.acquireLatestImage();

        if (image != null) {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            int bitmapWidth = width + rowPadding / pixelStride;

            if (latestBitmap == null || latestBitmap.getWidth() != bitmapWidth || latestBitmap.getHeight() != height) {
                if (latestBitmap != null) {
                    latestBitmap.recycle();
                }

                latestBitmap = Bitmap.createBitmap(bitmapWidth,
                        height, Bitmap.Config.ARGB_8888);
            }

            latestBitmap.copyPixelsFromBuffer(buffer);

            if (image != null) {
                image.close();
            }

            Bitmap cropped = Bitmap.createBitmap(latestBitmap, 0, 0,
                    width, height);

            svc.storeScreenshot(cropped);
        }
    }

    Surface getSurface() {
        return (imageReader.getSurface());
    }

    int getWidth() {
        return (width);
    }

    int getHeight() {
        return (height);
    }

    void close() {
        imageReader.close();
    }
}
