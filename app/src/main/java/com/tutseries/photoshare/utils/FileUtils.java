package com.tutseries.photoshare.utils;

import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by usama on 8/27/15.
 */
public class FileUtils {
    public static byte[] loadImage(Uri pathToImage, Context context) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream fileInputStream = null;
        try {
            fileInputStream = context.getContentResolver().openInputStream(pathToImage);

            byte[] buffer = new byte[1024];
            while ((fileInputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, buffer.length);

            return outputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
