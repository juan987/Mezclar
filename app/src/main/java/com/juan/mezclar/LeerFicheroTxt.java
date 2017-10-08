package com.juan.mezclar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juan on 08/10/2017.
 * Esta clase tiene metodos que devuelve daros de un fichero txt  de algun directorio del movil
 */

public class LeerFicheroTxt {
    public String xxx = this.getClass().getSimpleName();
    public Context context;

    public LeerFicheroTxt(Context context){
        this.context = context;
    }

    public List<String> getFileContentsLineByLineMethod(String pathToFileTxt){
        ArrayList<String> arrayLineasTexto = new ArrayList<>();
        if(isExternalStorageWritable()) {

            File directorioMain = new File(Environment.getExternalStorageDirectory() + pathToFileTxt);
            String directorio = directorioMain.getAbsolutePath();
            Log.d(xxx, "El directorio es: " + directorioMain);
            try {
                //Como en https://alvinalexander.com/blog/post/java/how-open-read-file-java-string-array-list
                BufferedReader br = new BufferedReader(new FileReader(directorioMain));
                String line;
                int lineCount = 0;
                while ((line = br.readLine()) != null) {
                    arrayLineasTexto.add(line);
                    // process the line 7  o multiplos, creo.
                    /* if(lineCount % 7 == 0) {
                        arrayLineasTexto.add(line);
                    } */
                    lineCount ++;
                }
                br.close();
                return arrayLineasTexto;
            }
            catch (Exception e) {
                Log.d(xxx, e.getMessage());
                return null;
            }

        }else{
            Log.d(xxx, "El external public storage no esta montado ");
            return null;
        }
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
