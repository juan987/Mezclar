package com.juan.mezclar;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juan.mezclar.ftpClases.FtpClient;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


//NOTA FINAL: ESTA CLASE ES LA ACTIVIDAD PRINCIPAL DE LA ACTIVIDAD

//Gestion de errores
/*
La App no necesita un interfaz de user, pero sería bueno que informara, a través de un icono en la barra superior del teléfono, del proceso que está realizando:
1)    Un icono cuadrado con un “1” dentro significaría que la App está componiendo la imagen
2)    Un icono cuadrado con un “2” dentro significaría que a imagen ha sido generada
3)    Un icono cuadrado con un “3” dentro significaría que la imagen ha sido generada y se está subiendo al directorio web indicado en el fichero de configuración (en caso de existir).
4)    Un icono cuadrado con un “OK” dentro, significaría que el proceso ha terminado correctamente
5)    Un icono cuadrado con un “E1” dentro significaría que ha habido un error en la generación del JPG
6)    Un icono cuadrado con un “E2” dentro significaría que ha habido un error en la subida del JPG al directorio web

Si la App encuentra un error, pondría el icono E1 o E2 y terminaría.

 */

//Para gestionar imagenes

//11 oct 17, Cesar me pidio que hiciera estos cambios, documento que esta probado y OK
//Prueba boton fab invisible, OK
//Prueba de inicio automatico, OK
//Prueba de NO mostrar la imagen original en la UI, OK
//Prueba de notificaciones con imagenes, sin texto,  OK
//Prueba CONFIG.txt con lineas blancas de por medio o con comentarios, OK
//OJO: si una coordenada es una letra, la aplicacion falla por null pointer
//Prueba quitar todas las notificaciones, lo hago modificando los metodos de notificaciones, no las llamadas

public class MezclarFinal extends AppCompatActivity {
    //String para usar en log.d con el nombre de la clase
    String xxx = this.getClass().getSimpleName();

    private ImageView collageImage;
    private ImageView finalImage;

    TextView textViewErrores;

    //Array para almacenar la secuencia de imagenes a superponer
    char[] arrayImagesSequence;
    char[] arrayImagesSequenceAlphanumeric;
    int sizearrayImagesSequence;
    //String de secuencia numerica de imagenes inicializada con la imagen 0.
    String stringImagesSecuence;
    //Para prueba con el array vacio
    //String stringImagesSecuence = "0";
    //Path a agregar al dir raiz del telefono
    String pathCesaralMagicImageC = "/CesaralMagic/ImageC/";
    //Nombre de la imagen principal sobre la que se superponen las imagenes de o a 9.
    String imagenPrincipal = "origin.jpg";
    //Bitmap que contiene el resultado de la imagen generada
    Bitmap originJpg;
    //array list que contiene datos de coordenada x e y de tipo N obtenidos del fichero CONFIG.txt
    List<PojoCoordenadas> listaCoordenadas;
    //ArrayList para guardar las coordenadas alfanumericas tipo T del fichero CONFIG.txt, es un variable de la clase
    ArrayList<PojoCoordenadas> arrayPojoCoordenadasAlfanumerico = new ArrayList<>();
    //Boolean para discriminar si la secuencia de imagenes recibida es numerica
    boolean booleanSecuenciaNumerica = true;
    //Boolean para discriminar si la secuencia de imagenes recibida es alfanumerica
    boolean booleanSecuenciaRecibidaAlfanumerica = false;



    //Fichero de datos: CONFIG.txt
    //Contiene las coordenadas N1 a N15
    //Contiene la url del servidor ftp
    //Contiene user y password para hacer login en el servidor ftp
    String ficheroConfigTxt = "CONFIG.txt";

    //Fichero para hacer pruebas y corregir fallos
    //String ficheroConfigTxt = "CONFIG_fallo_cesar.txt";

    //Campos de user y password para ftp server
    //user=testx
    //password=test.2017
    //Almacena la url del servidor ftp
    String urlServidor = "";
    //Almacena el user, a partir de version 1.0.1
    String user = "";
    //Almacena la contraseña a partir de version 1.0.1
    String password = "";

    //Almacena el SOR a partir de version 1.0.2
    String stringSOR = "";

    //Almacena el param de config overwrite. Nuevo req el 20oct17.
    String stringOverwrite = "";

/*

    20oct17: nuevo requerimiento
    Fichero de registro de llamadas

    La App, debe registrar en el fichero log.txt que estará en el directorio ImageC, todo el registro de actividad:

    Fecha / hora / cadena numérica empleada / cadena alfanumérica empleada / “OK” o “detalle del error encontrado”

    Por ejemplo, con solo dato numéricos:

            17/10/2017 / 10:24 / 012345678 / /  OK

    Por ejemplo, con solo dato alfanuméricos:

            17/10/2017 / 10:24 / / test /  OK

    Por ejemplo, con datos de ambos tipo, pero dando error:

            17/10/2017 / 10:27 / 123456 / test /  Upload Server “ftp.cesaral.com” not found

    Todas las condiciones de error que puedas localizar, que vayan al fichero de registro
    */
//Variables para el requerimiento del log
    String subDirLogFile = "/CesaralMagic/ImageC/";
    String fechaLog = "";
    String horaLog = "";
    String cadenaNumericaEmpleada = "";
    String cadenaAlphaumericaEmpleada = "";
    String mensaje = "";
    String separador = " / ";
    String indiceCoordNmenor = "";
    String indiceCoordTmenor = "";



    //No se usa la progress bar
    //ProgressBar progressBar;

    //FloatingActionButton fabOnPostExecute;//Para deshabilitar el boton FAB si la ejecucion es completa

    //Fichero de prueba para probar fallo de ArrayIndexOutOfBoundsException
    //String ficheroConfigTxt = "CONFIG[1].txt";

    //Fichero de prueba para probar fallo de URL no valida
    //String ficheroConfigTxt = "CONFIG[1][1].txt";

    //Fichero de prueba para probar fallo reportado por Cesar, cuando hay lineas en blanco en las coordenadas
    //String ficheroConfigTxt = "CONFIG_genera_fallos.txt";

    //Fichero de prueba para probar fallo por lineas de comentarios
    //String ficheroConfigTxt = "CONFIG_genera_fallos_comments.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(xxx, "En el onCreate, Hola " );

        super.onCreate(savedInstanceState);
        //Obtener datos iniciales. Si no hay datos, cerrar la app
        //recuperarIntentConDatosIniciales();

        recuperarAmbasSecuencias();


        //Si no pongo esto, entonces se cuelga la app cuando subo el fichero al servidor ftp. Tendria que usar un asynctask!!!!!
        //Ya estoy usando FtpAsyncTask, comento las lineas del thread policy
        /*
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/



        //Si hay datos, se carga la UI
        setContentView(R.layout.activity_mezclar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Muestr snakbar con lo que ha mandado la app Launch Mezclar
        //Snackbar.make(findViewById(R.id.coordinatorlayout_1), "received: " +stringImagesSecuence, Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();

        //Llamo al metodo desde el Floating button
        //En la ultima version, funciona automaticamente
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //Lo hago invisible, la app funciona de manera automatica, sin boton.
        fab.setVisibility(View.INVISIBLE);

        //No hago nada con el fab, No lo muestro, es invisible
        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                boolean booleanContinuarApp = metodoPrincipal_2();
                if(booleanContinuarApp){
                    //Mantener la app abierta
                    Snackbar.make(view, "Imagen predict.jpg guardada en DCIM/predict", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    //Forzar el cierre de la app por que ha habido un error durante el procesamiento de la imagen
                    //Y antes de ejecutar el FtpAsyncTask
                    //Snackbar.make(view, "Cerrando app debido a un ERROR", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    //Si la app no ha sido abierta desde otra app, Launh Mezclar en mi caso, la cierro automaticamente
                    //enviarNotification("Aplicacion cerrada debido a un error de ejecucion");
                    finish();
                }
            }
        });  */


        collageImage = (ImageView)findViewById(R.id.imageView3);
        textViewErrores = (TextView)findViewById(R.id.textView);

        //progressBar = (ProgressBar) findViewById(R.id.progressbar);

        //No mostramos la progress bar
        //progressBar.setVisibility(View.INVISIBLE);




        //Lanzamos el asynctask de componer imagen
        new ComponerImagenAsyncTask().execute("string1", "string2", "string3");

    }//Fin del onCreate

    //Nuevo requerimiento el 20 oct 2017:
    //Recuperar a la vez el string de secuencia de numeros y
    //el string de secuencia alphanumerica, y seguir el procesamiento,
    //independientemente de que la longitud de los strings de secuencia sea cero o mayor que cero.
    //Al final, la imagen compuesta seria la superposicion con coordenadas N y T caso de existir
    //las dos secuencias: numerica y alfanumerica.
    //Este req no aplica al intent service, que solo coje secuencias numericas
    //ESte metodo recoje ambas secuencias que vienen de la activity ActivityLauncherUI
    private void recuperarAmbasSecuencias(){
        Bundle data = getIntent().getExtras();
        if(data!=null) {
            String stringNumeric = data.getString("KeyName");
            String stringAlphanumeric = data.getString("KeyAlfanumerico");
            arrayImagesSequence = null;
            arrayImagesSequence = stringNumeric.toCharArray();
            arrayImagesSequenceAlphanumeric = null;
            arrayImagesSequenceAlphanumeric = stringAlphanumeric.toCharArray();
            //Muestro lo que he recibido
            Log.d(xxx, "En metodo recuperarAmbasSecuencias, secuencia numerica recibida: ");
            for (char temp : arrayImagesSequence) {
                Log.d(xxx, "En metodo recuperarAmbasSecuencias, numero " +temp);
            }
            Log.d(xxx, "En metodo recuperarAmbasSecuencias, secuencia alphanumerica recibida: ");
            for (char temp : arrayImagesSequenceAlphanumeric) {
                Log.d(xxx, "En metodo recuperarAmbasSecuencias, numero " +temp);
            }
            Log.d(xxx, "En metodo recuperarAmbasSecuencias, longitud de la secuencia numerica recibida: "
                                +arrayImagesSequence.length);
            Log.d(xxx, "En metodo recuperarAmbasSecuencias, longitud de la secuencia alphanumerica recibida: "
                                +arrayImagesSequenceAlphanumeric.length);
            //Inicializar a cero
            booleanSecuenciaNumerica = false;
            booleanSecuenciaRecibidaAlfanumerica = false;

            if(arrayImagesSequence.length !=0){
                booleanSecuenciaNumerica = true;
                sizearrayImagesSequence = arrayImagesSequence.length;
                cadenaNumericaEmpleada = stringNumeric;
            }
            if(arrayImagesSequenceAlphanumeric.length !=0){
                booleanSecuenciaRecibidaAlfanumerica = true;
                cadenaAlphaumericaEmpleada = stringAlphanumeric;
            }
            if(!booleanSecuenciaNumerica) cadenaNumericaEmpleada = "No data";
            if(!booleanSecuenciaRecibidaAlfanumerica) cadenaAlphaumericaEmpleada = "No data";
            Log.d(xxx, "En metodo recuperarAmbasSecuencias, booleanSecuenciaNumerica: " +booleanSecuenciaNumerica);
            Log.d(xxx, "En metodo recuperarAmbasSecuencias, booleanSecuenciaRecibidaAlfanumerica: " +booleanSecuenciaRecibidaAlfanumerica);


        }else{
            //No hay datos, finish
            finish();
        }
    }

    //Metodo que recupera los datos recibidos en un intent lanzado por otra aplicacion,
    //por ejemplo, Launch Mezclar.
    //Si el intent es nulo, o no hay datos, la app se cierra automaticamente
    //public String textoSnackBarInicial;
    private void recuperarIntentConDatosIniciales(){
        Bundle data = getIntent().getExtras();
        if(data!=null){
            String myString = data.getString("KeyName");
            //Hay que chequear myString para que no lanze el toast with null cuando lanzo la app desde el movil
            if(myString!=null && !myString.isEmpty()) {
                //Copiamos la secuencia de imagenes recibidas
                stringImagesSecuence = null;
                stringImagesSecuence = myString;
                //Toast.makeText(this, myString, Toast.LENGTH_SHORT).show();

                Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Datos de Launch Mezclar: " + stringImagesSecuence);
                //Muestro el string character a character
                for(int i = 0; i < stringImagesSecuence.length(); i++) {
                    Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Caracter " +i +":" + stringImagesSecuence.charAt(i));
                }

                //Convertir el string de secuencia de imagenes en un array de secuencia de imagenes, character a character
                arrayImagesSequence = stringImagesSecuence.toCharArray();
                //Lo muestro con
                for (char temp : arrayImagesSequence) {
                    Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Caracter " +temp);
                }

                sizearrayImagesSequence = arrayImagesSequence.length;
                Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, numero de digitos recibidos: " +sizearrayImagesSequence);

                booleanSecuenciaNumerica = true;
                booleanSecuenciaRecibidaAlfanumerica = false;
                //OK, la app continua con la secuencia numerica

            }else if(data.getString("KeyAlfanumerico")!=null && !data.getString("KeyAlfanumerico").isEmpty()) {
                //Seha recibido una secuencia de imagenes alfanumerica
                myString = data.getString("KeyAlfanumerico");
                //Copiamos la secuencia de imagenes recibidas
                stringImagesSecuence = null;
                stringImagesSecuence = myString;
                //Toast.makeText(this, myString, Toast.LENGTH_SHORT).show();

                Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Datos de Launch Mezclar alfanumericos: " + stringImagesSecuence);
                //Muestro el string character a character
                for(int i = 0; i < stringImagesSecuence.length(); i++) {
                    Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Caracter alfanumerico" +i +":" + stringImagesSecuence.charAt(i));
                }

                //Convertir el string de secuencia de imagenes en un array de secuencia de imagenes, character a character
                arrayImagesSequence = stringImagesSecuence.toCharArray();
                //Lo muestro con
                for (char temp : arrayImagesSequence) {
                    Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Caracter alfanumerico" +temp);
                }

                sizearrayImagesSequence = arrayImagesSequence.length;
                Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, numero de digitos recibidos: " +sizearrayImagesSequence);

                booleanSecuenciaNumerica = false;
                booleanSecuenciaRecibidaAlfanumerica = true;

                //OK, la app continua con la secuencia alfanumerica

            } else{//Salta aqui si no hay datos en el intent
                Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Datos de Launch Mezclar: No hay datos");
                //Datos fake para probar
                //Si la app no ha sido abierta desde otra app, Launh Mezclar en mi caso, la cierro automaticamente
                //this.finish();
                finish();

            }

        }else{//Salta aqui si recibe nulo en el intent
            Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Datos de Launch Mezclar: NULL 2 del else");
            //Si la app no ha sido abierta desde otra app, Launh Mezclar en mi caso, la cierro automaticamente
            //this.finish();
            finish();

        }

    }//Fin de recuperarIntentConDatosIniciales

    //Array numerico para ordenar arrayImagesSequence segun el algoritmo proporcionado por cesar
    int[] gOrigen;
    int[] gOriginFinal;

    //Coordenadas globales para colocar la imagen transparente sobre origin.jpg
    private float xFloat;
    private float yFloat;

    //Tiene que ser un campo de la clase para poder usarla en el OnPOst de un asynctask
    //Esta variable tiene el resultado final de componer la imagen
    Bitmap mergedImages = null;

    //Metodo final y OK
    ObtenerImagen obtenerImagen;
    private boolean metodoPrincipal_2(){
        Log.d(xxx, "En metodoPrincipal_2");


        //progressBar.setVisibility(View.VISIBLE); //To Hide ProgressBar

        //Lanzar notificacon de inicio de lageneracion de la imagen
        enviarNotificationConNumero("1");


        //Chequeo el array de secuencia de imagenes: si es null o esta vacio, termina el programa

        if (arrayImagesSequence != null) {
            //Verificacion del tamaño del array antes de version 1.0.2 solo era ver si estaba vacio
            //if (arrayImagesSequence.length == 0) {
            //A partir de la version 1.0.2, se verifica que la longitud del array es igual a 16,
            //si no, el algoritmo de ordenacion no funciona y la app casca


            /*
            if (arrayImagesSequence.length != 16) {
                //Hay un error, terminamos la ejecucion he informamos con una notificacion
                enviarNotification("Error: el array de imagenes esta vacio o no tiene longitud 16, saliendo de la aplicacion");
                enviarNotificationConNumero("E1");
                Log.d(xxx, "En metodoPrincipal_2, arrayImagesSequence.length != 16, salimos de la app");

                return false;
            }else{
                //El array de sequencia existe, continuamos
            }
            */

            //Seguimos

        }else
        {
            enviarNotification("Error: el array de imagenes es null, saliendo de la aplicacion");
            enviarNotificationConNumero("E1");
            metodoMostrarError("E1", "Sequence of images is NULL");
            Log.d(xxx, "En metodoPrincipal_2, arrayImagesSequence es null, salimos de la app");

            return false;
        }



        //**************************************************************************************************
        //REQ: Gestion de configuraciones multiples recibido el 23-10-17
        //obtener getActiveDirectory de la clase ConfiguracionesMultiples
        ConfiguracionesMultiples configuracionesMultiples = new ConfiguracionesMultiples(MezclarFinal.this);
        //Prueba, leer los sub directorios que cuelgan de, Prueba OK
        /*
        List<String> subDirs = configuracionesMultiples.getSubDirDeDirCesaralMagicImageC();
        for (int i=0; i < subDirs.size(); i++){
            Log.d(xxx, "metodoPrincipal_2, sub directorio es: " +subDirs.get(i));
        }
        */

        //Leer el directorio activo del share preferences, tb en IntentServiceMagic
        pathCesaralMagicImageC = configuracionesMultiples.getActiveDirectory();
        //El directorio activo de la app es:
        Log.d(xxx, "En metodoPrincipal_2, el directorio activo es: " +pathCesaralMagicImageC);




        //**************************************************************************************************

        //Obtener todas las lineas del fichero CONFIG.txt en el dir del dispositivo: pathCesaralMagicImageC
        LeerFicheroTxt leerFicheroTxt = new LeerFicheroTxt(MezclarFinal.this);
        //arrayLineasTexto contiene todas las lineas de CONFIG.txt
        List<String> arrayLineasTexto = leerFicheroTxt.getFileContentsLineByLineMethod(pathCesaralMagicImageC + ficheroConfigTxt);


        if(arrayLineasTexto == null){
            //Original
            //Hay un error, terminamos la ejecucion he informamos con una notificacion
            //enviarNotification("Error 1 al recuperar CONFIG.txt, saliendo de la aplicacion");
            //enviarNotificationConNumero("E1");
            //metodoMostrarError("E1", "Error reading file: CONFIG.txt");
            //Log.d(xxx, "En metodoPrincipal_2, arrayLineasTexto es null, salimos de la app");

            //Con el nuevo req:
            //Hay un error, terminamos la ejecucion he informamos con una notificacion
            enviarNotification("Error 1 al recuperar CONFIG.txt del dir:  +" +pathCesaralMagicImageC +", saliendo de la aplicacion");
            enviarNotificationConNumero("E1");
            metodoMostrarError("E1", "Error reading file: CONFIG.txt from dir: " +pathCesaralMagicImageC);
            Log.d(xxx, "En metodoPrincipal_2, arrayLineasTexto es null, salimos de la app");

            //No sabemos si el error es por que no existe un dir diferente a "/CesaralMagic/ImageC/".
            //En cualquier caso, volvemos al dir por defecto para poder escribir algo en el log.txt del dir por defecto
            pathCesaralMagicImageC = "/CesaralMagic/ImageC/";

            return false;
        }

        if(arrayLineasTexto.isEmpty()){
            //Hay un error, terminamos la ejecucion he informamos con una notificacion
            enviarNotification("Error 1 al recuperar CONFIG.txt, saliendo de la aplicacion");
            enviarNotificationConNumero("E1");
            metodoMostrarError("E1", "File: CONFIG.txt is empty");
            Log.d(xxx, "En metodoPrincipal_2, arrayLineasTexto esta vacio, salimos de la app");

            return false;
        }

        /*
        //Recorro y muestro la lista con el contenido de CONFIG.txt, solo para pruebas
        String[] coordenates;
        String linea;
        for (int i=0; i < arrayLineasTexto.size(); i++){
            Log.d(xxx, "Linea "  +(i+1) +" contiene: " +arrayLineasTexto.get(i));
            linea = arrayLineasTexto.get(i);
            //Hacemos split de linea, el token es espeacio en blanco como regex: \\s+
            coordenates = linea.split("\\s+");
            int index = 1;
            for(String dato : coordenates){
                //Dato tiene cada string de una linea de CONFIG.txt: N*, coordX y CoordY.
                Log.d(xxx, "Dato " +index +" es: " +dato);
                index++;

            }
            leerCoordenadasDeConfigTxt(arrayLineasTexto.get(i));
        }
        */




        //Leer coordenadas N y T, URL, user, password y SOR del array de lineas obtenido del fichero CONFIG.txt
        listaCoordenadas = generarPojoGenerarUrl(arrayLineasTexto);

        //Recorro he imprimo los datos de listaCoordenadas
        for(int i = 0; i < listaCoordenadas.size(); i++){
            Log.d(xxx, "en metodoPrincipal_2, lista de coordenadas N, posicion: " +i + ", CoordX= " +listaCoordenadas.get(i).getCoordX());
            Log.d(xxx, "en metodoPrincipal_2, lista de coordenadas N, posicion: " +i + ", CoordY= " +listaCoordenadas.get(i).getCoordY());
        }

        //Caso de try/ctach para ArrayIndexOutOfBoundsException
        if(listaCoordenadas == null){
            //Este error indica que alguna coordenada x, y no es un numero valido, alomejor es
            //un error tipografico, como poner una letra en vez de un digito.
            enviarNotification("Error ArrayIndexOutOfBoundsException, saliendo de la aplicacion");
            enviarNotificationConNumero("E1");
            metodoMostrarError("E1", "Error reading coordinates type N");
            Log.d(xxx, "En metodoPrincipal_2, listaCoordenadas == null, salimos de la app");
            return false;
        }

        if(listaCoordenadas.isEmpty()){
            //Hay un fallo en CONFIG text y no se han leido las coordenadas
            //Enviar notificacion de error y cerrar programa
            //enviarNotification("Error al recuperar coordenadas, saliendo de la aplicacion");
            //enviarNotificationConNumero("E1");
            Log.d(xxx, "En metodoPrincipal_2, listaCoordenadas N es Empty, seguimos, la lista es opcional");
            //Como todos los parametro son opcionales, seguimos aunque No haya coordenadas tipo N
            //NO devolvemos nada, seguimos con la ejecucion.
            //Lo hice el 19 oct 17, para cumplimentar el req: todos los parametros de
            // CONFIG.txt son opcionales
            //return false;


        }

        //Chequear si la url de CONFIG.txt es valida
        /*
        11 octubre 2017: No hago ningun chequeo de URL. Si el ftp es incorrecto,
        se recibira un fallo de no se pudo resolver el host: UnknownHostException
         */
        /* Lo dejo comentado
        if( URLUtil.isValidUrl(urlServidor)){
            //La URL del servidor es valida
            Log.d(xxx, "URL del servidor valida: " +urlServidor);


        }else{
            //La URL del servidor NO es valida
            enviarNotification("Error URL invalida, saliendo de la aplicacion");
            Log.d(xxx, "URL del servidor NO valida: " +urlServidor);
            return false;
        } */
        //FIN de Leer coordenadas y URL del array de lineas obtenido del fichero CONFIG.txt


        //Obtener la imagen origin.jpg como un bitmap
        //Si la imagen origin.jpg no existe, entonces buscamos con el nombre origin.xjpg, implementado en version 1.0.2
        obtenerImagen = new ObtenerImagen(MezclarFinal.this);
        //Bitmap originJpg = obtenerImagen.getImagenMethod(pathCesaralMagicImageC + imagenPrincipal);
        originJpg = obtenerImagen.getImagenMethod(pathCesaralMagicImageC + imagenPrincipal);
        if(originJpg == null){//No encuentra origin.jpg
            //Buscamos origin.xjpg
            Log.d(xxx, "No existe origin.jpg, buscamos origin.xjpg");
            originJpg = obtenerImagen.getImagenMethod(pathCesaralMagicImageC + "origin.xjpg");
        }
        if(originJpg == null){
            //Hay un error, terminamos la ejecucion he informamos con una notificacion
            enviarNotification("Error al recuperar origin.jpg, saliendo de la aplicacion");
            enviarNotificationConNumero("E1");
            metodoMostrarError("E1", "File origin does not exist");
            Log.d(xxx, "En metodoPrincipal_2, originJpg == null, salimos de la app");
            return false;
        }
        //NO se muestra origin.jpg en la UI, requerimiento de Cesar
        //ImageView imageView = (ImageView) findViewById(R.id.imageView);
        //imageView.setImageBitmap(originJpg);


        //Instruccion para cargar directamente de la memoria una imagen
        //imageView.setImageBitmap(BitmapFactory.decodeFile(pathToPicture));

        //

        //***************************************************************************************
        //Codigo viejo, antes del 12 de octubre: o se ejecutaba solo la secuencia numerica
        //o la alphanumerica, pero no las dos
        /*
        if(stringSOR.equals("") || booleanSecuenciaRecibidaAlfanumerica){
            //NO hay string SOR, NO HAY que ordenar la secuencia de imagenes recibida, seguimos
            Log.d(xxx, "En metodoPrincipal_2, NO hay parametro SOR o se ha recibido una secuencia alfanumerica, seguimos");

        }else{
            Log.d(xxx, "En metodoPrincipal_2, Hay parametro SOR, se ejecuta metodo ejecutarConParametroSor");

            if(!ejecutarConParametroSor()){
                //Ha habido un problema con la ordenacion, salir del programa
                //enviarNotificationConNumero("E1");
                //metodoMostrarError("E1", "Error in ordering algorithm for SOR parameter");
                Log.d(xxx, "En metodoPrincipal_2, Error en metodo ejecutarConParametroSor, salimos de la app");
                //Me faltaba esta linea
                return false;
            }

        }


        if(booleanSecuenciaNumerica){
            //secuencia numerica recibida, se ejecuta loopPrincipalImagenesTipoN
            if(loopPrincipalImagenesTipoN()){
                //Ejecucion correcta, seguimos
            }else{
                //Hay un fallo en el loop principal de numerico, cerramos la activity
                return false;
            }
        }else{
            //secuencia alfanumerica recibida, se ejecuta loopPrincipalImagenesTipoT
            if(loopPrincipalImagenesTipoT()){
                //Ejecucion correcta, seguimos
            }else{
                //Hay un fallo en el loop principal de alfanumerico, cerramos la activity
                return false;
            }
        }
        */
        //***************************************************************************************




        //Nuevo requerimiento el 20 oct 2017:
        //Recuperar a la vez el string de secuencia de numeros y
        //el string de secuencia alphanumerica, y seguir el procesamiento,
        //independientemente de que la longitud de los strings de secuencia sea cero o mayor que cero.
        //Al final, la imagen compuesta seria la superposicion con coordenadas N y T caso de existir
        //las dos secuencias: numerica y alfanumerica.

        //Chequeamos si hay que hacer ordenacion con el parametro SOR
        if(stringSOR.equals("")){
            //NO hay string SOR, NO HAY que ordenar la secuencia de imagenes recibida, seguimos
            Log.d(xxx, "En metodoPrincipal_2, NO hay parametro SOR o se ha recibido una secuencia alfanumerica, seguimos");

        }else{
            Log.d(xxx, "En metodoPrincipal_2, Hay parametro SOR, se ejecuta metodo ejecutarConParametroSor");
            if(booleanSecuenciaNumerica) {
                Log.d(xxx, "En metodoPrincipal_2, Hay parametro SOR y booleanSecuenciaNumerica=true, " +
                        "se ejecuta metodo ejecutarConParametroSor");
                if (!ejecutarConParametroSor()) {
                    //Ha habido un problema con la ordenacion, salir del programa
                    //enviarNotificationConNumero("E1");
                    //metodoMostrarError("E1", "Error in ordering algorithm for SOR parameter");
                    Log.d(xxx, "En metodoPrincipal_2, Error en metodo ejecutarConParametroSor, salimos de la app");
                    //Me faltaba esta linea
                    return false;
                }
            }

        }

        //Ejecutamos los loops
        if(booleanSecuenciaNumerica){
            //secuencia numerica recibida, se ejecuta loopPrincipalImagenesTipoN
            if(loopPrincipalImagenesTipoN()){
                //Ejecucion correcta, seguimos
            }else{
                //Hay un fallo en el loop principal de numerico, cerramos la activity
                return false;
            }
        }
        if(booleanSecuenciaRecibidaAlfanumerica){
            //cambiamos la variable para ejecutar con arrayImagesSequence el loopPrincipalImagenesTipoT
            arrayImagesSequence = arrayImagesSequenceAlphanumeric;
            //secuencia alfanumerica recibida, se ejecuta loopPrincipalImagenesTipoT
            if(loopPrincipalImagenesTipoT()){
                //Ejecucion correcta, seguimos
            }else{
                //Hay un fallo en el loop principal de alfanumerico, cerramos la activity
                return false;
            }
        }


        //FIN Nuevo requerimiento el 20 oct 2017:
        //**************************************************************************************


        //Ejecucion correcta, guardar imagen en la memoria externa del dispoositivo
        GuardarImagenFinal guardarImagenFinal = new GuardarImagenFinal(MezclarFinal.this, mergedImages);


        //Nuevo req el 20oct17: parametro overwrite en fichero CONFIG
        if(stringOverwrite.equals("overwrite")){
            //la imagen generada se llama: predict.jpg, comportamiento por defecto

        }else{//Componemos el nombre de la foto incluyendo dia y hora
            Date date = new Date();
            //El formato de la fecha para el fichero sera como aparece aqui:
            //predict_ddhhmmss.jpg

            SimpleDateFormat sdf2 = new SimpleDateFormat("ddHHmmss");
            String fechaDeLaFoto = sdf2.format(date);
            Log.d(xxx, "metodoPrincipal_2 La fecha del fichero predict es: " +fechaDeLaFoto);
            nombreFicheroJpg = "predict_" +fechaDeLaFoto +".jpg";
            Log.d(xxx, "metodoPrincipal_2 hay parametro overwrite, nombre del fichero predict es: " +nombreFicheroJpg);
        }


        //Guardar imagen el directorio pictures/predict
        //No hace falta, guardo directamente en DCIM/predict
        /*
        if (!guardarImagenFinal.guardarImagenMethod(Environment.DIRECTORY_PICTURES, "/predict/", "predict.jpg")){
            //Ha habido un error al guardar la imagen, devolver false
            enviarNotification("Error guardando imagen predict" +" ,saliendo de la aplicacion");
            return false;
        } */

        //Guardar imagen en el directorio DCIM/predict
        if (!guardarImagenFinal.guardarImagenMethod(Environment.DIRECTORY_DCIM, "/predict/", nombreFicheroJpg)){
            //Ha habido un error al guardar la imagen, devolver false
            enviarNotification("Error guardando imagen predict" +", saliendo de la aplicacion");
            enviarNotificationConNumero("E1");
            metodoMostrarError("E1", "Error when saving image predict to external storage");
            Log.d(xxx, "En metodoPrincipal_2, Ha habido un error al guardar la imagen compuesta, salimos de la app");

            return false;
        }

        //Hemos terminado
        enviarNotificationConNumero("2");
        return true;
    }//Fin de metodoPrincipal_2
    //Nombre de la imagen compuesta a guardar y a enviar con ftp
    String nombreFicheroJpg = "predict.jpg";


    private boolean loopPrincipalImagenesTipoN(){
        //Loop principal de la aplicacion
        Bitmap imagenParaSuperponerConOrigin;


        for(int i = 0; i < arrayImagesSequence.length; i++) {
            Log.d(xxx, "metodo loopPrincipalImagenesTipoN, mezclando imagen: " +i);
            enviarNotification("mezclando imagen: " +i);
            enviarNotificationConNumero("1");
            //Obtener la imagen a superponer como un bitmap
            imagenParaSuperponerConOrigin = obtenerImagen.getImagenMethod(pathCesaralMagicImageC
                    +arrayImagesSequence[i]+".bmp");
            if(imagenParaSuperponerConOrigin == null){//No encuentra la imagen con extension .bmp
                //Buscamos la imagen a superponer con extension .xbmp
                Log.d(xxx, "metodo loopPrincipalImagenesTipoN, No existe la imagen a superponer: " +i +"con extension .bmp, buscamos con extension .xbmp");
                imagenParaSuperponerConOrigin = obtenerImagen.getImagenMethod(pathCesaralMagicImageC
                        +arrayImagesSequence[i]+".xbmp");
            }
            if(imagenParaSuperponerConOrigin == null){
                //Hay un error, terminamos la ejecucion he informamos con una notificacion
                enviarNotification("Error al recuperar imagen pequeña numero: " +i +", saliendo de la aplicacion");
                enviarNotificationConNumero("E1");
                metodoMostrarError("E1", "Error when getting image file from external storage");
                Log.d(xxx, "metodo loopPrincipalImagenesTipoN, fallo con imagen 0-9 jpg, imagenParaSuperponerConOrigin == null, salimos de la app");

                //Acabamos la ejecucion
                return false;
            }else{

                //Modificar la imagen a superponer: pixels blancos son convertidos a transparentes con channel alpha
                imagenParaSuperponerConOrigin = changeSomePixelsToTransparent(imagenParaSuperponerConOrigin);
                //Leer las coordenadas de prueba
                //leerCoordenadasDeSuperposicion(i);



                //Leere las coordenadas reales obtenidas del fichero CONFIG.txt
                //Siempre chequeo que i no sea mayor o igual que la lista de coordenadas N, por si acaso
                //el fichero CONFIG.txt no tiene las 16 coordenadas N sino un numero menor.
                if(i >= listaCoordenadas.size()){
                    //Modificacion el 20 oct 2017:
                    //Nuevo requerimiento: ahora se admite que el indice del array de numeros sea mayor
                    //que el de coordenadas N.
                    //No se lanza error, se hace el loop hasta esta condicion, si existe,
                    //y solo se superponen las imagenes hasta que no se cumpla esta condicion,
                    //cuando indice del array de numeros sea mayor que el de coordenadas N
                    //Nuevo req 28oct17: fichero de log
                    //informar cuando esto ocurre al log
                    escribirDatosEnLog("index of numeric string > index of N coordenates");
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, No hay fallo, fin del loop tipo N debido a");
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, ........./index of nemeric string > index of N coordenates....,");
                    break;//finaliza el loop


                    //Dejo el codigo original comentado
                    //enviarNotification("Error en indice de coordenadas, saliendo de la aplicacion");
                    //enviarNotificationConNumero("E1");
                    //metodoMostrarError("E1", "Error with index in array of coordenates N");
                    //Log.d(xxx, "metodo loopPrincipalImagenesTipoN, Error en indice de coordenadas, salimos de la app");
                    //return false;//Cerrar aplicacion y evitar un null pointer
                }


                //Para corregir fallos de null, OJO OJO OJO
                /*
                if(listaCoordenadas.get(i).getCoordX() == null || listaCoordenadas.get(i).getCoordY() == null){
                    //No lee los valores null
                }else{
                    xFloat = Float.parseFloat(listaCoordenadas.get(i).getCoordX());
                    yFloat = Float.parseFloat(listaCoordenadas.get(i).getCoordY());
                } */
                xFloat = Float.parseFloat(listaCoordenadas.get(i).getCoordX());
                yFloat = Float.parseFloat(listaCoordenadas.get(i).getCoordY());

                //Chequear que xFloat y yFloat son validos, si no, cerrar el programa
                //Float.isNaN retorna true si no es un numero
                if(Float.isNaN(xFloat) || Float.isNaN(yFloat)){
                    enviarNotification("Error, coordenadas no son un numero valido, saliendo de la aplicacion");
                    enviarNotificationConNumero("E1");
                    metodoMostrarError("E1", "Error with coordenates N: some coordenate is not a number");
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, Error en coordenadas x o y no son un numero, revisar CONFIG.txt, salimos de la app");

                    return false;//Cerrar aplicacion y evitar fallo en el procesamiento
                }


                //Mezclar la imagen pequeña con origin.jpg en las coordenada que corresponden en CONGIG.txt
                mergedImages = createSingleImageFromMultipleImagesWithCoord(originJpg, imagenParaSuperponerConOrigin,
                        xFloat, yFloat);
                //En cada pasada, originJpg se tiene que refrescar con las imagenes mezcladas.
                originJpg = mergedImages;
                if(mergedImages != null) {
                    //Comando de prueba. Comentar esta linea en la version final
                    //collageImage.setImageBitmap(mergedImages);
                }else{
                    //Ha habido un error al mezclar las imagenes
                    enviarNotification("Error mezclando imagen: " +i  +", saliendo de la aplicacion");
                    enviarNotificationConNumero("E1");
                    metodoMostrarError("E1", "Error mixing images");
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, mergedImages es null, no se ha generado la imagen, salimos de la app");

                    return false;

                }
                //
            }

        }//Fin del loop principal

        return true;
    }//FIN de loopPrincipalImagenesTipoN


    private boolean loopPrincipalImagenesTipoT(){
        //Loop principal de la aplicacion

        Log.d(xxx, "metodo loopPrincipalImagenesTipoT, arrayImagesSequence inicial tiene: " +arrayImagesSequence.toString());

        Character character;
        String charDeLaSecuenciaRecibida = "";
        String soloCaracteresValidos = "";
        //*******************************************************************************
        //Revisamos la secuencia alphanumerica para descartar caracteres prohibidos.
        //En esta version solo se aceptan letras, menos la ñ, en mayusculas y minusculas, y digitos 0-9.
        for(int i = 0; i < arrayImagesSequence.length; i++) {
            Log.d(xxx, "metodo loopPrincipalImagenesTipoT, revisando el array de secuencia alfanumerica");

            //Convertir el caracter de la secuencia alfanumerica para usar el metodo matches con regex de string
            character = (Character) arrayImagesSequence[i];
            charDeLaSecuenciaRecibida = character.toString();
            //Generar el nombre de la imagen a utilizar para la mezcla
            if (charDeLaSecuenciaRecibida.matches("[a-z]")) {
                soloCaracteresValidos += charDeLaSecuenciaRecibida;
            } else if (charDeLaSecuenciaRecibida.matches("[A-Z]")) {
                soloCaracteresValidos += charDeLaSecuenciaRecibida;

            } else if (charDeLaSecuenciaRecibida.matches("[0-9]")) {
                soloCaracteresValidos += charDeLaSecuenciaRecibida;

            }
        }
        Log.d(xxx, "metodo loopPrincipalImagenesTipoT, soloCaracteresValidos tiene: " +soloCaracteresValidos);
        arrayImagesSequence = soloCaracteresValidos.toCharArray();
        Log.d(xxx, "metodo loopPrincipalImagenesTipoT, arrayImagesSequence final tiene: " +arrayImagesSequence.toString());
        Log.d(xxx, "metodo loopPrincipalImagenesTipoT, arrayImagesSequence final tiene una longitud de: " +arrayImagesSequence.length);
        if(arrayImagesSequence.length == 0){
            enviarNotification("Error en la secuencia de imagenes alfanumerica: caracteres NO validos");
            enviarNotificationConNumero("E1");
            metodoMostrarError("E1", "Not valid characters in alphanumeric array");
            Log.d(xxx, "metodo loopPrincipalImagenesTipoT, Error en la secuencia de imagenes alfanumerica: caracteres NO validos");

            //Acabamos la ejecucion
            return false;
        }




        //*******************************************************************************





        Bitmap imagenParaSuperponerConOrigin;
        //Los nombres de los ficheros para mezclar seran F1_ +letra o numero del array de secuencia +indice +.bmp o x.bmp
        //Ejemplos:
        /*
        a)    Si la letra es mayúscula, los ficheros gráficos que se utilizarán para la superposición son:

        F1_A1.bmp             o con extensión “xbmp”
        F1_B1.bmp             o con extensión “xbmp”
        …
        F1_Z1.bmp             o con extensión “xbmp”

        b)   Si la letra es minúscula, los ficheros gráficos que se utilizarán para la superposición son:

        F1_A2.bmp            o con extensión “xbmp”
        F1_B2.bmp             o con extensión “xbmp”
        …
        F1_Z2.bmp             o con extensión “xbmp”

        c)    Si el carácter es un número, los ficheros gráficos que se utilizarán para la superposición es el:

        F1_0.bmp              o con extensión “xbmp”
        F1_1.bmp              o con extensión “xbmp”
        …
        F1_9.bmp              o con extensión “xbmp”


         */
        String prefijoNombreFile = "F1_";

        charDeLaSecuenciaRecibida = "";

        for(int i = 0; i < arrayImagesSequence.length; i++) {
            Log.d(xxx, "metodo loopPrincipalImagenesTipoT, mezclando imagen: " +i);
            prefijoNombreFile = "F1_";

            //Convertir el caracter de la secuencia alfanumerica para usar el metodo matches con regex de string
            character = (Character)arrayImagesSequence[i];
            charDeLaSecuenciaRecibida = character.toString();
            //Generar el nombre de la imagen a utilizar para la mezcla
            if(charDeLaSecuenciaRecibida.matches("[a-z]")){
                prefijoNombreFile += charDeLaSecuenciaRecibida.toUpperCase() +2;
            }else if (charDeLaSecuenciaRecibida.matches("[A-Z]")){
                prefijoNombreFile += charDeLaSecuenciaRecibida.toUpperCase() +1;

            }else if(charDeLaSecuenciaRecibida.matches("[0-9]")){
                prefijoNombreFile += charDeLaSecuenciaRecibida.toUpperCase();
            }else{
                //Si llega aqui, es por que hay algun character que
                Log.d(xxx, "metodo loopPrincipalImagenesTipoT, OJO, hay un caracter prohibido en la secuencia numerica");

            }


            enviarNotification("mezclando imagen: " +i);
            enviarNotificationConNumero("1");
            //Obtener la imagen a superponer como un bitmap
            imagenParaSuperponerConOrigin = obtenerImagen.getImagenMethod(pathCesaralMagicImageC
                    +prefijoNombreFile +".bmp");
            if(imagenParaSuperponerConOrigin == null){//No encuentra la imagen con extension .bmp
                //Buscamos la imagen a superponer con extension .xbmp
                Log.d(xxx, "metodo loopPrincipalImagenesTipoT, No existe la imagen a superponer: " +i +"con extension .bmp, buscamos con extension .xbmp");
                imagenParaSuperponerConOrigin = obtenerImagen.getImagenMethod(pathCesaralMagicImageC
                        +prefijoNombreFile +".xbmp");
            }
            if(imagenParaSuperponerConOrigin == null){
                //Hay un error, terminamos la ejecucion he informamos con una notificacion
                enviarNotification("Error al recuperar imagen pequeña alfanumerica numero: " +i +", saliendo de la aplicacion");
                enviarNotificationConNumero("E1");
                metodoMostrarError("E1", "Error in recovering alphanumeric image from external storage");
                Log.d(xxx, "metodo loopPrincipalImagenesTipoT, fallo con imagen 0-9 jpg, imagenParaSuperponerConOrigin == null, salimos de la app");

                //Acabamos la ejecucion
                return false;
            }else{

                //Como todos los parametro son opcionales, seguimos aunque No haya coordenadas tipo T
                //Lo hice el 19 oct 17, para cumplimentar el req: todos los parametros de
                // CONFIG.txt son opcionales
                //Chequeo si arrayPojoCoordenadasAlfanumerico tiene coordenadas o no
                if(arrayPojoCoordenadasAlfanumerico != null){
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoT, arrayPojoCoordenadasAlfanumerico tiene: "
                            +arrayPojoCoordenadasAlfanumerico.size() +" coordenadas");

                }else{
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoT, arrayPojoCoordenadasAlfanumerico es null");

                }

                //Modificar la imagen a superponer: pixels blancos son convertidos a transparentes con channel alpha
                imagenParaSuperponerConOrigin = changeSomePixelsToTransparent(imagenParaSuperponerConOrigin);

                //Leere las coordenadas reales obtenidas del fichero CONFIG.txt
                //Siempre chequeo que i no sea mayor o igual que la lista de coordenadas T, por si acaso
                //el fichero CONFIG.txt no tiene las 16 coordenadas T sino un numero menor.
                if(i >= arrayPojoCoordenadasAlfanumerico.size()){
                        //Modificacion el 20 oct 2017:
                        //Nuevo requerimiento: ahora se admite que el indice del array de alphanumeric sea mayor
                        //que el de coordenadas T.
                        //No se lanza error, se hace el loop hasta esta condicion, si existe,
                        //y solo se superponen las imagenes hasta que no se cumpla esta condicion,
                        //cuando indice del array de numeros sea mayor que el de coordenadas T
                        //Nuevo req 28oct17: fichero de log
                        //informar cuando esto ocurre al log
                        escribirDatosEnLog("index of alphanumeric string > index of T coordenates");
                        Log.d(xxx, "metodo loopPrincipalImagenesTipoT, No hay fallo, fin del loop tipo T debido a");
                        Log.d(xxx, "metodo loopPrincipalImagenesTipoT, ........./index of alphanumeric string > index of T coordenates....,");
                        break;//termina el loop


                        //Dejo el codigo original comentado
                    //enviarNotification("Error en indice de coordenadas alfanumericas, saliendo de la aplicacion");
                    //enviarNotificationConNumero("E1");
                    //metodoMostrarError("E1", "Error in index of T coordenates");
                    //Log.d(xxx, "metodo loopPrincipalImagenesTipoT, Error en indice de coordenadas, salimos de la app");
                    //return false;//Cerrar aplicacion y evitar un null pointer
                }


                //Para corregir fallos de null, OJO OJO OJO
                /*
                if(listaCoordenadas.get(i).getCoordX() == null || listaCoordenadas.get(i).getCoordY() == null){
                    //No lee los valores null
                }else{
                    xFloat = Float.parseFloat(listaCoordenadas.get(i).getCoordX());
                    yFloat = Float.parseFloat(listaCoordenadas.get(i).getCoordY());
                } */



                xFloat = Float.parseFloat(arrayPojoCoordenadasAlfanumerico.get(i).getCoordX());
                yFloat = Float.parseFloat(arrayPojoCoordenadasAlfanumerico.get(i).getCoordY());

                //Chequear que xFloat y yFloat son validos, si no, cerrar el programa
                //Float.isNaN retorna true si no es un numero
                if(Float.isNaN(xFloat) || Float.isNaN(yFloat)){
                    enviarNotification("Error, coordenadas alfanumericas no son un numero valido, saliendo de la aplicacion");
                    enviarNotificationConNumero("E1");
                    metodoMostrarError("E1", "Error: some coordenate T is not a number");
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoT, Error en coordenadas x o y no son un numero, revisar CONFIG.txt, salimos de la app");

                    return false;//Cerrar aplicacion y evitar fallo en el procesamiento
                }


                //Mezclar la imagen pequeña con origin.jpg en las coordenada que corresponden en CONGIG.txt
                mergedImages = createSingleImageFromMultipleImagesWithCoord(originJpg, imagenParaSuperponerConOrigin,
                        xFloat, yFloat);
                //En cada pasada, originJpg se tiene que refrescar con las imagenes mezcladas.
                originJpg = mergedImages;
                if(mergedImages != null) {
                    //Comando de prueba. Comentar esta linea en la version final
                    //collageImage.setImageBitmap(mergedImages);
                }else{
                    //Ha habido un error al mezclar las imagenes
                    enviarNotification("Error mezclando imagen alfanumerica: " +i  +", saliendo de la aplicacion");
                    enviarNotificationConNumero("E1");
                    metodoMostrarError("E1", "Error when mixing alphanumeric images");
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoT, mergedImages es null, no se ha generado la imagen, salimos de la app");

                    return false;

                }
                //
            }

        }//Fin del loop principal

        return true;
    }//loopPrincipalImagenesTipoT


    private boolean ejecutarConParametroSor(){
        //Si existe el parametro SOR en la app, se ejecuta este metodo

        //************************************************************************************************
        //************************************************************************************************
        //************************************************************************************************
        //************************************************************************************************
        /**
         * Requeriniento SOR, incluido en version 1.0.2.
         * Esta clase tiene un metodo para ordenar el array de imagenes recibido desde el lanzador.
         * El codigo me lo proporciono cesar en el correo "nuevas funciones"
         */

        //Paso 1:
        //Inicializo gOrigen  al tamaño de arrayImagesSequence
        //gOrigen = new int[arrayImagesSequence.length];

        //Inicializo a 50 el array
        gOrigen = new int[100];



        //Convierto el arrayImagesSequence que es tipo char a un array de tipo byte
        //Hay que usar algo asi:
        //int a = Character.getNumericValue('3');
        //int a = Integer.parseInt(String.valueOf('3');
        for (int i = 0; i < arrayImagesSequence.length; i++){
            gOrigen[i] = Character.getNumericValue(arrayImagesSequence[i]);
            Log.d(xxx, "en ejecutarConParametroSor secuencia de imagenes en gOrigen recibida del lanzador, digito: " +i +" es: " +gOrigen[i]);
        }

        //Prueba 1: ORDENACION 1, tipo de ordenacion: 1, 2, o 4:, input: 10 digitos
        //ordenaNumeros(6);

        int integerSOR = Integer.parseInt(stringSOR);
        if(integerSOR < 1 || integerSOR >6){
            //Hay un error, No se podido ordenar el array, terminamos la ejecucion he informamos con una notificacion
            enviarNotification("Error, SOR esta fuera de rango, debe estar entre 1 y 6" +", saliendo de la aplicacion");
            enviarNotificationConNumero("E1");
            metodoMostrarError("E1", "SOR parameter out of range, value should be 1-6");
            Log.d(xxx, "En ejecutarConParametroSor, Error, SOR esta fuera de rango, debe estar entre 1 y 6, salimos de la app");
            return false;

        }

        //**************************************************************************
        //Chequeo de errores: verificar el numero de digitos segun el valor de SOR. Cada numero son dos digitos
        //Numeracion final
        // ORDENACION 1= LOS 5 PRIMEROS NUMEROS SE ORDENAN
        if ((integerSOR==1)||(integerSOR==2)||(integerSOR==4)) {
            if(sizearrayImagesSequence < 10){
                enviarNotificationConNumero("E1");
                metodoMostrarError("E1", "Number of alphanumeric digits is < 10, should be at least 10 digits");
                Log.d(xxx, "En ejecutarConParametroSor, SOR= " +integerSOR  +", ERROR: el numero de digitos debe ser minimo 10");
                return false;
            }

        }
        // ORDENACION 2 = LOS 6 PRIMEROS NUMEROS SE ORDENAN
        else if ((integerSOR==3)||(integerSOR==6)) {
            if(sizearrayImagesSequence < 12){
                enviarNotificationConNumero("E1");
                metodoMostrarError("E1", "Number of alphanumeric digits is < 12, should be at least 12 digits");
                Log.d(xxx, "En ejecutarConParametroSor, SOR= " +integerSOR  +", ERROR: el numero de digitos debe ser minimo 12");
                return false;
            }
        }
        // ORDENACION 3 = LOS 7 PRIMEROS NUMEROS SE ORDENAN
        else if (integerSOR==5) {
            if(sizearrayImagesSequence < 14){
                enviarNotificationConNumero("E1");
                metodoMostrarError("E1", "Number of alphanumeric digits is < 14, should be at least 14 digits");
                Log.d(xxx, "En ejecutarConParametroSor, SOR= " +integerSOR  +", ERROR: el numero de digitos debe ser minimo 14");
                return false;
            }
        }

        //**************************************************************************




        //Llamamos a la rutina de ordenar
        ordenaNumeros(integerSOR); //Coloca en gOrigin[] el resultado ordenado


        //Numeracion final
        // ORDENACION 1= LOS 5 PRIMEROS NUMEROS SE ORDENAN
        if ((integerSOR==1)||(integerSOR==2)||(integerSOR==4)) {
            gOriginFinal = new int[sizearrayImagesSequence];
            for (int x=0; x<10; x++) {
                gOriginFinal[x] = gOrigen[x];
            }
        }
        // ORDENACION 2 = LOS 6 PRIMEROS NUMEROS SE ORDENAN
        else if ((integerSOR==3)||(integerSOR==6)) {
            gOriginFinal = new int[sizearrayImagesSequence];
            for (int x=0; x<12; x++) {
                gOriginFinal[x] = gOrigen[x];
            }
        }
        // ORDENACION 3 = LOS 7 PRIMEROS NUMEROS SE ORDENAN
        else if (integerSOR==5) {
            gOriginFinal = new int[sizearrayImagesSequence];
            for (int x=0; x<14; x++) {
                gOriginFinal[x] = gOrigen[x];
            }
        }

        if (gOriginFinal == null || gOrigen == null){
            //Hay un error, No se podido ordenar el array, terminamos la ejecucion he informamos con una notificacion
            enviarNotification("Fallo al ordenar el array de imagenes" +", saliendo de la aplicacion");
            enviarNotificationConNumero("E1");
            metodoMostrarError("E1", "Error in ordering algorithm for SOR parameter");
            Log.d(xxx, "En ejecutarConParametroSor, Fallo al ordenar el array de imagenesl, salimos de la app");
            return false;
        }

        String stringSecuenciaOrdenada = "";
        for (int i = 0; i < gOriginFinal.length; i++){
            Log.d(xxx, "En ejecutarConParametroSor, req SOR secuencia de imagenes en gOrigen ordenada, digito: " +i +" es: " +gOrigen[i]);
            stringSecuenciaOrdenada = stringSecuenciaOrdenada + gOrigen[i];
        }
        Log.d(xxx, "En ejecutarConParametroSor, req SOR stringSecuenciaOrdenada: " +stringSecuenciaOrdenada);



        //Por ultimo, sobreescribimos arrayImagesSequence con la secuencia ordenada
        arrayImagesSequence = null;
        //Convertir el string de secuencia de imagenes en un array de secuencia de imagenes, character a character
        arrayImagesSequence = stringSecuenciaOrdenada.toCharArray();
        //Verificamos el nuevo array ordenado
        for (char temp : arrayImagesSequence) {
            //AQUI AQUI AQUI AQUOI
            Log.d(xxx, "En metodo ejecutarConParametroSor, secuencia de imagenes ordenada " +temp);
        }//OK, la app continua

        return true;



        //FIN del requerimiento SOR
        //************************************************************************************************
        //************************************************************************************************
        //************************************************************************************************
        //************************************************************************************************
    }


    /*
    public boolean modificarParametroFechaEnCofigTxt(){
        //Este metodo no lo uso, 17 oct 2017
        //Sirve para modificar o agregar el parametro Date en el fichero CONFIG.txt


        //Escribir la fecha en la que se ha generado la imagen en el fichero CONFIG.txt
        //Implementado en version 1.0.2
        //************************************************************************************************
        //Requerimiento: Nuevo parámetro para fijar la fecha del fichero jpg generado.
        //poner en CONFIG.txt la fecha de generacion de la imagen
        //Generar un string con todo el contenido de CONFIG.txt a partir de arrayLineasTexto
        String textoDeConfigTxt = "";
        String oldDate = "";
        String newTextInConfigTxt = "";
        for (int i=0; i < arrayLineasTexto.size(); i++){
            //Log.d(xxx, "Linea "  +(i+1) +" contiene: " +arrayLineasTexto.get(i));
            if(arrayLineasTexto.get(i).startsWith("Date=")){
                Log.d(xxx, "xxx, Hay una linea que empieza con web y tiene: " +arrayLineasTexto.get(i));
                oldDate = arrayLineasTexto.get(i);
                Log.d(xxx, "xxx, oldDate tiene: " +oldDate);

            }

            textoDeConfigTxt += arrayLineasTexto.get(i) +"\r\n";
        }
        Log.d(xxx, "Texto Viejo de CONFIG.txt en string textoDeConfigTxt: " +textoDeConfigTxt);
        //Generamos la fecha actual
        //Con la hora
        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HHmm");
        //Sin la hora
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateandTime = sdf.format(new Date());
        String textoFechaActualizada = "Date=" +currentDateandTime;

        if(oldDate.equals("")){
            //No hay un string con date en CONFIG.txt
            //Insertamos la nueva fecha en textoDeConfigTxt al final
            textoDeConfigTxt += textoFechaActualizada +"\r\n";
            newTextInConfigTxt = textoDeConfigTxt;

        }else{
            newTextInConfigTxt = textoDeConfigTxt.replaceAll(oldDate, textoFechaActualizada);

        }
        Log.d(xxx, "Texto Nuevo de CONFIG.txt en string newTextInConfigTxt: " +newTextInConfigTxt);



        EscribirEnFicheroTxt escribirEnFicheroTxt = new EscribirEnFicheroTxt(MezclarFinal.this);
        if(escribirEnFicheroTxt.appendDateEnFichero(pathCesaralMagicImageC + ficheroConfigTxt, newTextInConfigTxt)){
            //Return true al final del metodo. La app se queda abierta, esperando el resultado de la subida de predict.jpg con ftp
            enviarNotification("Imagen guardada en /DCIM/predict/  Ejecucion correcta" +"\n" +"Esperando resultado ftp...");
            enviarNotificationConNumero("2");
            Log.d(xxx, "En metodoPrincipal_2, Se ha guardado la fecha en CONFIG.txt, seguimos con el ftp");
            return true;

        }else{
            //Ha habido un error al escribir la fecha en CONFIG.txt, cerrar la app y no hacer ftp
            enviarNotification("Error al escribir la fecha en CONFIG.txt" +", saliendo de la aplicacion");
            enviarNotificationConNumero("E1");
            Log.d(xxx, "En metodoPrincipal_2, Error al escribir la fecha en CONFIG.txt, salimos de la app");
            return false;

        }


    }  */

    public void modificarFechaEnFichero(){
        //Modificar la fecha de predict.jpg si el parametro Date esta en config.txt
        //Obtengo la un objeto file con el path a predict.jpg y uso los metodos:
        //setLastModified(long time)
        //lastModified(), retorna un long

        //if (!guardarImagenFinal.guardarImagenMethod(Environment.DIRECTORY_DCIM, "/predict/", "predict.jpg")){
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String directorio = picturesDir.getAbsolutePath() ;

        String pathToFile = directorio +"/predict/"  +"predict.jpg";
        File file = new File(pathToFile);
        if(file.exists()) {
            long date = file.lastModified();
            Date fileData = new Date(date);

            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            String fechaDeLaFoto = sdf2.format(fileData);
            Log.d(xxx, "modificarFechaEnFichero La fecha del fichero: " +file.getAbsolutePath() +" es:" +fileData);
            Log.d(xxx, "modificarFechaEnFichero La fecha del fichero: " +file.getAbsolutePath() +" formateada es:" +fechaDeLaFoto);

            file.setLastModified(getDate(2017, 3, 1));

            //Verifico el cambio:
            date = file.lastModified();
        /* Do your modified date stuff here */
            fileData = new Date(date);
            fechaDeLaFoto = sdf2.format(fileData);
            Log.d(xxx, "modificarFechaEnFichero La fecha del fichero modoficada: " +file.getAbsolutePath() +" es:" +fileData);
            Log.d(xxx, "modificarFechaEnFichero La fecha del fichero modificada: " +file.getAbsolutePath() +" formateada es:" +fechaDeLaFoto);



        }

    }

    //metodo que devuelve un long de acuerdo a los parametros pasados
    public long getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Log.d(xxx, "en metodo getDate: la fecha creada con cal es " +cal.getTime());

        Date date = cal.getTime();
        Log.d(xxx, "en metodo getDate: la fecha creada con date es " +date.toString());

        return date.getTime();
    }




    //Metodo que envia distintos iconos a la barra de notificaciones, segun el estado de la app
    private void enviarNotificationConNumero(String stringConCodigoDeError){
        //Este switch para seleccionar la imagen correcta de la notificacion a enviar
        Log.d(xxx, "En metdo enviarNotificationConNumero, el codigo a enviar es: " + stringConCodigoDeError);
        int intIdentificadorDelIcon = R.drawable.ic_number_1;
        switch (stringConCodigoDeError){
            case "1"://N1
                intIdentificadorDelIcon = R.drawable.ic_number_1;

                break;
            case "2"://N2
                intIdentificadorDelIcon = R.drawable.ic_number_2;

                break;
            case "3"://N3
                intIdentificadorDelIcon = R.drawable.ic_number_3;

                break;
            case "OK"://N4
                intIdentificadorDelIcon = R.drawable.ic_icono_ok;

                break;
            case "E1"://N5
                intIdentificadorDelIcon = R.drawable.ic_error_jpg;

                break;
            case "E2"://N6
                intIdentificadorDelIcon = R.drawable.ic_error_ftp;

                break;
        }

        //Get an instance of NotificationManager//
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(intIdentificadorDelIcon)
                        .setContentTitle("CUPP Lite");
                        //.setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje));
        //.setContentText(mensaje);
        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try
        // to update an existing notification with this new information, rather than immediately creating a
        // new notification. If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//

        mNotificationManager.notify(003, mBuilder.build());
    }//Fin de enviarNotificationConNumero




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

    //ESte metodo es como leerCoordenadasDeConfigTxt, pero devuelve el array list de pojos
    private void leerCoordenadasDeConfigTxt_2(String linea){
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


    //Metodo para:
    //Generar array de PojoCoordenadas con las coordenadas x e Y de posicionamiento de imagenes
    //Generar la URL para subir y almacenar la imagen generada a un servidor
    //tambien lee user, password, SOR.
    private List<PojoCoordenadas> generarPojoGenerarUrl(List<String> arrayLineasTextoLocal){
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
    }//Fin de generarPojoGenerarUrl y obtener user y password


    //Metodo final para la mezcla
    private Bitmap createSingleImageFromMultipleImagesWithCoord(Bitmap firstImage, Bitmap secondImage, float x, float y ){

        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, x, y, null);
        return result;
    }

    private Bitmap changeSomePixelsToTransparent(Bitmap originalImage){

        Bitmap bitmap2 = originalImage.copy(Bitmap.Config.ARGB_8888,true);
        bitmap2.setHasAlpha(true);
        for(int x=0;x<bitmap2.getWidth();x++){
            for(int y=0;y<bitmap2.getHeight();y++){
                //if(bitmap2.getPixel(x, y)==Color.rgb(0xff, 0xff, 0xff))
                //if(bitmap2.getPixel(x, y)<=Color.rgb(0xd7, 0xd7, 0xd7))
                if(bitmap2.getPixel(x, y)>=Color.rgb(0xd7, 0xd7, 0xd7))
                {
                    int alpha = 0x00;
                    bitmap2.setPixel(x, y , Color.argb(alpha,0xff,0xff,0xff));  // changing the transparency of pixel(x,y)
                }
            }
        }
        return bitmap2;
    }

    //Metodo anulado para que no envie las notificaciones con texto
    private void enviarNotification(String mensaje){

        /*
        //Get an instance of NotificationManager//
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Mezclar")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje));
                        //.setContentText(mensaje);
        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try
        // to update an existing notification with this new information, rather than immediately creating a
        // new notification. If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//

        mNotificationManager.notify(001, mBuilder.build()); */


    }

    //Metodo anulado para que no envie las notificaciones con texto
    private void enviarNotificationFtp(String mensaje){

        /*
        //Get an instance of NotificationManager//
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Mezclar: ftp process")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje));
        //.setContentText(mensaje);
        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try
        // to update an existing notification with this new information, rather than immediately creating a
        // new notification. If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//

        mNotificationManager.notify(002, mBuilder.build()); */



    }



    //Metodo para operar con ftp y subir el la imagen predict.jpg al servidor:
    //http://ftp.cesaral.com/test/
    //Como en: http://tutoandroidblog.blogspot.com.es/2013/01/servidor-ftp-de-subida-de-archivo.html


    private boolean metodoSubirImagenConFtp(ObtenerImagen obtenerImagen) {
        Log.d(xxx, "En metodoSubirImagenConFtp");

        //Lanzar notificacion de que la imagen ha sido compuesta y se inicia el preceso ftp
        enviarNotificationConNumero("3");

        //Este metodo es llamado desde el doInBackground del asynctask de ftp


        //Credenciales
        String ip;                    //Almacena la direción ip del servidor
        FtpClient ftp;                    //Instancia manejador ftp

        //ip = "ftp.cesaral.com/test";
        ip = urlServidor;

        //Asignaciones solo para pruebas
        //user = "testx";
        //password = "test.2017";


        //Chequear si obtenerImagen es null
        if(obtenerImagen == null){
            Log.d(xxx, "En metodoSubirImagenConFtp, obtenerImagen == null");

        }else{
            Log.d(xxx, "En metodoSubirImagenConFtp, obtenerImagen No es null null");

        }

        File filePathDePredictJpg = obtenerImagen.getFilePathOfPicture(Environment.DIRECTORY_DCIM, "/predict/", nombreFicheroJpg);

        if(filePathDePredictJpg == null){
            enviarNotificationFtp("Error al obtener el file de predict.jpg para upload ftp" +", saliendo de la aplicacion");
            enviarNotificationConNumero("E2");
            metodoMostrarError("E2", "Error recovering compoised image predict.jpg from external storage");
            Log.d(xxx, "En metodoSubirImagenConFtp, Error al obtener el file de predict.jpg para upload ftp");

            return false;

        }else {//Hemos obtenido el file de la imagen a subir, seguimos

            //Chequeamos que el path a predict.jpg es correcto:
            Log.d(xxx, "Path a predict.jpg para enviar al servidor con ftp: " + filePathDePredictJpg.getName());
            Log.d(xxx, "Absolute Path a predict.jpg para enviar al servidor con ftp: " + filePathDePredictJpg.getAbsolutePath());

            //Establece un servidor
            ftp = new FtpClient(ip, user, password, getApplicationContext());

            //Realiza login en el servidor

            try {
                //Prueba de fallo de user
                //user="";
                if(ftp.login(user, password)){
                    //Login correcto, enviamos el fichero con el try catch de abajo
                }else{
                    enviarNotificationFtp("Error: El login o la conexion al servidor ftp ha fallado" +", saliendo de la aplicacion");
                    enviarNotificationConNumero("E2");
                    metodoMostrarError("E2", "Error with ftp connect or login");
                    Log.d(xxx, "En metodoSubirImagenConFtp, Error: El login o la conexion al servidor ftp ha fallado" +", saliendo de la aplicacion");
                    return false;
                }
            } catch (SocketException e) {
                //e.printStackTrace();
                enviarNotificationFtp("Error Socket Exception en ftp login: " +e.getMessage() +", saliendo de la aplicacion");
                enviarNotificationConNumero("E2");
                metodoMostrarError("E2", "Error Socket Exception in ftp login: ");
                Log.d(xxx, "En metodoSubirImagenConFtp, Error Socket Exception en ftp login: " +e.getMessage());

                return false;
            } catch (IOException e) {
                //e.printStackTrace();
                enviarNotificationFtp("Error IOException en ftp login: " +e.getMessage() +", saliendo de la aplicacion");
                enviarNotificationConNumero("E2");
                metodoMostrarError("E2", "Error IOException in ftp login ");
                Log.d(xxx, "En metodoSubirImagenConFtp, Error IOException en ftp login: " +e.getMessage());

                return false;
            }

            //Sube el archivo al servidor
            try {
                //if(ftp.enviarFile(nombreArhivo)){
                //if(ftp.enviarFile("predict.jpg")){
                //if(ftp.enviarFileFinalFinal(filePathDePredictJpg, nombreFicheroJpg)){
                //Envio el fichero al server con el nombre predict.jpg siempre
                if(ftp.enviarFileFinalFinal(filePathDePredictJpg, "predict.jpg")){
                    enviarNotificationFtp("Fichero predict.jpg enviado al servidor");

                    //Lanzar notificacion de que el proceso ha terminado de forma correcta
                    enviarNotificationConNumero("OK");
                    Log.d(xxx, "En metodoSubirImagenConFtp, Archivo predict.jpg enviado al servidor");

                    return true;
                }else{
                    enviarNotificationFtp("Error: Fallo al enviar el fichero predict.jpg al servidor" +", saliendo de la aplicacion");
                    enviarNotificationConNumero("E2");
                    metodoMostrarError("E2", "Error upload file to ftp server ");
                    Log.d(xxx, "En metodoSubirImagenConFtp, Error: Fallo al enviar el fichero predict.jpg al servidor");
                    return false;
                }
            } catch (IOException e) {
                //e.printStackTrace();
                enviarNotificationFtp("Error IOException en ftp al enviar el fichero al servidor: " +e.getMessage() +", saliendo de la aplicacion");
                enviarNotificationConNumero("E2");
                metodoMostrarError("E2", "ErrorIOException  upload file to ftp server ");
                Log.d(xxx, "En metodoSubirImagenConFtp, Error IOException en ftp al enviar el fichero al servidor: " +e.getMessage());

                return false;
            }

        }//Fin del else de if(filePathDePredictJpg == null)

    }//Fin de metodo metodoSubirImagenConFtp


    //Clase para subir la imagen al servidor en un thread distinto de la UI
    private class FtpAsyncTask extends AsyncTask<String, Integer, Boolean> {
        public FtpAsyncTask() {
            super();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Log.d(xxx, "estoy en doInBackground de FtpAsyncTask");

            if (metodoSubirImagenConFtp(obtenerImagen)) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(Boolean boolResultado) {
            Log.d(xxx, "onPostExecute de FtpAsyncTask, el resultado de doInBackground es: " +boolResultado);

            if(boolResultado) {
                Log.d(xxx, "En onPostExecute: Success, Imagen enviada al servidor ftp");
                //Mantener la app abierta y avisar con snakc bar:
                /*
                Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Imagen predict.jpg guardada en DCIM/predict y enviada al servidor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); */

                //Si todo va bien, dejo la app abierta pero deshabilito el fab:

                //No hago nada con el fab
                //fabOnPostExecute = (FloatingActionButton) findViewById(R.id.fab);
                //fabOnPostExecute.setVisibility(View.INVISIBLE);

                //Nuevo req 28oct17: fichero de log
                escribirDatosEnLog("OK");


                //Exito
                finish();

            }else{
                Log.d(xxx, "En onPostExecute: FAIL, Imagen NO enviada al servidor ftp, saliendo de la app");
                //progressBar.setVisibility(View.INVISIBLE); //To Hide ProgressBar
                //Cerrar aplicacion, ha habido un fallo
                //finish();

                //Lanzo el error al texview desde aqui, No me funciona desde el doInBackground
                //por que desde el otro thread o proceso de doInBackground no se pueden tocar
                //componentes de la UI
                metodoMostrarErrorDesdeOnPostExecute();
                //Nuevo req 28oct17: fichero de log
                escribirDatosEnLog(stringTipoDeError +separador +stringMensajeDeError);
            }
            //Tanto si hay fallo como si se ejecuta correctamente, se cierra la app
            //finish();
        }

        protected void onProgressUpdate(Integer[] values) {
        }

        protected void onCancelled() {
        }
    }//FIN de la clase FtpAsyncTask

    //Variables para el requerimiento del log
    /*
    String subDirLogFile = "/CesaralMagic/ImageC/";
    String fechaLog = "";
    String horaLog = "";
    String cadenaNumericaEmpleada = "";
    String cadenaAlphaumericaEmpleada = "";
    String mensaje = "";  */


    //Nuevo req 28oct17: fichero de log
    private void escribirDatosEnLog(String mensajeError){
        Date date = new Date();
        //Fecha / hora / cadena numérica empleada / cadena alfanumérica empleada / “OK” o “detalle del error encontrado”

        /*Por ejemplo, con solo dato numéricos:
        17/10/2017 / 10:24 / 012345678 / /  OK
        Por ejemplo, con solo dato alfanuméricos:
        17/10/2017 / 10:24 / / test /  OK
        Por ejemplo, con datos de ambos tipo, pero dando error:
        17/10/2017 / 10:27 / 123456 / test /  Upload Server “ftp.cesaral.com” not found */


        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yy");
        fechaLog = sdf2.format(date);
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
        horaLog = sdf3.format(date);
        String mensajeLog = "//" +fechaLog +separador +horaLog +separador
                +cadenaNumericaEmpleada +separador +cadenaAlphaumericaEmpleada +separador +mensajeError +"\r\n";
        Log.d(xxx, "escribirDatosEnLog el mensaje del log es: " +mensajeLog);


        EscribirEnFicheroTxt escribirEnFicheroTxt = new EscribirEnFicheroTxt(MezclarFinal.this);
        if(escribirEnFicheroTxt.appendDateEnFichero(pathCesaralMagicImageC + "log.txt", mensajeLog)){

            Log.d(xxx, "escribirDatosEnLog fichero escrito correctamente");

        }else{
            Log.d(xxx, "escribirDatosEnLog fichero NO escrito correctamente");


        }
    }



    //Asynctask para componer la imagen
    private class ComponerImagenAsyncTask extends AsyncTask<String, Integer, Boolean> {
        public ComponerImagenAsyncTask() {
            super();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Log.d(xxx, "estoy en doInBackground de ComponerImagenAsyncTask");

            boolean booleanContinuarApp = metodoPrincipal_2();
            if(booleanContinuarApp){
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(Boolean boolResultado) {
            Log.d(xxx, "onPostExecute de ComponerImagenAsyncTask, el resultado de doInBackground es: " +boolResultado);

            if(boolResultado) {
                Log.d(xxx, "En onPostExecute: Success, Imagen compuesta correctamente, se inicia FtpAsyncTask");

                //Mostramos la imagen compuesta en pantalla, no se puede manipular un componente grafico desde el thread
                collageImage.setImageBitmap(mergedImages);
                collageImage.setVisibility(View.VISIBLE);

                //Lanzamos el asynctask de enviar imagen al servidor con ftp
                new FtpAsyncTask().execute("string1", "string2", "string3");

            }else{//Ha habido un fallo
                Log.d(xxx, "En onPostExecute: FAIL, Imagen jpg NO generada, saliendo de la app");
                //progressBar.setVisibility(View.INVISIBLE); //To Hide ProgressBar
                //Cerrar aplicacion, ha habido un fallo
                //finish();

                //Lanzo el error al texview desde aqui, No me funciona desde el doInBackground
                //por que desde el otro thread o proceso de doInBackground no se pueden tocar
                //componentes de la UI
                metodoMostrarErrorDesdeOnPostExecute();
                escribirDatosEnLog(stringTipoDeError +separador +stringMensajeDeError);


            }
        }

        protected void onProgressUpdate(Integer[] values) {
        }

        protected void onCancelled() {
        }
    }//FIN de la clase ComponerImagenAsyncTask

    //Metodo de ordenacion proporcionado por cesar
    //------------------------------------------------------------------------------
// A la función se le pasa el numero asociado al parámetro SOR
//
// Variable global donde están los números a ordenar = gOrigen[]
// Variable global donde se dejan los números a ordenados = gOrigen[]
//------------------------------------------------------------------------------
    void ordenaNumeros(int TipoOrdenacion){


        int x, num, numc1=0, numc2=0;
        int gCad[] = new int[100];

        // BORRO LA MEMORIA DE TRABAJO
        for (x=0; x<=99; x++) gCad[x]=0;


        // ORDENACION 1= LOS 5 PRIMEROS NUMEROS SE ORDENAN
        if ((TipoOrdenacion==1)||(TipoOrdenacion==2)||(TipoOrdenacion==4)) {
            for (x=0; x<=8; x++) {
                num=gOrigen[x]*10 + gOrigen[x+1];
                if (num!=0) gCad[num]=1;
                x+=1;
            }
        }
        // ORDENACION 2 = LOS 6 PRIMEROS NUMEROS SE ORDENAN
        else if ((TipoOrdenacion==3)||(TipoOrdenacion==6)) {
            for (x=0; x<=10; x++) {
                num=gOrigen[x]*10 + gOrigen[x+1];
                if (num!=0) gCad[num]=1;
                x+=1;
            }
        }
        // ORDENACION 3 = LOS 7 PRIMEROS NUMEROS SE ORDENAN
        else if (TipoOrdenacion==5) {
            for (x=0; x<=12; x++) {
                num=gOrigen[x]*10 + gOrigen[x+1];
                if (num!=0) gCad[num]=1;
                x+=1;
            }
        }

        // ME QUEDO CON DOS DATOS QUE PUEDO NECESITAR...
        numc1=gOrigen[10]*10+gOrigen[11];
        numc2=gOrigen[12]*10+gOrigen[13];


        for (x=0; x<16; x++) gOrigen[x]=0;

        // AHORA RECORRO LA MEMORIA Y CARGO LOS NUEVOS NUMEROS
        int y=0;
        for (x=0; x<=99; x++) {
            if (gCad[x]!=0) {
                gOrigen[y++]=x/10;
                gOrigen[y++]=x%10;
            }
        }

        if (TipoOrdenacion==1) {
            // EL QUINTO NUMERO NO LO TOCO EN EL CASO DE UK.
            gOrigen[10]=numc1/10;
            gOrigen[11]=numc1%10;
        }
        else if (TipoOrdenacion==2) {
            if (numc1==0) numc1=11;
            if (numc2==0) numc2=12;

            if (numc2<numc1) {
                gOrigen[10]=numc2/10;
                gOrigen[11]=numc2%10;
                gOrigen[12]=numc1/10;
                gOrigen[13]=numc1%10;
            }
            else {
                gOrigen[10]=numc1/10;
                gOrigen[11]=numc1%10;
                gOrigen[12]=numc2/10;
                gOrigen[13]=numc2%10;
            }
        }
        else if (TipoOrdenacion==4) {
            gOrigen[10]=numc1/10;
            gOrigen[11]=numc1%10;
        }
        else if (TipoOrdenacion==6) {
            gOrigen[12]=numc2/10;
            gOrigen[13]=numc2%10;
        }
    }//Fin del algoritmo de ordenacion proporcionado por cesar



    //Metodo para mostrar mensajes de error E1 y E2 por pantalla
    String stringTipoDeError = "";
    String stringMensajeDeError = "";
    private void metodoMostrarError(String tipoDeError, String mensaje){
        Log.d(xxx, "estoy en metodoMostrarError, mensaje: " +mensaje);

        //************************************************************************************
        //Esta parte no funciona

        //textViewErrores.setText(tipoDeError +": " +mensaje);

        //Para mostrar el textview hay que hacerlo a traves de un handler, por que no estamos en el proceso de la UI
        //Estamos en doInBackground del asynctask, y no podemos tocar cosas de la UI, como las vistas
       /* Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                textViewErrores.setText(tipoDeError +": " +mensaje);


            }
        });  */
        //****************************************************************************************

        //Truco; asigno dos variables globales y las uso en el onPostExecute
        stringTipoDeError = tipoDeError;
        stringMensajeDeError = mensaje;
    }//Fin de metodoMostrarError

    private void metodoMostrarErrorDesdeOnPostExecute(){
        Log.d(xxx, "estoy en metodoMostrarErrorDesdeOnPostExecute, tipo de error: " +stringTipoDeError);
        Log.d(xxx, "estoy en metodoMostrarErrorDesdeOnPostExecute, mensaje: " +stringMensajeDeError);
        textViewErrores.setText(stringTipoDeError +": " +stringMensajeDeError, null);


    }//Fin de metodoMostrarErrorDesdeOnPostExecute



}//Fin del activity
