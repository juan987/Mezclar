package com.juan.mezclar;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Juan on 05/10/2017.
 * Esta clase tiene metodos que devuelven imagenes  de algun directorio del movil
 */

public class ObtenerImagen {
    String xxx = this.getClass().getSimpleName();
    Context context;
    public ObtenerImagen(Context context, String pathToImage){
        this.context = context;
    }

    public void getImagenMethod(){
        if(isExternalStorageWritable()) {
            File DCIMDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String directorio = DCIMDir.getAbsolutePath() ;
            Log.d(xxx, "El directorio es: " + directorio);
            Toast.makeText(context,
                    directorio, Toast.LENGTH_SHORT).show();
            if(loadImageFromExternalPublicStorage(directorio)){
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
