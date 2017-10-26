package com.juan.mezclar;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.List;

/**
 * Created by Juan on 26/10/2017.
 */

//Nuevo requerimiento, centrado de cadenas/numeros recibido el 26 oct 2017


public class CentradoCadenasNumeros {
    public String xxx = this.getClass().getSimpleName();
    public Context context;

    public CentradoCadenasNumeros(Context context){
        this.context = context;
        Log.d(xxx, "nueva instancia de:  " + xxx);

    }

    public int getOffsetX_ParaCentrarImagenN(String directorioActual, String nombreFicheroParaDigitos,
                                      String nombreFicheroParaAlphanumericos,
                                      String cadenaDigitos,
                                      List<PojoCoordenadas> listaCoordenadasN,
                                      int intCenterConfig){
        int offsetX_ParaCentrar = 0;
        //*******************************************
        int anchuraUltimaImagenTipoN = 0;
        //Calculamos la anchura
        anchuraUltimaImagenTipoN = calcularAnchuraUltimaImagenTipoN(directorioActual, cadenaDigitos );
        //La coordenada X del primer carácter:



        //*******************************************
        return offsetX_ParaCentrar;
    }//Fin de getOffsetX_ParaCentrarImagenN

    //Recibe la cadena de digitos
    private int calcularAnchuraUltimaImagenTipoN(String directorioActual, String cadena){
        int anchura = 0;
        //Necesitamos saber la longitud para poner el nombre del fichero 0.bmp hasta 9.bmp
        int ultimaCoordenada = getUltimaCoordenadaTipoN(cadena);
        String nombreFichero1 = ultimaCoordenada +".bmp";
        String nombreFichero2 = ultimaCoordenada +".xbmp";
        ObtenerImagen obtenerImagen = new ObtenerImagen(context);
        File file = obtenerImagen.getFilePathOfPicture(Environment.DIRECTORY_DCIM, directorioActual, nombreFichero1);
        if(file == null){
            file = obtenerImagen.getFilePathOfPicture(Environment.DIRECTORY_DCIM, directorioActual, nombreFichero2);
            //file tiene extension .xbmp
        }else{
            //File tiene extension bmp
        }

        //Obtener el array de bytes
        byte bytes[] = obtenerImagen.getFileBytes(file);
        if(bytes == null){//ERROR
            //hay un error, devolvemos anchura como cero
            Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, array de bytes es null, devolver anchura = 0");
            return anchura;
        }else if(bytes.length == 0){
            //hay un error, devolvemos anchura como cero
            Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, array de bytes es null o longitud cero, devolver anchura = 0:  " + bytes.length);
            return anchura;

        }
        //Tengo el array de bytes binario, seguimos
        Log.d(xxx, "array de bytes tiene datos, longitud del array:  " + bytes.length);

        //aplicamos la formula del mail Centrado de cadenas/numeros:  AnchuraF= Pos[18]+255  x  Pos[19]
        anchura = (bytes[18]+255) * bytes[19];

        return anchura;
    }//FIN calcularAnchuraUltimaImagenTipoN

    private int getUltimaCoordenadaTipoN(String cadena){
        int ultimaCoordenada = 0;
        char[] arrayImagesSequence = null;
        arrayImagesSequence = cadena.toCharArray();
        ultimaCoordenada = arrayImagesSequence[arrayImagesSequence.length - 1];
        Log.d(xxx, "getUltimaCoordenadaTipoN, ultimaCoordenada:   " + ultimaCoordenada);

        return ultimaCoordenada;
    }

    private int getPrimeraCoordenadaTipoN(String cadena){
        int primeraCoordenada = 0;
        char[] arrayImagesSequence = null;
        arrayImagesSequence = cadena.toCharArray();
        primeraCoordenada = arrayImagesSequence[0];
        Log.d(xxx, "getPrimeraCoordenadaTipoN, primeraCoordenada:   " + primeraCoordenada);

        return primeraCoordenada;
    }



    public int getOffsetX_ParaCentrarImagenT(String directorioActual, String nombreFicheroParaDigitos,
                                             String nombreFicheroParaAlphanumericos,
                                             String cadenaAlfanumerica,
                                             List<PojoCoordenadas> listaCoordenadasT,
                                             int intCenterConfig){
        int offsetX_ParaCentrar = 0;
        //*******************************************
        int anchuraUltimaImagenTipoT = 0;
        //Calculamos la anchura
        anchuraUltimaImagenTipoT = calcularAnchuraUltimaImagenTipoT(directorioActual, cadenaAlfanumerica );


        //*******************************************
        return offsetX_ParaCentrar;
    }



    //Recibe la cadena de alfanumericos
    private int calcularAnchuraUltimaImagenTipoT(String directorioActual, String cadena){
        int anchura = 0;
        int ultimaCoordenada = cadena.length();
        return anchura;
    }


}//Fin de la clase
