package com.juan.mezclar;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Juan on 04/10/2017.
 */

/*

Comentarios importantes

If you want to use a image file from your drawable folder you can get it with this line of code:
Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.my_image);

If you want to save a thumbnail of an image you can easily change the size of the image with this line of code:
Bitmap thumbnail = Bitmap.createScaledBitmap(originalBitmap, width, height, false);


 */


public class GuardarImagenFinal {
    String xxx = this.getClass().getSimpleName();
    Context context;
    Bitmap bitmap;

    public GuardarImagenFinal(Context context, Bitmap bitmap){
        this.context = context;
        this.bitmap = bitmap;
    }

    // los path para guardar la imagen generada son:
    //   /pictures/predict y /DCIM/predict
    public boolean guardarImagenMethod(String environmentDir, String subDir, String imageName){
        //NOTA 1: environmentDir es string del tipo Environment.DIRECTORY_PICTURES o Environment.DIRECTORY_DCIM
        //Nota 2: subDir es un string con barras: /predict/
        //NOTA 3: imageName es el nombre de la imagen a guardar como predict.jpg
        if(isExternalStorageWritable()) {
            File picturesDir = Environment.getExternalStoragePublicDirectory(environmentDir);
            String directorio = picturesDir.getAbsolutePath() ;
            Log.d(xxx, "El directorio es: " + directorio);
            //Toast.makeText(context,
                    //directorio, Toast.LENGTH_SHORT).show();
            if(saveImageToExternalPublicStorage(directorio, subDir, imageName)){
                //Toast.makeText(context,
                        //"Imagen guardada", Toast.LENGTH_SHORT).show();
                Log.d(xxx, "Imagen guardada");
            }else{
                //Toast.makeText(context,
                        //"ERROR Imagen NO guardada", Toast.LENGTH_LONG).show();
                Log.d(xxx, "ERROR: Imagen NO guardada" );
                return false;
            }


        }else{
            Log.d(xxx, "El external public storage no esta montado ");
            return false;

        }
        //Si llega aqui, la imagen se ha guardado correctamente
        return true;
    }



    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean saveImageToExternalPublicStorage(String directorio, String subDir, String imageName) {
    //Como en:
    //https://www.101apps.co.za/articles/using-android-s-file-system-for-saving-application-data-part-1-saving-files.html
    //http://www.e-nature.ch/tech/saving-loading-bitmaps-to-the-android-device-storage-internal-external/
        try {
            File dir = new File(directorio + subDir);
            if (!dir.exists()) {
                dir.mkdirs();
                Log.d(xxx, "Directorio predict creado ");
            }

            OutputStream fOut = null;
            //Siempre sobreescribe el fichero si ya existe
            File file = new File(directorio + subDir + imageName);
            //file.createNewFile();
            fOut = new FileOutputStream(file);

// 100 means no compression, the lower you go, the stronger the compression
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

            //Esta linea inserta dos imagenes bajo el dir images: una  con toda la resolucion y un thumbnail.
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

            return true;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            //Toast.makeText(context,
                    //e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
