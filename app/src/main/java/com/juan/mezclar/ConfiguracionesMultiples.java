package com.juan.mezclar;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

/**
 * Created by Juan on 23/10/2017.
 */


//Esta clase gestiona el requerimiento:
//REQ: Gestion de configuraciones multiples recibido el 23-10-17

public class ConfiguracionesMultiples{
    public String xxx = this.getClass().getSimpleName();
    public Context context;
    public static final String PREFS_NAME_1 = "MyActiveDir";
    String prefName = "activeDirectory";

    //Directorio de trabajo por defecto de la aplicacion
    String pathCesaralMagicImageC = "/CesaralMagic/ImageC/";

    public ConfiguracionesMultiples(Context context){
            this.context = context;
            Log.d(xxx, "Nueva instancia de la clase  ConfiguracionesMultiples" );

    }

    public void setActiveDirectory(String string){//Prueba OK
        //guarda en share preferences el directorio de trabajo actual:
        //que puede ser /CesaralMagic/ImageC/ (por defecto) o
        //alguno que cuelga de /CesaralMagic/ImageC/ introducido a traves de la UI
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_1, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(prefName, string); // Storing string
        editor.commit(); // commit changes
    }

    public String getActiveDirectory(){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_1, 0); // 0 - for private mode
        //***************************************************************************
        //Prueba OK. Hewcha para ver que envio el error correspondiente y lo escribo en el log de
        ///CesaralMagic/ImageC/ si por si acaso, el dir de preferencias NO EXISTE, cosa que no deberia ocurrir.
        //setActiveDirectory("dirPrueba");
        //Vuelvo a colocar el valor por defecto
        //setActiveDirectory(null);

        //***************************************************************************
        String activeDir = pref.getString(prefName, null); // getting String
        if(activeDir!= null){
            Log.d(xxx, "getActiveDirectory,  Directorio activo es: " +(pathCesaralMagicImageC +activeDir));
            return pathCesaralMagicImageC +activeDir;

        }else{
            Log.d(xxx, "getActiveDirectory,  Directorio activo es: " +pathCesaralMagicImageC);
            return pathCesaralMagicImageC;

        }
    }

    public List<String> getSubDirDeDirCesaralMagicImageC(){//Probado y OK
        //devuelve la lista de los dir que cuelgan de /CesaralMagic/ImageC/"
        //y la presenta en el dropdown menu de la UI

        //prueba para ver si lee la nueva carpeta creada con el ordenador
        ConfiguracionesDeDirectoriosApp configuracionesDeDirectoriosApp = new ConfiguracionesDeDirectoriosApp(context);
        List<String> listDirectories = configuracionesDeDirectoriosApp.getListaDirectorios(pathCesaralMagicImageC);
        for (int i=0; i < listDirectories.size(); i++){
            Log.d(xxx, "getSubDirDeDirCesaralMagicImageC, sub directorio es: " +listDirectories.get(i));
        }
        return listDirectories;
    }

    public void createSubDirDeDirCesaralMagicImageC(){
        //TODO crear un nuevo sub directorio con el nombre que el usuario ha introducido en
        //el edit text, No uno que haya seleccionado del drop down menu
        ConfiguracionesDeDirectoriosApp configuracionesDeDirectoriosApp = new ConfiguracionesDeDirectoriosApp(context);
        boolean boolDirCreado = configuracionesDeDirectoriosApp.crearSubDirMethod(pathCesaralMagicImageC, "nuevo_dir_2");

    }






}
