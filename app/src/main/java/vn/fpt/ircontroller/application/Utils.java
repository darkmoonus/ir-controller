package vn.fpt.ircontroller.application;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hunter on 11/20/2015.
 */
public class Utils {
    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();
        InputStream i;
        Bitmap bitmap = null;
        try {
            i = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
