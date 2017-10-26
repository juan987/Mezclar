package com.juan.mezclar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Juan on 05/10/2017.
 * Esta clase tiene metodos que devuelven imagenes  de algun directorio del movil
 */

public class ObtenerImagen {
    public String xxx = this.getClass().getSimpleName();
    public Context context;

    public ObtenerImagen(Context context){
        this.context = context;
    }

    public Bitmap getImagenMethod(String pathToImage){
        Bitmap bitmap = null;
        if(isExternalStorageWritable()) {

            //File DCIMDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            //Obtenemos la raiz del directorio del movil
            //File DCIMDir = context.getExternalFilesDir(null);
            File DCIMDir = Environment.getExternalStorageDirectory();
            String directorio = DCIMDir.getAbsolutePath();
            directorio += pathToImage;
            Log.d(xxx, "El directorio es: " + directorio);
            //Toast.makeText(context,
                    //directorio, Toast.LENGTH_SHORT).show();
            bitmap = BitmapFactory.decodeFile(directorio);
            if(bitmap != null){
                //Toast.makeText(context,
                        //"Imagen cargada", Toast.LENGTH_SHORT).show();
                Log.d(xxx, "Imagen cargada");
            }else{
                //Toast.makeText(context,
                        //"ERROR Imagen NO cargada", Toast.LENGTH_SHORT).show();
                Log.d(xxx, "ERROR: Imagen NO cargada" );
            }


        }else{
            Log.d(xxx, "El external public storage no esta montado ");

        }
        return bitmap;
    }



    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public File getFilePathOfPicture(String environmentDir, String subDir, String imageName){
        //NOTA 1: environmentDir es string del tipo Environment.DIRECTORY_PICTURES o Environment.DIRECTORY_DCIM
        //Nota 2: subDir es un string con barras: /predict/
        //NOTA 3: imageName es el nombre de la imagen a guardar como predict.jpg
        if(isExternalStorageWritable()) {
            File picturesDir = Environment.getExternalStoragePublicDirectory(environmentDir);
            String directorio = picturesDir.getAbsolutePath() ;
            Log.d(xxx, "El directorio es: " + directorio);
            File file = new File(directorio + subDir + imageName);
            String path = file.getAbsolutePath();
            Log.d(xxx, "El path absoluto es: " +path);
            return file;
        }else{
            Log.d(xxx, "El external public storage no esta montado ");
            return null;

        }
    }

    public File getFilePathOfPictureParaCentrar(String subDir, String imageName){
        //Devuelve la ultima imagen asociada al ultimo digito de una cadena de digitos o de texto
        if(isExternalStorageWritable()) {

            File dir = new File(Environment.getExternalStorageDirectory() + subDir +imageName);

            String directorio = dir.getAbsolutePath();

            Log.d(xxx, "getFilePathOfPictureParaCentrar, El directorio es: " + directorio);
            return dir;
        }else{
            Log.d(xxx, "El external public storage no esta montado ");
            return null;

        }
    }

    public byte[] getFileBytes(File file){
        //este metodo NO lo utilizo, y tampoco lo he probado

        try {
            byte bytes[] = FileUtils.readFileToByteArray(file);
            Log.d(xxx, "getFileBytes, file size in bytes: " +bytes.length);
            return bytes;

        } catch (IOException e) {
            //e.printStackTrace();
            Log.d(xxx, "getFileBytes, error leyendo file en binario: " +e.getMessage());
            return null;

        }


    }

}
