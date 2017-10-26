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
                                      String cadenaDigitos,
                                      List<PojoCoordenadas> listaCoordenadasN,
                                      int intCenterConfig){
        int offsetX_ParaCentrar = 0;
        //*******************************************
        int anchuraUltimaImagenTipoN = 0;
        //Calculamos la anchura
        anchuraUltimaImagenTipoN = calcularAnchuraUltimaImagenTipoN(directorioActual, cadenaDigitos );
        Log.d(xxx, "getOffsetX_ParaCentrarImagenN, anchuraUltimaImagenTipoN:  "  +anchuraUltimaImagenTipoN);

        int coordX_DelPrimerDigito = Integer.parseInt(listaCoordenadasN.get(0).getCoordX());
        Log.d(xxx, "getOffsetX_ParaCentrarImagenN, coordX_DelPrimerDigito:  "  +coordX_DelPrimerDigito);

        //Revisamos la longitud de la cadena, para no tener un out of bounds exception
        int longitudCadenaDigitos= cadenaDigitos.length();
        int longitudArrayCoordenadasN = listaCoordenadasN.size();
        Log.d(xxx, "getOffsetX_ParaCentrarImagenN, longitudCadenaDigitos:  "  +longitudCadenaDigitos);
        Log.d(xxx, "getOffsetX_ParaCentrarImagenN, longitudArrayCoordenadasN:  "  +longitudArrayCoordenadasN);

        int coordX_DelUltimoDigito;
        if(longitudCadenaDigitos > longitudArrayCoordenadasN){

            coordX_DelUltimoDigito = Integer.parseInt(listaCoordenadasN.get(listaCoordenadasN.size()-1).getCoordX());

        }else{
            coordX_DelUltimoDigito = Integer.parseInt(listaCoordenadasN.get(cadenaDigitos.length()-1).getCoordX());

        }
        Log.d(xxx, "getOffsetX_ParaCentrarImagenN, coordX_DelUltimoDigito:  "  +coordX_DelUltimoDigito);

        //Aplicamos la formula del mail Centrado de cadenas/numeros:
        //Offset_X = Xa - (X2-X1+Anchura) / 2
        offsetX_ParaCentrar = intCenterConfig -
                Math.round((coordX_DelUltimoDigito - coordX_DelPrimerDigito + anchuraUltimaImagenTipoN) / 2);
        Log.d(xxx, "getOffsetX_ParaCentrarImagenN, offsetX_ParaCentrar:  "  +offsetX_ParaCentrar);

        //*******************************************
        return offsetX_ParaCentrar;
    }//Fin de getOffsetX_ParaCentrarImagenN

    //Recibe la cadena de digitos
    private int calcularAnchuraUltimaImagenTipoN(String directorioActual, String cadena){
        int anchura = 0;
        //Necesitamos saber la longitud para poner el nombre del fichero 0.bmp hasta 9.bmp
        char ultimaCoordenada = getUltimaCoordenadaTipoN(cadena);
        String nombreFichero1 = ultimaCoordenada +".bmp";
        String nombreFichero2 = ultimaCoordenada +".xbmp";
        ObtenerImagen obtenerImagen = new ObtenerImagen(context);
        File file = obtenerImagen.getFilePathOfPictureParaCentrar(directorioActual, nombreFichero1);
        File file2 = obtenerImagen.getFilePathOfPictureParaCentrar(directorioActual, nombreFichero2);
            //file tiene extension .xbmp


        //Obtener el array de bytes
        boolean boolPedirFile2 = false;
        byte bytes[] = obtenerImagen.getFileBytes(file);
        if(bytes == null){//ERROR
            //hay un error, devolvemos anchura como cero
            Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, array de bytes es null con .bmp, devolver anchura = 0");
            //return anchura;
            boolPedirFile2 = true;
        }else if(bytes.length == 0){
            //hay un error, devolvemos anchura como cero
            Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, array de bytes es null tiene 0 bytes con bmp:  " + bytes.length);
            Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, anchura:  " +anchura);
            boolPedirFile2 = true;
        }

        //Pedir el file2, con extension .xbmp
        if(boolPedirFile2) {
            bytes = obtenerImagen.getFileBytes(file2);
            if (bytes == null) {//ERROR
                //hay un error, devolvemos anchura como cero
                Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, array de bytes es null con .xbmp, devolver anchura = 0");
                return anchura;
            } else if (bytes.length == 0) {
                //hay un error, devolvemos anchura como cero
                Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, array de bytes es null tiene 0 bytes:  " + bytes.length);
                Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, anchura:  " + anchura);
                return anchura;

            }
        }



        //Tengo el array de bytes binario, seguimos
        Log.d(xxx, "array de bytes tiene datos, longitud del array:  " + bytes.length);

        //aplicamos la formula del mail Centrado de cadenas/numeros:  AnchuraF= Pos[18]+255  x  Pos[19]
        Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, bytes[18]:  " +bytes[18]);
        Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, bytes[19]:  " +bytes[19]);
        int suma = bytes[18]+255;
        Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, int suma = bytes[18]+255 :  " +suma);


        //anchura = (bytes[18]+(byte)255) * bytes[19];
        anchura = bytes[18]+ ((byte)255 * bytes[19]);
        Log.d(xxx, "calcularAnchuraUltimaImagenTipoN, anchura:  " +anchura);


        return anchura;
    }//FIN calcularAnchuraUltimaImagenTipoN

    private char getUltimaCoordenadaTipoN(String cadena){
        char ultimaCoordenada;
        char[] arrayImagesSequence = null;
        arrayImagesSequence = cadena.toCharArray();
        ultimaCoordenada = arrayImagesSequence[arrayImagesSequence.length - 1];
        Log.d(xxx, "getUltimaCoordenadaTipoN, ultimaCoordenada:   " + ultimaCoordenada);

        return ultimaCoordenada;
    }





    public int getOffsetX_ParaCentrarImagenT(String directorioActual, String nombreFicheroParaDigitos,
                                             String cadenaAlfanumerica,
                                             List<PojoCoordenadas> listaCoordenadasT,
                                             int intCenterConfig){
        int offsetX_ParaCentrar = 0;
        //*******************************************
        int anchuraUltimaImagenTipoT = 0;
        //Calculamos la anchura
        anchuraUltimaImagenTipoT = calcularAnchuraUltimaImagenTipoT(directorioActual, cadenaAlfanumerica );
        Log.d(xxx, "getOffsetX_ParaCentrarImagenT, anchuraUltimaImagenTipoT:  "  +anchuraUltimaImagenTipoT);

        int coordX_DelPrimerDigito = Integer.parseInt(listaCoordenadasT.get(0).getCoordX());
        Log.d(xxx, "getOffsetX_ParaCentrarImagenT, coordX_DelPrimerDigito:  "  +coordX_DelPrimerDigito);

        //Revisamos la longitud de la cadena, para no tener un out of bounds exception
        int longitudCadenaAlfanumerica= cadenaAlfanumerica.length();
        int longitudArrayCoordenadasT = listaCoordenadasT.size();
        Log.d(xxx, "getOffsetX_ParaCentrarImagenT, longitudCadenaAlfanumerica:  "  +longitudCadenaAlfanumerica);
        Log.d(xxx, "getOffsetX_ParaCentrarImagenT, longitudArrayCoordenadasT:  "  +longitudArrayCoordenadasT);

        int coordX_DelUltimoDigito;
        if(longitudCadenaAlfanumerica > longitudArrayCoordenadasT){

            coordX_DelUltimoDigito = Integer.parseInt(listaCoordenadasT.get(listaCoordenadasT.size()-1).getCoordX());

        }else{
            coordX_DelUltimoDigito = Integer.parseInt(listaCoordenadasT.get(cadenaAlfanumerica.length()-1).getCoordX());

        }
        Log.d(xxx, "getOffsetX_ParaCentrarImagenT, coordX_DelUltimoDigito:  "  +coordX_DelUltimoDigito);

        //Aplicamos la formula del mail Centrado de cadenas/numeros:
        //Offset_X = Xa - (X2-X1+Anchura) / 2
        offsetX_ParaCentrar = intCenterConfig -
                Math.round((coordX_DelUltimoDigito - coordX_DelPrimerDigito + anchuraUltimaImagenTipoT) / 2);
        Log.d(xxx, "getOffsetX_ParaCentrarImagenN, offsetX_ParaCentrar:  "  +offsetX_ParaCentrar);


        //*******************************************
        return offsetX_ParaCentrar;
    }



    //Recibe la cadena de alfanumericos
    private int calcularAnchuraUltimaImagenTipoT(String directorioActual, String cadena){
        int anchura = 0;
        //Convertir el ultimo caracter de la secuencia alfanumerica para usar el metodo matches con regex de string
        String prefijoNombreFile = "F1_";
        Character character = (Character)getUltimaCoordenadaTipoN(cadena);
        String charDeLaSecuenciaRecibida = character.toString();
        //Generar el nombre de la imagen a utilizar para la mezcla
        if(charDeLaSecuenciaRecibida.matches("[a-z]")){
            prefijoNombreFile += charDeLaSecuenciaRecibida.toUpperCase() +2;
        }else if (charDeLaSecuenciaRecibida.matches("[A-Z]")){
            prefijoNombreFile += charDeLaSecuenciaRecibida.toUpperCase() +1;

        }else if(charDeLaSecuenciaRecibida.matches("[0-9]")){
            prefijoNombreFile += charDeLaSecuenciaRecibida.toUpperCase();
        }else{
            //Si llega aqui, es por que hay algun character que no es valido
            Log.d(xxx, "metodo calcularAnchuraUltimaImagenTipoT, OJO, el ultimo caracter no es letra ni digito");
            Log.d(xxx, "metodo calcularAnchuraUltimaImagenTipoT, entonces fallara por que no va a encontrar" +
                    "el fichero correcto, por que el nombre de fichero no se ha completado");
            //Como no es un caracter valido, no ejecutamos lo que viene, volvemos al loop a chequear el siguiente caracter
        }

        String nombreFichero1 = prefijoNombreFile +".bmp";
        String nombreFichero2 = prefijoNombreFile +".xbmp";
        ObtenerImagen obtenerImagen = new ObtenerImagen(context);
        File file = obtenerImagen.getFilePathOfPictureParaCentrar(directorioActual, nombreFichero1);
        File file2 = obtenerImagen.getFilePathOfPictureParaCentrar(directorioActual, nombreFichero2);

        /*
        if(file == null){
            file = obtenerImagen.getFilePathOfPictureParaCentrar(directorioActual, nombreFichero2);
            //file tiene extension .xbmp
        }else{
            //File tiene extension bmp
        }

                //Obtener el array de bytes de la imagen
        byte bytes[] = obtenerImagen.getFileBytes(file);
        if(bytes == null){//ERROR
            //hay un error, devolvemos anchura como cero
            Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, array de bytes es null, devolver anchura = 0");
            return anchura;
        }else if(bytes.length == 0){
            //hay un error, devolvemos anchura como cero
            Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, array de bytes es null tiene 0 bytes:  " + bytes.length);
            Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, anchura:  " +anchura);

        }

        */
        //*******************************************************************************************

        //Obtener el array de bytes
        boolean boolPedirFile2 = false;
        byte bytes[] = obtenerImagen.getFileBytes(file);
        if(bytes == null){//ERROR
            //hay un error, devolvemos anchura como cero
            Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, array de bytes es null con .bmp, devolver anchura = 0");
            //return anchura;
            boolPedirFile2 = true;
        }else if(bytes.length == 0){
            //hay un error, devolvemos anchura como cero
            Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, array de bytes es null tiene 0 bytes con bmp:  " + bytes.length);
            Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, anchura:  " +anchura);
            boolPedirFile2 = true;
        }

        //Pedir el file2, con extension .xbmp
        if(boolPedirFile2) {
            bytes = obtenerImagen.getFileBytes(file2);
            if (bytes == null) {//ERROR
                //hay un error, devolvemos anchura como cero
                Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, array de bytes es null con .xbmp, devolver anchura = 0");
                return anchura;
            } else if (bytes.length == 0) {
                //hay un error, devolvemos anchura como cero
                Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, array de bytes es null tiene 0 bytes:  " + bytes.length);
                Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, anchura:  " + anchura);
                return anchura;

            }
        }




        //*********************************************************************************************





        //Tengo el array de bytes binario, seguimos
        Log.d(xxx, "array de bytes calcularAnchuraUltimaImagenTipoT datos, longitud del array:  " + bytes.length);

        //aplicamos la formula del mail Centrado de cadenas/numeros:  AnchuraF= Pos[18]+255  x  Pos[19]
        Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, bytes[18]:  " +bytes[18]);
        Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, bytes[19]:  " +bytes[19]);
        int suma = bytes[18]+255;
        Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, int suma = bytes[18]+255, es incorrecto :  " +suma);


        //anchura = (bytes[18]+(byte)255) * bytes[19];
        anchura = bytes[18]+ ((byte)255 * bytes[19]);
        Log.d(xxx, "calcularAnchuraUltimaImagenTipoT, anchura:  " +anchura);

        return anchura;
    }//Fin de calcularAnchuraUltimaImagenTipoT


}//Fin de la clase
