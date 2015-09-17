package net.valentinc.uppa.planning;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

/**
 * Created by valentinc on 16/09/2015.
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
        int len = 0;
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

    public static ArrayList<String> GetListOfFileInFolder(File folder){
        if(!folder.exists())
            return new ArrayList<>();
        else{
            File[] listFile = folder.listFiles();
            int nbrElem = listFile.length;
            ArrayList<String> list = new ArrayList<>();
            for(int i = 0; i < nbrElem;i++){
                list.add(listFile[i].getName());
            }
            return list;
        }
    }

    public static File TakeElementFromCache(Context context, String nameOfFile,extension e) throws CacheException {
        Log.i("INFO","TakeElementFromCache " + nameOfFile+'.'+e);
        String path = context.getFilesDir().getAbsolutePath() + File.separator;
        String CurrentPath = path+nameOfFile+'.'+e;
        File file = new File(CurrentPath);

        if (file.exists()) {
            Log.i("INFO","File taken from cache :" + file.getName());
            return file;
        } else {
            throw new CacheException("File does not exist");
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
        //GetListOfFileInFolder(context,context.getCacheDir());
        File file = new File(CurrentPath);
        Log.i("INFO", "File exists : " + file.exists());
        return file.exists();
    }
    public static void ResetCache(Context context){
        Log.i("INFO", "Start Reset Cache ");
        Log.i("INFO", "Number of file " + context.getApplicationContext().getCacheDir().listFiles().length);
        for(File file : context.getApplicationContext().getCacheDir().listFiles()){
            Log.i("INFO", "Will delete : " + file.getName());
            file.delete();
            Log.i("INFO", "File deleted :" + file.getName());
        }
        Log.i("INFO", "End Reset Cache ");
    }

    public static boolean isEqual(InputStream i1, InputStream i2)
            throws IOException {

        ReadableByteChannel ch1 = Channels.newChannel(i1);
        ReadableByteChannel ch2 = Channels.newChannel(i2);

        ByteBuffer buf1 = ByteBuffer.allocateDirect(1024);
        ByteBuffer buf2 = ByteBuffer.allocateDirect(1024);

        try {
            while (true) {

                int n1 = ch1.read(buf1);
                int n2 = ch2.read(buf2);

                if (n1 == -1 || n2 == -1) return n1 == n2;

                buf1.flip();
                buf2.flip();

                for (int i = 0; i < Math.min(n1, n2); i++)
                    if (buf1.get() != buf2.get())
                        return false;

                buf1.compact();
                buf2.compact();
            }

        } finally {
            if (i1 != null) i1.close();
            if (i2 != null) i2.close();
        }
    }
}
