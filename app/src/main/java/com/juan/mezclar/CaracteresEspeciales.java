package com.juan.mezclar;

import android.content.Context;
import android.util.Log;

//Nuevo req el 29 ene 2018 en el correo: "caracteres especiales"
//Este req solo aplica a secuencias alfanumericas

public class CaracteresEspeciales {
    public String xxx = this.getClass().getSimpleName();
    public Context context;

    public CaracteresEspeciales(Context context){
        this.context = context;
        Log.d(xxx, "nueva instancia de:  " + xxx);
    }


    public String getNombreFicheroEspecial(String charDeLaSecuenciaRecibida){
        String prefijoNombreFile = null;
        if(charDeLaSecuenciaRecibida.matches("-")){
            prefijoNombreFile = "F1_GU";
        }else if (charDeLaSecuenciaRecibida.matches("\u20ac")){
            prefijoNombreFile = "F1_MM";
        }else if(charDeLaSecuenciaRecibida.matches("\\$")){
            prefijoNombreFile = "F1_NN";
        }else if(charDeLaSecuenciaRecibida.matches("\\(" )){
            prefijoNombreFile = "F1_((";
        }else if(charDeLaSecuenciaRecibida.matches("\\)")){
            prefijoNombreFile = "F1_))";
        }else if(charDeLaSecuenciaRecibida.matches("\\\\")){
            prefijoNombreFile = "F1_BS";
        }else if(charDeLaSecuenciaRecibida.matches("¡")){
            prefijoNombreFile = "F1_EX";
        }else if(charDeLaSecuenciaRecibida.matches("\\/")){
            prefijoNombreFile = "F1_FS";
        }else if(charDeLaSecuenciaRecibida.matches(",")){
            prefijoNombreFile = "F1_CO";
        }else if(charDeLaSecuenciaRecibida.matches(":")){
            prefijoNombreFile = "F1_SC";
        }else if(charDeLaSecuenciaRecibida.matches("\\.")){
            prefijoNombreFile = "F1_PO";
        }else{
            //Si llega aqui, es por que puede haber  algun character especial
            Log.d(xxx, "metodo getNombreFicheroEspecial, OJO, hay un caracter prohibido en la secuencia numerica");

        }

        return prefijoNombreFile;
    }

}
