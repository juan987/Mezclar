package com.juan.mezclar;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juan on 25/10/2017.
 */

public class DatosConfigTxt {
    public String xxx = this.getClass().getSimpleName();
    public Context context;
    //ArrayList para guardar las coordenadas alfanumericas tipo T del fichero CONFIG.txt, es un variable de la clase
    ArrayList<PojoCoordenadas> arrayPojoCoordenadasAlfanumerico = new ArrayList<>();
    String urlServidor = "";
    //Almacena el user, a partir de version 1.0.1
    String user = "";
    //Almacena la contraseña a partir de version 1.0.1
    String password = "";

    //Almacena el SOR a partir de version 1.0.2
    String stringSOR = "";

    //Almacena el param de config overwrite. Nuevo req el 20oct17.
    //25oct17: Cambio de requerimiento en mail apk 1.0.12:
    //Si el parametro "OVERWRITE" de CONFIG.txt no existe o es OVERWRITE=1, se sobbreescribe predict.jpg
    //Entonces se envia stringOverwrite = "overwrite" para sobreescribir
    //Para generar un nombre distinto cada vez, tiene que estra el parametro con valor 0: OVERWRITE=1
    String stringOverwrite = "overwrite";

    //Parametrros de offset y scale como strings para recuperarlos del CONFIX.txt
    String stringOffset_x="";
    String stringOffset_y="";
    String stringScale_x="";

    //Nuevo requerimiento, centrado de cadenas/numeros recibido el 26 oct 2017
    String stringIntCenterConfig="";
    int intCenterConfig = 0;
    boolean boolUsarCenter = false;


    public boolean getBoolUsarCenter() {
        return boolUsarCenter;
    }

    //Nuevo req recibido el 27oct17, parametro m_x
    //Se trata de un parámetro opcional para el modo numérico solamente, denominado M_X
    String stringMX = "";
    int intMX = 0;
    boolean boolMX = false;
    public boolean getboolMX() {
        return boolMX;
    }
    public int getintMX() {
        return intMX;
    }


    //Nuevo req recibido el 27oct17,por telefono: parametro q para la calidad con la que se salva el
    //bitmap.
    //100 es la maxima calidad, si disminuye, baja la calidad.
    //Lo usa la clase GuardarImagenFinal al almacenar el bitmap compuesto.
    //Si no es valor entre 0 y 100, uso 100 por defecto = no compression, maxima calidad.
    public String string_q = "";
    private int intq = 100;


    //2 nov 2017, parametro mode_t, nuevo req del mail proximos requerimientos
    //solo afecta a la cadena de numeros
    String stringMode_t = "0";//por defecto
    public String getMode_t() {
        return stringMode_t;
    }


    //2 nov 2017, Posicionamiento de ficheros en modo proporcional, parametro CENTER_P=nnn, nuevo req del mail proximos requerimientos
    //Afecta a la cadena de numeros y alfanumericos
    String stringCenter_p = "";
    int intCenter_p = 0;//por defecto, y este es el valor a comparar.
    public int getIntCenter_p() {
        return intCenter_p;
    }



    //6 nov 2017, nuevo req en mail Plan lunes - Modo rotacional
    //Te paso los requerimientos del modo nuevo de rotación:
    //parámetro opcional MODE_C=1
    //Solo aplica al modo numerico
    //13 nov 17: mode_c tambien puede valer 2, ver mail detalle importante en MODE_C.
    String stringMode_c = "0";//por defecto
    public String getMode_c() {
        return stringMode_c;
    }



    //13 nov 2017, nuevo req en mail: offet_h y offset_m, pueden ser positivos o negativos, son grados
    String offset_h = "0";
    String offset_m = "0";

    public float getOffset_h() {
        //return offset_h;
        return Float.parseFloat(offset_h);
    }

    public float getOffset_m() {
        //return offset_m;
        return Float.parseFloat(offset_m);
    }

    //Parametro offset y scale para modificar coordenadas N y T
    int intOffset_x=0;
    int intOffset_y=0;
    Double doubleScale_x=1.0;

    public int getIntOffset_x() {
        return intOffset_x;
    }

    public int getIntOffset_y() {
        return intOffset_y;
    }

    public Double getDoubleScale_x() {
        return doubleScale_x;
    }


    public DatosConfigTxt(Context context){
        this.context = context;
        Log.d(xxx, "nueva instancia de:  " + xxx);

    }

    public ArrayList<PojoCoordenadas> getArrayPojoCoordenadasAlfanumerico() {
        return arrayPojoCoordenadasAlfanumerico;
    }

    public String getUrlServidor() {
        return urlServidor;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getStringSOR() {
        return stringSOR;
    }

    public String getStringOverwrite() {
        return stringOverwrite;
    }

    public int getIntCenterConfig() {
        return intCenterConfig;
    }



    //23 ene 2018: Nuevo req de email "CUPP Lite - nuevo requerimiento de Cesar", recibido el 14 ene 18
    public String F_R = "0";
    public String F_G = "0";
    public String F_B = "0";

    public int int_F_R = -1;
    public int int_F_G = -1;
    public int int_F_B = -1;

    public int getInt_F_R() {
        return int_F_R;
    }

    public int getInt_F_G() {
        return int_F_G;
    }

    public int getInt_F_B() {
        return int_F_B;
    }

    //7 nov 17, metodo para quitar todsos los espacios en blanco de las lineas de config
    public List<String> quitarEspaciosBlancos(List<String> arrayLineasTextoLocal){
        for(int i = 0; i < arrayLineasTextoLocal.size(); i++){
            String lineaSinBlancos = "";
            for(int c = 0; c < arrayLineasTextoLocal.get(i).length(); c++){
                if(arrayLineasTextoLocal.get(i).charAt(c) != ' '){
                    lineaSinBlancos += arrayLineasTextoLocal.get(i).charAt(c);
                }

            }
            arrayLineasTextoLocal.set(i, lineaSinBlancos);
        }//Fin de for(int i = 0; i <= arrayLineasTextoLocal.size(); i++)
        return arrayLineasTextoLocal;
    }








    //Metodo para:
    //Generar array de PojoCoordenadas con las coordenadas x e Y de posicionamiento de imagenes
    //Generar la URL para subir y almacenar la imagen generada a un servidor
    //tambien lee user, password, SOR.
    //public List<PojoCoordenadas> generarPojoGenerarUrl(List<String> arrayLineasTextoLocal){
    public List<PojoCoordenadas> getCoordenadasN(List<String> arrayLineasTextoLocal){
        //Imprimo las lineas del CONFIG original
        imprimirLineasConfigTxt(arrayLineasTextoLocal);

        //**********************************************************************
        //**********************************************************************
        //7 nov 17: quito todos los especios en blanco de todas las lineas:
        //los leading, los trailing y los que haya por medio
        arrayLineasTextoLocal = quitarEspaciosBlancos(arrayLineasTextoLocal);

        //**********************************************************************
        //**********************************************************************
        //Imprimo las lineas del CONFIG sin espacios en blanco
        Log.d(xxx, "getCoordenadasN, lines de CONFIG sin blancos:  ");
        imprimirLineasConfigTxt(arrayLineasTextoLocal);



        ArrayList<PojoCoordenadas> arrayPojoCoordenadas = new ArrayList<>();
        String regex = "[^\\d]+";
        for(int i = 0; i < arrayLineasTextoLocal.size(); i++){
            //Solo quiero las lineas que empiezan con N
            //if(arrayLineasTextoLocal.get(i).startsWith("N")) {
            if(arrayLineasTextoLocal.get(i).toUpperCase().startsWith("N")) {

                //Extrae las coordenadas x e y de cada linea con regex y genera pojo de
                //coordenadas por cada linea y lo guarda en el array de coordenadas
                String[] str = arrayLineasTextoLocal.get(i).split(regex);
                //Recorro y muestro con Log.d el array str
                for (int i2 = 0; i2 < str.length; i2++) {
                    Log.d(xxx, "En metodo generarPojoGenerarUrl, despues del split con regex" + "\n"
                            + "Linea " + i + "\n"
                            + "posicion " + i2 + " tiene: " + str[i2]);
                }
                //Si la linea no tiene digitos, hago un break y continua el loop
                //if(str.length == 0) break;
                PojoCoordenadas pojoCoordenadas = new PojoCoordenadas();
                try {
                    if (str.length > 2) {//Para evitar las lineas blancas y las que no tienen coordenadas y que de ArrayIndexOutOfBoundsException
                        pojoCoordenadas.setCoordX(str[2]);
                        pojoCoordenadas.setCoordY(str[3]);
                        arrayPojoCoordenadas.add(pojoCoordenadas);

                    } else {
                        Log.d(xxx, "En generarPojoGenerarUrl, la linea " + i + " esta vacia o no tiene coordenadas: "
                                + arrayLineasTextoLocal.get(i));

                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.d(xxx, "ArrayIndexOutOfBoundsException:  " + e.getMessage());
                    return null;
                }
            }//Fin de if(arrayLineasTextoLocal.get(i).startsWith("N"))

            //***************************************************************************************
            //***************************************************************************************
            //***************************************************************************************

            //Solo quiero las lineas que empiezan con T
            if(arrayLineasTextoLocal.get(i).toUpperCase().startsWith("T")) {

                //Extrae las coordenadas x e y de cada linea con regex y genera pojo de
                //coordenadas por cada linea y lo guarda en el array de coordenadas
                String[] str = arrayLineasTextoLocal.get(i).split(regex);
                //Recorro y muestro con Log.d el array str
                for (int i2 = 0; i2 < str.length; i2++) {
                    Log.d(xxx, "En metodo generarPojoGenerarUrl, despues del split con regex" + "\n"
                            + "Linea " + i + "\n"
                            + "posicion " + i2 + " tiene: " + str[i2]);
                }
                //Si la linea no tiene digitos, hago un break y continua el loop
                //if(str.length == 0) break;
                PojoCoordenadas pojoCoordenadas = new PojoCoordenadas();
                try {
                    if (str.length > 2) {//Para evitar las lineas blancas y las que no tienen coordenadas y que de ArrayIndexOutOfBoundsException
                        pojoCoordenadas.setCoordX(str[2]);
                        pojoCoordenadas.setCoordY(str[3]);
                        arrayPojoCoordenadasAlfanumerico.add(pojoCoordenadas);

                    } else {
                        Log.d(xxx, "En generarPojoGenerarUrl, la linea " + i + " esta vacia o no tiene coordenadas: "
                                + arrayLineasTextoLocal.get(i));

                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.d(xxx, "ArrayIndexOutOfBoundsException:  " + e.getMessage());
                    return null;
                }
            }//Fin de if(arrayLineasTextoLocal.get(i).startsWith("T"))



            //***************************************************************************************
            //***************************************************************************************
            //***************************************************************************************





        }//FIN de for(int i = 0; i < arrayLineasTextoLocal.size(); i++)



        //Este for extrae la URL del servidor, el user y el password que estan en CONFIG.txt
        String[] stringURLFinal = null;
        String[] arrayStringUser = null;
        String[] arrayStringPass = null;
        String[] arrayStringSOR = null;
        String[] array_intOffset_x = null;
        String[] array_intOffset_y = null;
        String[] array_doubleScale_x = null;
        String[] arrayStringOverwrite = null;
        String[] arrayIntCenter = null;
        String[] arrayMX = null;
        String[] arrayQ = null;
        String[] arrayMode_t = null;
        String[] arraycenter_p = null;
        String[] arrayMode_c = null;


        String regexUrl = "=";
        String regexUser = "=";
        String regexPass = "=";
        String regexSOR = "=";
        String regex_intOffset_x = "=";
        String regex_intOffset_y = "=";
        String regex_doubleScale_x = "=";
        String regexOverwrite = "=";
        String regexIntCenter = "=";
        String regexMX = "=";
        String regexQ = "=";
        String regexMode_t = "=";
        String regexcenter_p = "=";
        String regexMode_c = "=";
        String regexMode_c_offset = "=";


        for(int i = 0; i < arrayLineasTextoLocal.size(); i++){

            //2 nov 2017: nuevo req parametro MODE_T=1 de mail "proximos requerimientos
            //Solo afecta a la cadena numerica
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("mode_t")){
                Log.d(xxx, "xxx, Hay una linea que empieza con mode_t y tiene: " +arrayLineasTextoLocal.get(i));
                arrayMode_t = arrayLineasTextoLocal.get(i).split(regexMode_t);
            }

            //Obtener URL del Servidor para almacenar imagen generada
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("web")){
                Log.d(xxx, "xxx, Hay una linea que empieza con web y tiene: " +arrayLineasTextoLocal.get(i));
                stringURLFinal = arrayLineasTextoLocal.get(i).split(regexUrl);
            }
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("user")){
                Log.d(xxx, "xxx, Hay una linea que empieza con user y tiene: " +arrayLineasTextoLocal.get(i));
                arrayStringUser = arrayLineasTextoLocal.get(i).split(regexUser);
            }
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("password")){
                Log.d(xxx, "xxx, Hay una linea que empieza con password y tiene: " +arrayLineasTextoLocal.get(i));
                arrayStringPass = arrayLineasTextoLocal.get(i).split(regexPass);
            }
            //if(arrayLineasTextoLocal.get(i).startsWith("SOR")){
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("sor")){
                Log.d(xxx, "xxx, Hay una linea que empieza con SOR y tiene: " +arrayLineasTextoLocal.get(i));
                arrayStringSOR = arrayLineasTextoLocal.get(i).split(regexSOR);
            }

            /*  ORIGINAL, CAMBIO DE REQ, VER DEFINICION DE VARIABLES AL PRINCIPIO DE LA CLASE
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("overwrite")){
                Log.d(xxx, "xxx, Hay una linea que empieza con overwrite y tiene: " +arrayLineasTextoLocal.get(i));
                //Asignamos la linea directamente, no hay que hacer regex como en las otras
                stringOverwrite = arrayLineasTextoLocal.get(i);
            }  */

            //CON EL NUEVO REQ 25OCT17
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("overwrite")){
                Log.d(xxx, "xxx, Hay una linea que empieza con overwrite y tiene: " +arrayLineasTextoLocal.get(i));
                arrayStringOverwrite = arrayLineasTextoLocal.get(i).split(regexOverwrite);

            }else{
                //No hacemos nada, envia el valor por defecto de stringOverwrite
            }

            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("offset_x")){
                Log.d(xxx, "xxx, Hay una linea que empieza con offset_x, y tiene: " +arrayLineasTextoLocal.get(i));
                array_intOffset_x = arrayLineasTextoLocal.get(i).split(regex_intOffset_x);
            }

            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("offset_y")){
                Log.d(xxx, "xxx, Hay una linea que empieza con offset_y, y tiene: " +arrayLineasTextoLocal.get(i));
                array_intOffset_y = arrayLineasTextoLocal.get(i).split(regex_intOffset_y);
            }

            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("scale_x")){
                Log.d(xxx, "xxx, Hay una linea que empieza con scale_x, y tiene: " +arrayLineasTextoLocal.get(i));
                array_doubleScale_x = arrayLineasTextoLocal.get(i).split(regex_doubleScale_x);
            }

            //2 de noviembre, cambie center por center=, por que si no, cogia center con center_p,
            //ya que ambos cumplen la igualdad de empezar por center
            //if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("center")){
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("center=")){
                Log.d(xxx, "xxx, Hay una linea que empieza con center, y tiene: " +arrayLineasTextoLocal.get(i));
                arrayIntCenter = arrayLineasTextoLocal.get(i).split(regexIntCenter);
                //boolUsarCenter = true;
            }

            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("m_x")){
                Log.d(xxx, "xxx, Hay una linea que empieza con m_x, y tiene: " +arrayLineasTextoLocal.get(i));
                arrayMX = arrayLineasTextoLocal.get(i).split(regexMX);
                //boolMX = true;
            }

            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("q=")){
                Log.d(xxx, "xxx, Hay una linea que empieza con q=, y tiene: " +arrayLineasTextoLocal.get(i));
                arrayQ = arrayLineasTextoLocal.get(i).split(regexQ);
                //boolMX = true;
            }

            //2 nov 2017, Posicionamiento de ficheros en modo proporcional, parametro CENTER_P=nnn, nuevo req del mail proximos requerimientos
            //Afecta a la cadena de numeros y alfanumericos
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("center_p")){
                Log.d(xxx, "xxx, Hay una linea que empieza con center_p, y tiene: " +arrayLineasTextoLocal.get(i));
                arraycenter_p = arrayLineasTextoLocal.get(i).split(regexcenter_p);
                //boolMX = true;
            }


            //6 nov 2017, nuevo req en mail Plan lunes - Modo rotacional
            //Te paso los requerimientos del modo nuevo de rotación:
            //parámetro opcional MODE_C=1
            //Solo aplica al modo numerico
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("mode_c")){
                Log.d(xxx, "xxx, Hay una linea que empieza con mode_c y tiene: " +arrayLineasTextoLocal.get(i));
                arrayMode_c = arrayLineasTextoLocal.get(i).split(regexMode_c);
            }



            //13 nov 2017, nuevo req en mail: offet_h y offset_m, pueden ser positivos o negativos
            //Complementa mode_c, solo aplica al modo numerico
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("offset_h")){
                Log.d(xxx, "xxx, Hay una linea que empieza con offset_h y tiene: " +arrayLineasTextoLocal.get(i));
                String[] array_offset = arrayLineasTextoLocal.get(i).split(regexMode_c_offset);
                offset_h = array_offset[array_offset.length -1];
            }
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("offset_m")){
                Log.d(xxx, "xxx, Hay una linea que empieza con offset_m y tiene: " +arrayLineasTextoLocal.get(i));
                String[] array_offset = arrayLineasTextoLocal.get(i).split(regexMode_c_offset);
                offset_m = array_offset[array_offset.length -1];
            }


            //*********************************
            //23 Enero 2018
            //*********************************
            //23 ene 2018: Nuevo req de email "CUPP Lite - nuevo requerimiento de Cesar", recibido el 14 ene 18
            //Para el color rojo
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("f_r")){
                Log.d(xxx, "xxx, Hay una linea que empieza con f_r y tiene: " +arrayLineasTextoLocal.get(i));
                String[] array_f_r = arrayLineasTextoLocal.get(i).split("=");
                F_R = array_f_r[array_f_r.length -1];
                //Verifico que es un integer valido
                try {
                    int_F_R = Integer.parseInt(F_R);
                    Log.d(xxx, "El valor F_R es valido " + int_F_R);
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                    Log.d(xxx, "El valor F_R NO es valido " + F_R);
                }
            }

            //Para el color verde
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("f_g")){
                Log.d(xxx, "xxx, Hay una linea que empieza con f_g y tiene: " +arrayLineasTextoLocal.get(i));
                String[] array_f_g = arrayLineasTextoLocal.get(i).split("=");
                F_G = array_f_g[array_f_g.length -1];
                try {
                    int_F_G = Integer.parseInt(F_G);
                    Log.d(xxx, "El valor F_G es valido " + int_F_G);
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                    Log.d(xxx, "El valor F_G NO es valido " + F_G);
                }
            }

            //Para el color azul
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("f_b")){
                Log.d(xxx, "xxx, Hay una linea que empieza con f_g y tiene: " +arrayLineasTextoLocal.get(i));
                String[] array_f_b = arrayLineasTextoLocal.get(i).split("=");
                F_B = array_f_b[array_f_b.length -1];
                try {
                    int_F_B = Integer.parseInt(F_B);
                    Log.d(xxx, "El valor F_B es valido " + int_F_B);
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                    Log.d(xxx, "El valor F_B NO es valido " + F_B);
                }
            }

            //*********************************
            //23 Enero 2018
            //*********************************
        }


        //COJONES, quitar esto
        /*
        if(stringURLFinal != null) {
            int i = 0;
            for (String st : stringURLFinal) {
                Log.d(xxx, "xxx Dato en stringURLFinal " + i + " es: " + st);
                urlServidor = stringURLFinal[i];
                i++;
            }
        } */

        //Imprime las coordenadas, solo para pruebas
        /*
        for (int i = 0; i < arrayPojoCoordenadas.size(); i++ ){
            Log.d(xxx, "Coordenada X en arraPojo " + i + " es: " + arrayPojoCoordenadas.get(i).getCoordX()
                            +"\n"
                             +"Coordenada y en arraPojo " + i + " es: " + arrayPojoCoordenadas.get(i).getCoordY());
        } */




        //Imprime la url y la asigna a la variable global
        if(stringURLFinal != null) {
            int i = 0;
            for (String st : stringURLFinal) {
                Log.d(xxx, "xxx Dato en stringURLFinal " + i + " es: " + st);
                urlServidor = stringURLFinal[i];
                i++;
            }
        }

        //Imprime el user y lo asigna a la variable global
        if(arrayStringUser != null) {
            int i = 0;
            for (String usuario : arrayStringUser) {
                Log.d(xxx, "xxx Dato de user en arrayStringUser " + i + " es: " + usuario);
                user = arrayStringUser[i];
                i++;
            }
        }

        //Imprime el password y lo asigna a la variable global
        if(arrayStringPass != null) {
            int i = 0;
            for (String pass : arrayStringPass) {
                Log.d(xxx, "xxx Dato de user en arrayStringUser " + i + " es: " + pass);
                password = arrayStringPass[i];
                i++;
            }
        }


        //Imprime el SOR y lo asigna a la variable global
        if(arrayStringSOR != null) {
            int i = 0;
            for (String sor : arrayStringSOR) {
                Log.d(xxx, "xxx Dato de user en arrayStringSOR " + i + " es: " + sor);
                stringSOR = arrayStringSOR[i];
                i++;
            }
        }

        //Imprime el overwrite y lo asigna a la variable global
        if(arrayStringOverwrite != null) {
            int i = 0;
            for (String overwrite : arrayStringOverwrite) {
                Log.d(xxx, "xxx Dato de user en arrayStringOverwrite " + i + " es: " + overwrite);
                stringOverwrite = arrayStringOverwrite[i];
                i++;
            }
            if(stringOverwrite.equals("1")){
                stringOverwrite = "overwrite";
            }else if (stringOverwrite.equals("0")){
                stringOverwrite = "";
            }else{
                //No hace nada, se usa el valor por defecto, que reescribimos aqui:
                stringOverwrite = "overwrite";
            }
        }

        //Imprime el offset_x y lo asigna a la variable global como integer
        if(array_intOffset_x != null) {
            int i = 0;
            for (String offset_x : array_intOffset_x) {
                Log.d(xxx, "xxx Dato de user en array_intOffset_x " + i + " es: " + offset_x);
                stringOffset_x = array_intOffset_x[i];
                i++;
            }

            try {
                intOffset_x = Integer.parseInt(stringOffset_x);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                Log.d(xxx, "Error de formato de  stringOffset_x, no puede ser tipo int " + stringOffset_x);
            }
        }

        //Imprime el offset_y y lo asigna a la variable global como integer
        if(array_intOffset_y != null) {
            int i = 0;
            for (String offset : array_intOffset_y) {
                Log.d(xxx, "xxx Dato de user en array_intOffset_y " + i + " es: " + offset);
                stringOffset_y = array_intOffset_y[i];
                i++;
            }

            try {
                intOffset_y = Integer.parseInt(stringOffset_y);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                Log.d(xxx, "Error de formato de  stringOffset_y, no puede ser tipo int " + stringOffset_y);
            }
        }

        //Imprime el scale_x y lo asigna a la variable global como double
        if(array_doubleScale_x != null) {
            int i = 0;
            for (String scale : array_doubleScale_x) {
                Log.d(xxx, "xxx Dato de user en array_doubleScale_x " + i + " es: " + scale);
                stringScale_x = array_doubleScale_x[i];
                i++;
            }

            try {
                doubleScale_x = Double.valueOf(stringScale_x);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                Log.d(xxx, "Error de formato de  stringScale_x, no puede ser tipo double " + stringScale_x);
            }
        }


        //Imprime el center y lo asigna a la variable global como integer
        if(arrayIntCenter != null) {
            int i = 0;
            for (String center : arrayIntCenter) {
                Log.d(xxx, "xxx Dato de user en array_intOffset_x " + i + " es: " + center);
                stringIntCenterConfig = arrayIntCenter[i];
                i++;
            }

            try {
                intCenterConfig = Integer.parseInt(stringIntCenterConfig);
                boolUsarCenter = true;
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                Log.d(xxx, "Error de formato de  stringIntCenterConfig, no es un int: " + stringIntCenterConfig);
                Log.d(xxx, "Error de formato de  stringIntCenterConfig, NumberFormatException: " + e.getMessage());
            }
        }

        //Parametro M_X
        if(arrayMX != null) {
            int i = 0;
            for (String m_x : arrayMX) {
                Log.d(xxx, "xxx Dato de user en array_intOffset_x " + i + " es: " + m_x);
                stringMX = arrayMX[i];
                i++;
            }

            try {
                intMX = Integer.parseInt(stringMX);
                boolMX = true;
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                Log.d(xxx, "Error de formato de  stringMX, no es un int: " + stringMX);
                Log.d(xxx, "Error de formato de  stringMX, NumberFormatException: " + e.getMessage());
            }
        }




        //Parametro q
        if(arrayQ != null) {
            int i = 0;
            for (String q : arrayQ) {
                Log.d(xxx, "xxx Dato de user en arrayQ " + i + " es: " + q);
                string_q = arrayQ[i];
                i++;
            }

            try {
                intq = Integer.parseInt(string_q);
                Log.d(xxx, "despues de parsin, intq es: " +intq);

                //Chequeo que no este fuera de rango

                if(intq < 0 || intq > 100){
                    Log.d(xxx, "int q esta fuera de rango, intq es: "  +intq);

                    //asigno 100 por defecto
                    intq = 100;
                }
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                Log.d(xxx, "Error de formato de  string_q, no es un int: " + string_q);
                Log.d(xxx, "Error de formato de  string_q, NumberFormatException: " + e.getMessage());
            }
        }

        //Imprime mode_t y la asigna a la variable global
        if(arrayMode_t != null) {
            int i = 0;
            for (String mode_t : arrayMode_t) {
                Log.d(xxx, "xxx Dato en arrayMode_t " + i + " es: " + mode_t);
                stringMode_t = arrayMode_t[i];
                i++;
            }
        }


        //Parametro intCenter_p
        if(arraycenter_p != null) {
            int i = 0;
            for (String center_p : arraycenter_p) {
                Log.d(xxx, "xxx Dato de user en arraycenter_p " + i + " es: " + center_p);
                stringCenter_p = arraycenter_p[i];
                i++;
            }

            try {
                intCenter_p = Integer.parseInt(stringCenter_p);
                Log.d(xxx, "despues de parsin, intCenter_p es: " +intCenter_p);

            } catch (NumberFormatException e) {
                //e.printStackTrace();
                Log.d(xxx, "Error de formato de  intCenter_p, no es un int: " + intCenter_p);
                Log.d(xxx, "Error de formato de  intCenter_p, NumberFormatException: " + e.getMessage());
            }
        }

        //Imprime mode_c y la asigna a la variable global
        //Solo aplica al modo numerico
        if(arrayMode_c != null) {
            int i = 0;
            for (String mode_c : arrayMode_c) {
                Log.d(xxx, "xxx Dato en arrayMode_c " + i + " es: " + mode_c);
                stringMode_c = arrayMode_c[i];
                i++;
            }
            if(stringMode_c.equals("0") || stringMode_c.equals("1") || stringMode_c.equals("2")){
                //el parametro esta bien
            }else{
                //si no es ni "0" ni "1" le pongo el valor "0" por defecto
                stringMode_c = "0";
            }
        }


        Log.d(xxx, "xxx Variable urlServidor: " +urlServidor
                +"\n"  +"xxx Variable user: " +user
                +"\n"  +"xxx Variable password: " +password
                +"\n"  +"xxx Variable SOR: " +stringSOR
                +"\n"  +"xxx Variable overwrite: " +stringOverwrite
                +"\n"  +"xxx Variable intOffset_x: " +intOffset_x
                +"\n"  +"xxx Variable intOffset_y: " +intOffset_y
                +"\n"  +"xxx Variable doubleScale_x: " +doubleScale_x
                +"\n"  +"xxx Variable boolUsarCenter: " +boolUsarCenter
                +"\n"  +"xxx Variable intCenterConfig: " +intCenterConfig
                +"\n"  +"xxx Variable boolMX: " +boolMX
                +"\n"  +"xxx Variable intMX: " +intMX
                +"\n"  +"xxx Variable intq: " +intq
                +"\n"  +"xxx Variable stringMode_t: " +stringMode_t
                +"\n"  +"xxx Variable intCenter_p: " +intCenter_p
                +"\n"  +"xxx Variable stringMode_c: " +stringMode_c
                +"\n"  +"xxx Variable offset_h: " +offset_h
                +"\n"  +"xxx Variable offset_m: " +offset_m);



        //Modificar siempre arrayPojoCoordenadas y arrayPojoCoordenadasAlfanumerico con offset y scale

        return arrayPojoCoordenadas;
    }//Fin de getCoordenadasN y obtener user y password

    public int getquality(){
        Log.d(xxx, "en getquality, intq es  " + intq);

        return intq;
    }


    //Metodo para modificar las coordenadas X originales de acuerdo a offset_x, y scale_x)
    public float modificarCoordenadaX(float coordInicial, double doubleScale_x, int intOffset_x){
        Log.d(xxx, "En metodo modificarCoordenadaX, coordInicial: " +coordInicial);
        float coordFinal = 0;
        //Math.round(f - 32 / 1.8f)
        coordFinal = Math.round(coordInicial * doubleScale_x);
        coordFinal = coordFinal + intOffset_x;
        Log.d(xxx, "En metodo modificarCoordenadaX, coordFinal: " +coordFinal);
        return coordFinal;
    }

    //Metodo para modificar las coordenadas X originales de acuerdo a offset_x, y scale_x)
    public float modificarCoordenadaY(float coordInicial, int intOffset_y ){
        Log.d(xxx, "En metodo modificarCoordenadaY, coordInicial: " +coordInicial);
        float coordFinal = 0;
        //Math.round(f - 32 / 1.8f)
        coordFinal = coordInicial + intOffset_y;
        Log.d(xxx, "En metodo modificarCoordenadaY, coordFinal: " +coordFinal);
        return coordFinal;
    }


    private void imprimirLineasConfigTxt(List<String> arrayLineasTexto) {

        //Recorro y muestro la lista con el contenido de CONFIG.txt, solo para pruebas
        String[] coordenates;
        String linea;
        for (int i = 0; i < arrayLineasTexto.size(); i++) {
            Log.d(xxx, "en imprimirLineasConfigTxt, CONFIG Linea " + (i + 1) + " contiene: " + arrayLineasTexto.get(i));
            linea = arrayLineasTexto.get(i);
            //Hacemos split de linea, el token es espeacio en blanco como regex: \\s+
            /*
            coordenates = linea.split("\\s+");
            int index = 1;
            for (String dato : coordenates) {
                //Dato tiene cada string de una linea de CONFIG.txt: N*, coordX y CoordY.
                Log.d(xxx, "Dato " + index + " es: " + dato);
                index++;

            } */
            //leerCoordenadasDeConfigTxt(arrayLineasTexto.get(i));
        }
    }

    //ESte metodo separa los numeros de cada linea de config.txt en el array str:
    //str[1] muestra el indice N, el str[2] el valor de la coordenada X, str[3] la coordenada Y
    //str[0] siempre muestra un char vacio.
    //Este metodo es solo para pruebas
    private void leerCoordenadasDeConfigTxt(String linea){
        String line = "This order was32354 placed 343434for 43411 QT ! OK?";
        String regex = "[^\\d]+";
        //String[] str = line.split(regex);
        String[] str = linea.split(regex);
        //String required = "";
        int i = 0;
        for(String st: str){
            Log.d(xxx, "xxx Dato " +i +" es: " +st);
            i++;
        }
    }

    public List<PojoCoordenadas> getCoordenadasN_Original(List<String> arrayLineasTextoLocal){
        imprimirLineasConfigTxt(arrayLineasTextoLocal);
        ArrayList<PojoCoordenadas> arrayPojoCoordenadas = new ArrayList<>();
        String regex = "[^\\d]+";
        for(int i = 0; i < arrayLineasTextoLocal.size(); i++){
            //Solo quiero las lineas que empiezan con N
            if(arrayLineasTextoLocal.get(i).startsWith("N")) {

                //Extrae las coordenadas x e y de cada linea con regex y genera pojo de
                //coordenadas por cada linea y lo guarda en el array de coordenadas
                String[] str = arrayLineasTextoLocal.get(i).split(regex);
                //Recorro y muestro con Log.d el array str
                for (int i2 = 0; i2 < str.length; i2++) {
                    Log.d(xxx, "En metodo generarPojoGenerarUrl, despues del split con regex" + "\n"
                            + "Linea " + i + "\n"
                            + "posicion " + i2 + " tiene: " + str[i2]);
                }
                //Si la linea no tiene digitos, hago un break y continua el loop
                //if(str.length == 0) break;
                PojoCoordenadas pojoCoordenadas = new PojoCoordenadas();
                try {
                    if (str.length > 2) {//Para evitar las lineas blancas y las que no tienen coordenadas y que de ArrayIndexOutOfBoundsException
                        pojoCoordenadas.setCoordX(str[2]);
                        pojoCoordenadas.setCoordY(str[3]);
                        arrayPojoCoordenadas.add(pojoCoordenadas);

                    } else {
                        Log.d(xxx, "En generarPojoGenerarUrl, la linea " + i + " esta vacia o no tiene coordenadas: "
                                + arrayLineasTextoLocal.get(i));

                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.d(xxx, "ArrayIndexOutOfBoundsException:  " + e.getMessage());
                    return null;
                }
            }//Fin de if(arrayLineasTextoLocal.get(i).startsWith("N"))

            //***************************************************************************************
            //***************************************************************************************
            //***************************************************************************************

            //Solo quiero las lineas que empiezan con T
            if(arrayLineasTextoLocal.get(i).startsWith("T")) {

                //Extrae las coordenadas x e y de cada linea con regex y genera pojo de
                //coordenadas por cada linea y lo guarda en el array de coordenadas
                String[] str = arrayLineasTextoLocal.get(i).split(regex);
                //Recorro y muestro con Log.d el array str
                for (int i2 = 0; i2 < str.length; i2++) {
                    Log.d(xxx, "En metodo generarPojoGenerarUrl, despues del split con regex" + "\n"
                            + "Linea " + i + "\n"
                            + "posicion " + i2 + " tiene: " + str[i2]);
                }
                //Si la linea no tiene digitos, hago un break y continua el loop
                //if(str.length == 0) break;
                PojoCoordenadas pojoCoordenadas = new PojoCoordenadas();
                try {
                    if (str.length > 2) {//Para evitar las lineas blancas y las que no tienen coordenadas y que de ArrayIndexOutOfBoundsException
                        pojoCoordenadas.setCoordX(str[2]);
                        pojoCoordenadas.setCoordY(str[3]);
                        arrayPojoCoordenadasAlfanumerico.add(pojoCoordenadas);

                    } else {
                        Log.d(xxx, "En generarPojoGenerarUrl, la linea " + i + " esta vacia o no tiene coordenadas: "
                                + arrayLineasTextoLocal.get(i));

                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.d(xxx, "ArrayIndexOutOfBoundsException:  " + e.getMessage());
                    return null;
                }
            }//Fin de if(arrayLineasTextoLocal.get(i).startsWith("T"))



            //***************************************************************************************
            //***************************************************************************************
            //***************************************************************************************





        }//FIN de for(int i = 0; i < arrayLineasTextoLocal.size(); i++)

        //Este for extrae la URL del servidor, el user y el password que estan en CONFIG.txt
        String[] stringURLFinal = null;
        String[] arrayStringUser = null;
        String[] arrayStringPass = null;
        String[] arrayStringSOR = null;
        String regexUrl = "web=";
        String regexUser = "user=";
        String regexPass = "password=";
        String regexSOR = "SOR=";
        for(int i = 0; i < arrayLineasTextoLocal.size(); i++){
            //Obtener URL del Servidor para almacenar imagen generada
            if(arrayLineasTextoLocal.get(i).startsWith("web")){
                Log.d(xxx, "xxx, Hay una linea que empieza con web y tiene: " +arrayLineasTextoLocal.get(i));
                stringURLFinal = arrayLineasTextoLocal.get(i).split(regexUrl);
            }
            if(arrayLineasTextoLocal.get(i).startsWith("user")){
                Log.d(xxx, "xxx, Hay una linea que empieza con user y tiene: " +arrayLineasTextoLocal.get(i));
                arrayStringUser = arrayLineasTextoLocal.get(i).split(regexUser);
            }
            if(arrayLineasTextoLocal.get(i).startsWith("password")){
                Log.d(xxx, "xxx, Hay una linea que empieza con password y tiene: " +arrayLineasTextoLocal.get(i));
                arrayStringPass = arrayLineasTextoLocal.get(i).split(regexPass);
            }
            if(arrayLineasTextoLocal.get(i).startsWith("SOR")){
                Log.d(xxx, "xxx, Hay una linea que empieza con SOR y tiene: " +arrayLineasTextoLocal.get(i));
                arrayStringSOR = arrayLineasTextoLocal.get(i).split(regexSOR);
            }
            if(arrayLineasTextoLocal.get(i).startsWith("overwrite")){
                Log.d(xxx, "xxx, Hay una linea que empieza con overwrite y tiene: " +arrayLineasTextoLocal.get(i));
                //Asignamos la linea directamente, no hay que hacer regex como en las otras
                stringOverwrite = arrayLineasTextoLocal.get(i);
            }
        }


        //COJONES, quitar esto
        /*
        if(stringURLFinal != null) {
            int i = 0;
            for (String st : stringURLFinal) {
                Log.d(xxx, "xxx Dato en stringURLFinal " + i + " es: " + st);
                urlServidor = stringURLFinal[i];
                i++;
            }
        } */

        //Imprime las coordenadas, solo para pruebas
        /*
        for (int i = 0; i < arrayPojoCoordenadas.size(); i++ ){
            Log.d(xxx, "Coordenada X en arraPojo " + i + " es: " + arrayPojoCoordenadas.get(i).getCoordX()
                            +"\n"
                             +"Coordenada y en arraPojo " + i + " es: " + arrayPojoCoordenadas.get(i).getCoordY());
        } */

        //Imprime la url y la asigna a la variable global
        if(stringURLFinal != null) {
            int i = 0;
            for (String st : stringURLFinal) {
                Log.d(xxx, "xxx Dato en stringURLFinal " + i + " es: " + st);
                urlServidor = stringURLFinal[i];
                i++;
            }
        }

        //Imprime el user y lo asigna a la variable global
        if(arrayStringUser != null) {
            int i = 0;
            for (String usuario : arrayStringUser) {
                Log.d(xxx, "xxx Dato de user en arrayStringUser " + i + " es: " + usuario);
                user = arrayStringUser[i];
                i++;
            }
        }

        //Imprime el password y lo asigna a la variable global
        if(arrayStringPass != null) {
            int i = 0;
            for (String pass : arrayStringPass) {
                Log.d(xxx, "xxx Dato de user en arrayStringUser " + i + " es: " + pass);
                password = arrayStringPass[i];
                i++;
            }
        }


        //Imprime el SOR y lo asigna a la variable global
        if(arrayStringSOR != null) {
            int i = 0;
            for (String sor : arrayStringSOR) {
                Log.d(xxx, "xxx Dato de user en arrayStringSOR " + i + " es: " + sor);
                stringSOR = arrayStringSOR[i];
                i++;
            }
        }


        Log.d(xxx, "xxx Variable urlServidor: " +urlServidor
                +"\n"  +"xxx Variable user: " +user
                +"\n"  +"xxx Variable password: " +password
                +"\n"  +"xxx Variable SOR: " +stringSOR
                +"\n"  +"xxx Variable overwrite: " +stringOverwrite);



        return arrayPojoCoordenadas;
    }//Fin de getCoordenadasN_Original y obtener user y password
}
