package net.valentinc.uppa.hyperplanning;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by valentinc on 16/09/2015.
 * Allow functionnalities to use Cache
 */
public abstract class Cache {
    // abstract class to be use without instanciation
    public enum extension{
        xml,
        png
    }

    public static void AddInputStreamToCache(Context context, InputStream input,String nameOfFile) throws IOException{
        Log.i("INFO", "AddInputStreamToCache started");
        String path = context.getCacheDir().getAbsolutePath()+File.separator+nameOfFile;
        FileOutputStream output = new FileOutputStream(path);
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = input.read(buffer)) != -1) {
            output.write(buffer, 0, len);
        }
        Log.i("INFO", "AddInputStreamToCache ended");
    }

    public static InputStream GetInputStreamFromCache(Context context, String nameOfFile, extension e){
        File initialFile = new File(context.getCacheDir().getAbsolutePath()+File.separator+nameOfFile + '.' +e);
        if(!DoesFileExist(context,nameOfFile,e)){
            Log.e("ERROR","File does not exist : " + nameOfFile+'.'+e);
            return null;
        }
        try {
            return new FileInputStream(initialFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Log.e("ERROR", "Error convert inputStream : " + ex.getMessage() + " - file : " + nameOfFile + '.' + e);
            return null;
        }
    }

    public static void updateWebView(Context context, WebView web, String nameOfFile,extension e) throws CacheException {
        Log.i("INFO", "Update webview with " + nameOfFile+'.'+e);
        String path = context.getCacheDir().getAbsolutePath()+File.separator+nameOfFile+'.'+e;
        Log.i("INFO", "Path : "+path);
        web.loadUrl("file://" + path);
        Log.i("INFO", "Updated");
    }

    public static boolean DoesFileExist(Context context, String nameOfFile,extension e){
        String path = context.getCacheDir().getAbsolutePath() + File.separator;
        String CurrentPath = path+nameOfFile+'.'+e;
        File file = new File(CurrentPath);
        Log.i("INFO", "File exists : " + file.exists());
        return file.exists();
    }
}
