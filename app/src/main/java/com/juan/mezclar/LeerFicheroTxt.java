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

            //File directorioMain = Environment.getExternalStorageDirectory() + pathToFileTxt;

            File directorioMain = new File(Environment.getExternalStorageDirectory() + pathToFileTxt );

            String directorio = directorioMain.getAbsolutePath();
            //directorio += pathToFileTxt;
            Log.d(xxx, "El directorio es: " + directorioMain);
            //Leer el fichero txt
            //BufferedReader br = new BufferedReader(new FileReader(directorioMain));
            BufferedReader br = null;

            try {
                br = new BufferedReader(new FileReader(directorioMain));
                //BufferedReader br = new BufferedReader(new FileReader(directorioMain));
                String line;
                int lineCount = 0;
                while ((line = br.readLine()) != null) {
                    arrayLineasTexto.add(line);
                    // process the line.
                    /* if(lineCount % 7 == 0) {
                        arrayLineasTexto.add(line);
                    } */
                    lineCount ++;
                }
                //br.close();
            }

            /*
            catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } */
            catch (IOException ioEx) {
                //ioEx.printStackTrace();
                Log.d(xxx, ioEx.getMessage());

            }
            finally {
                try {

                    if (br != null) {
                        br.close();
                    }
                } catch (IOException ex) {
                    //ex.printStackTrace();
                    Log.d(xxx, ex.getMessage());

                }
            }

        }else{
            Log.d(xxx, "El external public storage no esta montado ");

        }
        return arrayLineasTexto;
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
