package com.juan.mezclar;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Juan on 23/10/2017.
 */


//Esta clase gestiona el requerimiento:
//REQ: Gestion de configuraciones multiples recibido el 23-10-17

public class ConfiguracionAlfanumerica {
    public String xxx = this.getClass().getSimpleName();
    public Context context;
    public static final String PREFS_NAME_1 = "secuenciaAlfanumerica";
    String prefName = "secuencia_alfanumerica";

    //Directorio de trabajo por defecto de la aplicacion
    String pathCesaralMagicImageC = "/CesaralMagic/ImageC/";

    public ConfiguracionAlfanumerica(Context context){
            this.context = context;
            Log.d(xxx, "Nueva instancia de la clase  ConfiguracionAlfanumerica" );

    }

    public void setStringAlfanumerico(String string){//Prueba OK
        //guarda en share preferences lo que haya en el campo alfanumerico de la pantalla de ActivityLauncherUI

        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_1, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        if(string.equals("")){
            //No hay nada en el campo alfanumerico, asignamos null
            editor.putString(prefName, null); // Storing string
            Log.d(xxx, "getStringAlfanumerico, No hay nada en el campo alfanumerico, asignamos null");


        }else{
            //Guardamos el campo alfanumerico
            editor.putString(prefName, string+"/"); // Storing string
            Log.d(xxx, "getStringAlfanumerico, la secuencia alfanumerica guardad es: " + string);


        }
        editor.commit(); // commit changes
    }

    public String getStringAlfanumerico(){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME_1, 0); // 0 - for private mode
        String secuenciaAlphanumerica = pref.getString(prefName, null); // getting String
        if(secuenciaAlphanumerica!= null){
            Log.d(xxx, "getStringAlfanumerico, la secuencia alfanumerica recuperada es: " + secuenciaAlphanumerica);
            return secuenciaAlphanumerica;

        }else{
            Log.d(xxx, "getStringAlfanumerico, NO hay secuencia alfanumerica, se retorna null");
            return null;

        }
    }


}
