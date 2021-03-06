package net.valentinc.uppa.hyperplanning;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by valentinc on 17/09/2015.
 * Allow functionnalities to export things
 */

public abstract class Export {

    public static int exportCurrentView(Context context, String imageName){
        /* copy file from cache to ... For the moment only Documents Folder */
        File file = new File(context.getCacheDir().getAbsolutePath() + File.separator + imageName);
        if(!file.exists()){
            return -1;
        }
        try {
            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
            boolean isPresent = true;
            if (!docsFolder.exists()) {
                isPresent = docsFolder.mkdir();
            }
            if (isPresent) {
                MemoryFunctions.copy(file, new File(Environment.getExternalStorageDirectory() + "/Documents/"+imageName));
            } else {
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return 0;
    }
}
