package io.github.buniaowanfeng.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.AppMessage;

/**
 * Created by caofeng on 16-7-24.
 */
public class ImageUtil {


    private static final String TAG = "saveicon";

    public static  void saveIcon(AppMessage appInfo) {
        Drawable drawable =appInfo.appIcon;

        Bitmap bitmap = null;

        if(drawable instanceof BitmapDrawable){
            bitmap = (((BitmapDrawable) drawable)).getBitmap();
        }else if (drawable instanceof VectorDrawable){
            bitmap = getBitMap((VectorDrawable)drawable);
        }

        File saveDir = new File(YiTian.mIconPath);
        LogUtil.d("path",saveDir.getPath());
        if (!saveDir.exists()){
            saveDir.mkdir();
        }

        String fileName = appInfo.packageName+".png";
        File file = new File(saveDir,fileName);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitMap(VectorDrawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void saveIcon(List<AppMessage> savedApps) {
        for (AppMessage appMessage : savedApps){
            saveIcon(appMessage);
        }
    }

    public static Uri saveShare(Bitmap bigBitmap, String name){
        File saveDir = new File(YiTian.mContext.getExternalFilesDir(null),"todo");
        if (!saveDir.exists()){
            saveDir.mkdir();
        }
        String fileName =name+".png";
        File file = new File(saveDir,fileName);
        Uri uri = Uri.fromFile(file);
        LogUtil.d(TAG,"path " + file.getPath());

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bigBitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            Toast.makeText(YiTian.mContext,"error",Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }

        return uri;
    }
}
