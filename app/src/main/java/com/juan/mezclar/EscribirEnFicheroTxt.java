package com.juan.mezclar;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
                Log.d(xxx, "El directorio es: " + directorioMain);


                //Por defecto se sobreescribe el log
                boolean sobreescribirLog = true;

                //Chequear el tamaño del fichero txt.log
                long filesize = directorioMain.length();
                Log.d(xxx, "Tamaño de log.txt en bytes es: " + filesize);
                long filesizeInKB = filesize / 1024;
                Log.d(xxx, "Tamaño de log.txt en KB es: " + filesizeInKB);
                long filesizeinMB=filesizeInKB/1024;
                Log.d(xxx, "Tamaño de log.txt en MB es: " + filesizeinMB);

                //Prueba, cada linea nueva son menos de 100 bytes, unos 50 bytes de media
                //if(filesize >= 2330) sobreescribirLog = false;
                //Cuando el tamaño supere 1MB, el log.txt empieza de nuevo.
                if(filesizeinMB >= 1) sobreescribirLog = false;
                //Si sobreescribirLog es false, el log empieza de nuevo
                Log.d(xxx, "boolean sobreescribirLog es: " + sobreescribirLog);
                //FIN de Chequear el tamaño del fichero txt.log

                directorio = directorioMain.getAbsolutePath();

                //Cuando sobreescribirLog es true (valor por defecto), significa append.
                FileWriter fw = new FileWriter(directorio, sobreescribirLog);
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


    //3 nov 2017 nuevo req recibido en mail Plan viernes - corregido
    //Si al ejecutarse la App por primera vez no existe el folder
    // CesaralMagic/ImageC, entonces la App lo creará y copiará en este directorio
    // los ficheros de la configuración por defecto
    //Este metodo no se usa
    public boolean copiarFicheroConfigTxtPorDefecto(String pathCesaralMagicImageC){

        //Leer el CONFIG.txt por defecto de la carpeta raw y reconstruirlo en string each line
        InputStream inputStream = context.getResources().openRawResource(R.raw.config);
        BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));

        String textoConfigTxt = "";

        try {
            String eachline;
            //int lineCount = 0;
            //while ((eachline = bufferedReader.readLine()) != null) {
            while ((eachline = bufferedReader.readLine()) != null) {
                textoConfigTxt += eachline +"\r\n";
            }
            bufferedReader.close();
            Log.d(xxx, "copiarFicheroConfigTxtPorDefecto, El CONFIG.txt por defecto tiene: " +textoConfigTxt);
        } catch (IOException e) {
            //e.printStackTrace();
            Log.d(xxx, "copiarFicheroConfigTxtPorDefecto, io exception con el config por defecto: " +e.getMessage());
            return false;
        }

        //creamos el fichero config.txt con el string
        if(appendDateEnFichero(pathCesaralMagicImageC, textoConfigTxt)){
            Log.d(xxx, "escribirDatosEnLog fichero escrito correctamente");
            return true;
        }else{
            Log.d(xxx, "escribirDatosEnLog fichero NO escrito correctamente");
            return true;
        }

    }
}
