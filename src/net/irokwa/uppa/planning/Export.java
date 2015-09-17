package net.irokwa.uppa.planning;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by valentinc on 17/09/2015.
 */
public abstract class Export {

    public static int exportCurrentView(Context context, String imageName){
        //copy file from cache to ... For the moment only Documents Folder
        //nom fichier ((Periode) spPeriodes.getSelectedItem()).getImageCode() + "." + extension.png
        File file = new File(context.getCacheDir().getAbsolutePath() + File.separator + imageName);
        if(!file.exists()){
            Toast.makeText(null,"Le fichier n'existe pas. Impossible d'exporter.",Toast.LENGTH_LONG).show();
            return -1;
        }
        try {
            MemoryFunctions.copy(file, new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + File.separator + imageName));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(null,"Erreur durant l'exportation.",Toast.LENGTH_LONG).show();
        }
        Toast.makeText(null,"Sauvegardé dans \"Mes Documents\"",Toast.LENGTH_LONG).show();
        return 0;
    }
}
