package com.sunway.averychoke.studywifidirect3.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by AveryChoke on 13/4/2017.
 */

public class FileUtil {
    public static String getFileName(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { OpenableColumns.DISPLAY_NAME };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            File file = new File(uri.getPath());
            return file.getName();
        }

        return null;
    }

    public static void copyFile(Context context, Uri srcUri, File destFile) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(srcUri, "r");
            inChannel = new FileInputStream(parcelFileDescriptor.getFileDescriptor()).getChannel();
            outChannel = new FileOutputStream(destFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (parcelFileDescriptor != null) {
                parcelFileDescriptor.close();
            }
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
}
