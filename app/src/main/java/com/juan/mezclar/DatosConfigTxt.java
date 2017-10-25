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
    String stringOverwrite = "";

    //Parametrros de offset y scale como strings para recuperarlos del CONFIX.txt
    String stringOffset_x="";
    String stringOffset_y="";
    String stringScale_x="";

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

    //Metodo para:
    //Generar array de PojoCoordenadas con las coordenadas x e Y de posicionamiento de imagenes
    //Generar la URL para subir y almacenar la imagen generada a un servidor
    //tambien lee user, password, SOR.
    //public List<PojoCoordenadas> generarPojoGenerarUrl(List<String> arrayLineasTextoLocal){
    public List<PojoCoordenadas> getCoordenadasN(List<String> arrayLineasTextoLocal){
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

        String regexUrl = "=";
        String regexUser = "=";
        String regexPass = "=";
        String regexSOR = "=";
        String regex_intOffset_x = "=";
        String regex_intOffset_y = "=";
        String regex_doubleScale_x = "=";
        for(int i = 0; i < arrayLineasTextoLocal.size(); i++){
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
            if(arrayLineasTextoLocal.get(i).toLowerCase().startsWith("overwrite")){
                Log.d(xxx, "xxx, Hay una linea que empieza con overwrite y tiene: " +arrayLineasTextoLocal.get(i));
                //Asignamos la linea directamente, no hay que hacer regex como en las otras
                stringOverwrite = arrayLineasTextoLocal.get(i);
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
                Log.d(xxx, "Error de formato de  stringOffset_x, no puede ser tipo int " + stringOffset_x);            }
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
                Log.d(xxx, "Error de formato de  stringOffset_y, no puede ser tipo int " + stringOffset_y);            }
        }

        //Imprime el scale_x y lo asigna a la variable global como integer
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


        Log.d(xxx, "xxx Variable urlServidor: " +urlServidor
                +"\n"  +"xxx Variable user: " +user
                +"\n"  +"xxx Variable password: " +password
                +"\n"  +"xxx Variable SOR: " +stringSOR
                +"\n"  +"xxx Variable overwrite: " +stringOverwrite
                +"\n"  +"xxx Variable intOffset_x: " +intOffset_x
                +"\n"  +"xxx Variable intOffset_y: " +intOffset_y
                +"\n"  +"xxx Variable doubleScale_x: " +doubleScale_x);

        //Modificar siempre arrayPojoCoordenadas y arrayPojoCoordenadasAlfanumerico con offset y scale

        return arrayPojoCoordenadas;
    }//Fin de getCoordenadasN y obtener user y password


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
            Log.d(xxx, "Linea " + (i + 1) + " contiene: " + arrayLineasTexto.get(i));
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
