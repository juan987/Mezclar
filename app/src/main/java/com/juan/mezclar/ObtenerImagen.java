package com.juan.mezclar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

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
            Toast.makeText(context,
                    directorio, Toast.LENGTH_SHORT).show();
            bitmap = BitmapFactory.decodeFile(directorio);
            if(bitmap != null){
                Toast.makeText(context,
                        "Imagen cargada", Toast.LENGTH_SHORT).show();
                Log.d(xxx, "Imagen cargada");
            }else{
                Toast.makeText(context,
                        "ERROR Imagen NO cargada", Toast.LENGTH_SHORT).show();
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
}
