package com.juan.mezclar;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//Esta clase gestiona el requerimiento:
//REQ: Gestion de configuraciones multiples recibido el 23-10-17
//Pero solo la parte que tiene que ver con crear un nuevo directorio bajo "/CesaralMagic/ImageC/"
//o leer los directorios que cuelgan bajo "/CesaralMagic/ImageC/"


public class ConfiguracionesDeDirectoriosApp {
    String xxx = this.getClass().getSimpleName();
    Context context;
    //Directorio de trabajo por defecto de la aplicacion
    String pathCesaralMagicImageC = "/CesaralMagic/ImageC/";

    public ConfiguracionesDeDirectoriosApp(Context context){
        this.context = context;
    }


    public boolean crearSubDirMethod(String environmentDir, String subDir){
        //environmentDir es normalmente /CesaralMagic/ImageC/
        //Nota 2: subDir es un string sin barras: my_nuevo_directorio
        //subDir es el nuevo directorio que vamos a crear
        if(isExternalStorageWritable()) {
            File dir = Environment.getExternalStoragePublicDirectory(environmentDir);
            String directorio = dir.getAbsolutePath() ;
            Log.d(xxx, "crearSubDirMethod, El directorio es: " + directorio);
            Log.d(xxx, "crearSubDirMethod, El sub directorio es: " + subDir);

            if(crearSubDirMethod2(directorio +"/", subDir)){
                Log.d(xxx, "crearSubDirMethod, Sub directorio creado");
            }else{
                //Toast.makeText(context,
                        //"ERROR Imagen NO guardada", Toast.LENGTH_LONG).show();
                Log.d(xxx, "crearSubDirMethod, ERROR: Sub directorio no creado" );
                return false;
            }


        }else{
            Log.d(xxx, "El external public storage no esta montado ");
            return false;

        }
        //Si llega aqui, la imagen se ha guardado correctamente
        return true;
    }

    public boolean borrarSubDirMethod(String environmentDir, String subDir){
        //environmentDir es normalmente /CesaralMagic/ImageC/
        //Nota 2: subDir es un string sin barras: my_nuevo_directorio
        //subDir es el nuevo directorio que vamos a crear
        if(isExternalStorageWritable()) {
            File dir = Environment.getExternalStoragePublicDirectory(environmentDir);
            String directorio = dir.getAbsolutePath() ;
            Log.d(xxx, "borrarSubDirMethod, El directorio es: " + directorio);
            Log.d(xxx, "borrarSubDirMethod, El sub directorio es: " + subDir);

            if(borrarSubDirMethod2(directorio +"/", subDir)){
                Log.d(xxx, "borrarSubDirMethod, Sub directorio borrado");
            }else{
                //Toast.makeText(context,
                //"ERROR Imagen NO guardada", Toast.LENGTH_LONG).show();
                Log.d(xxx, "borrarSubDirMethod, ERROR: Sub directorio no borrado" );
                return false;
            }


        }else{
            Log.d(xxx, "en borrarSubDirMethod, El external public storage no esta montado ");
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

    public boolean crearSubDirMethod2(String directorio, String subDir) {
    //Ver para referencia:
    //http://www.java2s.com/Code/Android/Core-Class/Createanewdirectoryonexternalstorage.htm
        try {
            File dir = new File(directorio + subDir);
            if (!dir.exists()) {
                dir.mkdirs();
                Log.d(xxx, "crearSubDirMethod2,  Nuevo sub directorio creado: "  +dir.getAbsolutePath());
            }


            return true;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            return false;
        }
    }

    public boolean borrarSubDirMethod2(String directorio, String subDir) {
        //Ver para referencia:
        //http://www.java2s.com/Code/Android/Core-Class/Createanewdirectoryonexternalstorage.htm

        //este mejor
        //https://stackoverflow.com/questions/14930908/how-to-delete-all-files-and-folders-in-one-folder-on-android
        boolean directorioBorrado = true;//por defecto
        try {
            File dir = new File(directorio + subDir);
            if (dir.exists()) {
                //directorioBorrado = dir.delete();
                FileUtils.deleteDirectory(dir);
                if(directorioBorrado)
                Log.d(xxx, "borrarSubDirMethod2,  directorio borrado");
            }


            return directorioBorrado;

        } catch (Exception e) {
            Log.d(xxx, "En el catch, directorio no borrado: " +e.getMessage());
            return false;
        }
    }


    List<String> getListaDirectorios(String directorio) {
        //dir es normalmente /CesaralMagic/ImageC/
        //File directorioMain = new File(Environment.getExternalStorageDirectory() + pathToFileTxt);
        File dir = new File(Environment.getExternalStorageDirectory() +directorio);
        ArrayList<String> inDir = new ArrayList<String>();
        //Siempre pongo el defaul delante:
        inDir.add("default directory");
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                //Log.d(xxx, "getListaDirectorios, sub directorio en \"/CesaralMagic/ImageC/\": "  +file.getAbsolutePath());
                Log.d(xxx, "getListaDirectorios, sub directorio en /CesaralMagic/ImageC/: "  +file.getAbsolutePath());
                Log.d(xxx, "getListaDirectorios, SOLO sub directorio en /CesaralMagic/ImageC/: "  +file.getName());
                //inDir.add(file.getAbsolutePath());

                //Solo pongo el nombre del dir, NO todo el path
                //Solo da el nombre sin /
                inDir.add(file.getName());

                //Si es muy grande la lista, funciona el scroll, probado con estas lineas
                /*
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName());
                inDir.add(file.getName()); */
            }
        }


        return inDir;
    }



    //No uso este metodo, lo tengo para referencia. Extrae ficheros o directorios
    List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

}
