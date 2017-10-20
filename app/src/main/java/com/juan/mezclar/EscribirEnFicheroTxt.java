package com.juan.mezclar;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juan on 08/10/2017.
 * Esta clase tiene metodos que devuelve daros de un fichero txt  de algun directorio del movil
 */

public class EscribirEnFicheroTxt {
    public String xxx = this.getClass().getSimpleName();
    public Context context;

    public EscribirEnFicheroTxt(Context context){
        this.context = context;
    }


    //Este metodo re-escribe la fecha en la que se genero la imagen y se guardo en el dispositivo
    //La fecha se escribe en la ultima linea de CONFIG
    public boolean appendDateEnFichero(String pathToFileTxt, String nuevoTexto){
        String data = "Hola soy juan";

        char[] charData = nuevoTexto.toCharArray();

        File directorioMain = new File(Environment.getExternalStorageDirectory() + pathToFileTxt);
        String directorio = directorioMain.getAbsolutePath();



        Log.d(xxx, "El directorio es: " + directorioMain);
        if(isExternalStorageWritable()) {

            try {
                //el flag de append hay que ponerlo a true, default es false en FileWriter

                /*
                FileOutputStream fileOutputStream = new FileOutputStream(directorioMain);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                writer.append(charData);
                writer.close();
                fileOutputStream.close();
                */

                directorioMain = new File(Environment.getExternalStorageDirectory() + pathToFileTxt);
                directorio = directorioMain.getAbsolutePath();

                FileWriter fw = new FileWriter(directorio, true);
                fw.write(charData);
                fw.close();


                return true;

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                Log.d(xxx, e.getMessage());
                return false;
            } catch (IOException e) {
                //e.printStackTrace();
                Log.d(xxx, e.getMessage());
                return false;
            }

        }else{
            Log.d(xxx, "El external public storage no esta montado ");
            return false;
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
