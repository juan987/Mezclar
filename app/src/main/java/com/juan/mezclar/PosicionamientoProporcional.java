package com.juan.mezclar;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * Created by Juan on 02/11/2017.
 */

/*

Posicionamiento de ficheros en modo proporcional

Este parámetro es opcional, y funciona para los modos numérico y alfanumérico.

Si el CONFIG.txt tiene la línea CENTER_P=nnn, entonces la App posicionará los caracteres en coordenadas X en función del nnn y de la anchura de cada carácter/numero. EN este caso no se tendrá en cuenta los valores X del CONFIG.txt.

Por ejemplo si se pasa la cadena alfanumérica:   ABc1

L1 es la anchura en pixels de del carácter “A”
L2 es la anchura en pixels de del carácter “B”
L3 es la anchura en pixels de del carácter “c”
L4 es la anchura en pixels de del carácter “1”
Supongamos que la anchura total es L=L1+L2+L3+L4

El posicionamiento del primer carácter será  en X=nnn-(L/2)
El posicionamiento del segundo carácter será  en X=nnn-(L/2)+L1
El posicionamiento del tercer carácter será  en X=nnn-(L/2)+L1+L2
El posicionamiento del cuarto carácter será  en X=nnn-(L/2)+L1+L2+L3

Anchura del grafico correspondiente: leer los bytes y operar asi:
La fórmula correcta es: Pos[18] + (Pos[19] x 255)
 */

public class PosicionamientoProporcional {
    public String xxx = this.getClass().getSimpleName();
    public Context context;

    public PosicionamientoProporcional(Context context){
        this.context = context;
        Log.d(xxx, "nueva instancia de:  " + xxx);
    }


    //Este metodo es para las secuencias numericas
    public int[] getArrayAnchurasImagenesPequenas(String pathCesaralMagicImageC, char[] arrayImagesSequence) {
        int[] arrayAnchuraImagenesPequeñas = new int[arrayImagesSequence.length];
        ObtenerImagen obtenerImagen = new ObtenerImagen(context);

        for (int i = 0; i < arrayImagesSequence.length; i++) {

            String nombreFichero = arrayImagesSequence[i]+".bmp";
            File file = obtenerImagen.getFilePathOfPictureParaCentrar(pathCesaralMagicImageC, nombreFichero);
            //Aunque el fichero no exista, file tiene el  path, nunca es null
            /*
            if(file == null){//intentamos con la extension .xbmp
                nombreFichero = arrayImagesSequence[i]+".xbmp";
                file = obtenerImagen.getFilePathOfPictureParaCentrar(pathCesaralMagicImageC, nombreFichero);
            }
            */
            //Obtener el array de bytes del fichero
            byte bytes[] = obtenerImagen.getFileBytes(file);
            if(bytes == null) {//ERROR
                Log.d(xxx, "getArrayAnchurasImagenesPequenas, array de bytes es null con .bmp");
                //return null, indica que ha habido un fallo
                nombreFichero = arrayImagesSequence[i] + ".xbmp";
                file = obtenerImagen.getFilePathOfPictureParaCentrar(pathCesaralMagicImageC, nombreFichero);
                bytes = obtenerImagen.getFileBytes(file);
                if (bytes == null) {
                    Log.d(xxx, "getArrayAnchurasImagenesPequenas, array de bytes es null con .xbmp");
                    return null;
                }
            }
                /*
            }else if(bytes.length == 0){
                Log.d(xxx, "getArrayAnchurasImagenesPequenas, array de bytes tiene longitud cero, lo devuelvo como null");
                //return null, indica que ha habido un fallo
                return null;
            }
            */

            //seguimos
            //Calcular la anchura de la imagen
            int anchura = bytes[18]+ ((byte)255 * bytes[19]);
            Log.d(xxx, "getArrayAnchurasImagenesPequenas, anchura:  " +anchura +"de imagin: " +i);
            arrayAnchuraImagenesPequeñas[i] = anchura;
        }//FIN de for (int i = 0; i < arrayImagesSequence.length; i++)

        return arrayAnchuraImagenesPequeñas;
    }//Fin de getArrayAnchurasImagenesPequenas




    public int getAnchoTotalDeTodasLasImagenes(int[] arrayAnchuraImagenesPequeñas){
        //sumamos las anchuras de todas las imagenes pequeñas
        int intAnchoTotal = 0;
        for(int i = 0; i < arrayAnchuraImagenesPequeñas.length; i++){
            intAnchoTotal += arrayAnchuraImagenesPequeñas[i];
        }
        Log.d(xxx, "getAnchoTotalDeTodasLasImagenes, intAnchoTotal: " +intAnchoTotal);
        return intAnchoTotal;
    }

    //Devuelve la posicion x que corresponpe a la coordenada Nx
    //este metodo lo usan las secuencias numericas y alfanumericas
    public float center_pGetPosicionX(int intCenter_p, int loopSize, int[] arrayAnchuraImagenesPequeñas, int intAnchoTotal){
        Log.d(xxx, "center_pGetPosicionX, loopSize: " +loopSize);

        //El posicionamiento del primer carácter será  en X=nnn-(L/2)
        //El posicionamiento del segundo carácter será  en X=nnn-(L/2)+L1
        //El posicionamiento del tercer carácter será  en X=nnn-(L/2)+L1+L2
        //El posicionamiento del cuarto carácter será  en X=nnn-(L/2)+L1+L2+L3
        //etc.....

        //Calculo de X
        float xFloat;
        int intL_Suma = 0;//ES L1+L2..... etc
        //for(int i = 0; i < (loopSize + 1); i++){
        if(loopSize == 0){
            intL_Suma = 0;
            Log.d(xxx, "center_pGetPosicionX, intL_Suma: " +intL_Suma);
        }else{
            for(int i = 0; i < (loopSize); i++){
                intL_Suma += arrayAnchuraImagenesPequeñas[i];
                Log.d(xxx, "center_pGetPosicionX, intL_Suma: " +intL_Suma);
            }
        }

        xFloat = intCenter_p - Math.round(intAnchoTotal/2) + intL_Suma;
        Log.d(xxx, "center_pGetPosicionX, nuevo xFloat: " +xFloat);
        return xFloat;
    }


    //este metodo lo usan las secuencias numericas y alfanumericas
    public float center_pGetPosicionX2(int intCenter_p, int loopSize, int[] arrayAnchuraImagenesPequeñas, int intAnchoTotal){
        Log.d(xxx, "center_pGetPosicionX, loopSize: " +loopSize);

        //El posicionamiento del primer carácter será  en X=nnn-(L/2)
        //El posicionamiento del segundo carácter será  en X=nnn-(L/2)+L1
        //El posicionamiento del tercer carácter será  en X=nnn-(L/2)+L1+L2
        //El posicionamiento del cuarto carácter será  en X=nnn-(L/2)+L1+L2+L3
        //etc.....

        //Calculo de X
        float xFloat;
        int intL_Suma = 0;//ES L1+L2..... etc
        //for(int i = 0; i < (loopSize + 1); i++){
        for(int i = 0; i <= (loopSize); i++){
            if(loopSize == 0){
                intL_Suma = 0;
                Log.d(xxx, "center_pGetPosicionX, intL_Suma: " +intL_Suma);
                break;
            }
            intL_Suma += arrayAnchuraImagenesPequeñas[i];
            i++;

                Log.d(xxx, "center_pGetPosicionX, intL_Suma: " +intL_Suma);


        }
        xFloat = intCenter_p - Math.round(intAnchoTotal/2) + intL_Suma;
        Log.d(xxx, "center_pGetPosicionX, nuevo xFloat: " +xFloat);
        return xFloat;
    }



    //Este metodo es para las secuencias alfanumericas
    public int[] getArrayAnchurasImagenesPequenasAlfa(String pathCesaralMagicImageC, char[] arrayImagesSequence) {
        int[] arrayAnchuraImagenesPequeñas = new int[arrayImagesSequence.length];
        ObtenerImagen obtenerImagen = new ObtenerImagen(context);

        for (int i = 0; i < arrayImagesSequence.length; i++) {
            String prefijoNombreFile = "F1_";
            String nombreFichero = "";
            //Convertir el caracter de la secuencia alfanumerica para usar el metodo matches con regex de string
            Character character = (Character)arrayImagesSequence[i];
            String charDeLaSecuenciaRecibida = character.toString();
            //Generar el nombre de la imagen a utilizar para la mezcla
            if(charDeLaSecuenciaRecibida.matches("[a-z]")){
                prefijoNombreFile += charDeLaSecuenciaRecibida.toUpperCase() +2;
                nombreFichero = prefijoNombreFile;

            }else if (charDeLaSecuenciaRecibida.matches("[A-Z]")){
                prefijoNombreFile += charDeLaSecuenciaRecibida.toUpperCase() +1;
                nombreFichero = prefijoNombreFile;

            }else if(charDeLaSecuenciaRecibida.matches("[0-9]")){
                prefijoNombreFile += charDeLaSecuenciaRecibida.toUpperCase();
                nombreFichero = prefijoNombreFile;

            }else{//" nov: No hago nada aqui, voy a asumir que la secuencia alfa solo tiene caracteres validos
                //Si llega aqui, es por que hay algun character que no es valido
                Log.d(xxx, "metodo getArrayAnchurasImagenesPequenasAlfa, OJO, hay un caracter prohibido en la secuencia numerica");
                Log.d(xxx, "metodo getArrayAnchurasImagenesPequenasAlfa, OJO, " +
                        "en este caso usamos la anchura que se encuentre en el fichero  F1_A1.xbmp" +
                         "Como esta puesto en el mail Plan viernes - corregido del 3 nov 17");

                prefijoNombreFile += "A1";
                nombreFichero = prefijoNombreFile;
                //Como no es un caracter valido, no ejecutamos lo que viene, volvemos al loop a chequear el siguiente caracter
            }

            File file = obtenerImagen.getFilePathOfPictureParaCentrar(pathCesaralMagicImageC, nombreFichero + ".bmp");
            byte bytes[] = obtenerImagen.getFileBytes(file);

            if(bytes == null) {//ERROR
                Log.d(xxx, "getArrayAnchurasImagenesPequenasAlfa, array de bytes es null con .bmp");
                //return null, indica que ha habido un fallo
                nombreFichero = nombreFichero + ".xbmp";
                file = obtenerImagen.getFilePathOfPictureParaCentrar(pathCesaralMagicImageC, nombreFichero);
                bytes = obtenerImagen.getFileBytes(file);
                if (bytes == null) {
                    Log.d(xxx, "getArrayAnchurasImagenesPequenasAlfa, array de bytes es null con .xbmp");
                    return null;
                }
            }

            //seguimos
            //Calcular la anchura de la imagen
            int anchura = bytes[18]+ ((byte)255 * bytes[19]);
            Log.d(xxx, "getArrayAnchurasImagenesPequenasAlfa, anchura:  " +anchura +"de imagin: " +i);
            arrayAnchuraImagenesPequeñas[i] = anchura;
        }//FIN de for (int i = 0; i < arrayImagesSequence.length; i++)

        return arrayAnchuraImagenesPequeñas;
    }//Fin de getArrayAnchurasImagenesPequenasAlfa

}
