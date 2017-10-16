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


    //Este metodo escribe la fecha en la que se genero la imagen y se guardo en el dispositivo
    //La fecha se escribe en la ultima linea de CONFIG
    public boolean appendDateEnFichero(String pathToFileTxt){
        String data = "Hola soy juan";
        File directorioMain = new File(Environment.getExternalStorageDirectory() + pathToFileTxt);
        String directorio = directorioMain.getAbsolutePath();

        BufferedWriter bw = null;

        FileWriter fw = null;

        Log.d(xxx, "El directorio es: " + directorioMain);
        if(isExternalStorageWritable()) {



            try {
                //el flag de append hay que ponerlo a true, default es false en FileOutputStream

                FileOutputStream fileOutputStream = new FileOutputStream(directorioMain);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                writer.append(data);
                writer.close();
                fileOutputStream.close();




                //String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + mFileName;
                //fw = new FileWriter(directorioMain, true);

                //bw = new BufferedWriter(fw);

                //bw.write(data);

                //fw.write(data);
                //fw.close();
                //bw.close();

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
            finally {

                try {

                    if (bw != null)
                        bw.close();

                    if (fw != null)
                        fw.close();

                    return true;


                } catch (IOException e) {

                    //ex.printStackTrace();
                    Log.d(xxx, e.getMessage());
                    return false;

                }
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
