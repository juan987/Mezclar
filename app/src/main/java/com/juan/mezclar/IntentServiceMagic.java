package com.juan.mezclar;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juan.mezclar.ftpClases.FtpClient;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IntentServiceMagic extends IntentService {
    String xxx = this.getClass().getSimpleName();

    DatosConfigTxt datosConfigTxt;

    //ESTE SERVICIO SOLO MANEJA COORDENADAS N
    //Variables de UI, las comento. El servicio no tiene UI
    //private ImageView collageImage;
    //private ImageView finalImage;
    //TextView textViewErrores;

    //Array para almacenar la secuencia de imagenes a superponer
    char[] arrayImagesSequence;
    int sizearrayImagesSequence;
    //String de secuencia de imagenes inicializada con la imagen 0.
    //String stringImagesSecuence = "0";
    //Path a agregar al dir raiz del telefono
    String pathCesaralMagicImageC = "/CesaralMagic/ImageC/";
    //Nombre de la imagen principal sobre la que se superponen las imagenes de o a 9.
    String imagenPrincipal = "origin.jpg";
    //para prueba de fallo
    //String imagenPrincipal = "origin.xxjpg";
    //Bitmap que contiene el resultado de la imagen generada
    Bitmap originJpg;
    //array list que contiene datos de coordenada x e y obtenidos del fichero CONFIG.txt
    List<PojoCoordenadas> listaCoordenadas;
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

    //Parametro offset y scale para modificar coordenadas N y T
    int intOffset_x=0;
    int intOffset_y=0;
    Double doubleScale_x=1.0;

    //Nuevo requerimiento, centrado de cadenas/numeros recibido el 26 oct 2017
    int intCenterConfig = 0;
    boolean boolUsarCenter = false;

    //Nuevo req recibido el 27oct17, parametro m_x
    //Se trata de un parámetro opcional para el modo numérico solamente, denominado M_X
    int intMX = 0;
    boolean boolMX = false;



    public IntentServiceMagic() {
        super("IntentServiceMagic");
    }

    String stringImagesSecuence;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(xxx, "En onHandleIntent, el intent no es nulo");

            Bundle data = intent.getExtras();
            if (data != null) {
                String myString = data.getString("KeyName");
                stringImagesSecuence = myString;
                cadenaNumericaEmpleada = myString;
                Log.d(xxx, "En onHandleIntent, stringImagesSecuence tiene: " +stringImagesSecuence);
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


                //To show the toast hay que hacerlo a traves de un handler
                //Lo mismo para activar el asynctask ComponerImagenAsyncTask, si no, el compilador da un error de threads....
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), stringImagesSecuence, Toast.LENGTH_LONG).show();
                        //Lanzamos el asynctask de componer imagen
                        new IntentServiceMagic.ComponerImagenAsyncTask().execute("string1", "string2", "string3");

                    }
                });


            }
        }//Fin de intent!=null
    }//Fin de onHandleIntent

    //**********************************************************************************************
    //                Copia de todo lo que esta fuera del onCreate de MezclarFinal

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
        //TODO obtener getActiveDirectory de la clase ConfiguracionesMultiples
        ConfiguracionesMultiples configuracionesMultiples = new ConfiguracionesMultiples(IntentServiceMagic.this);
        pathCesaralMagicImageC = configuracionesMultiples.getActiveDirectory();
        //El directorio activo de la app es:
        Log.d(xxx, "En metodoPrincipal_2, el directorio activo es: " +pathCesaralMagicImageC);


        //**************************************************************************************************


        //Obtener todas las lineas del fichero CONFIG.txt en el dir del dispositivo: pathCesaralMagicImageC
        LeerFicheroTxt leerFicheroTxt = new LeerFicheroTxt(IntentServiceMagic.this);
        //arrayLineasTexto contiene todas las lineas de CONFIG.txt
        List<String> arrayLineasTexto = leerFicheroTxt.getFileContentsLineByLineMethod(pathCesaralMagicImageC + ficheroConfigTxt);
        if(arrayLineasTexto == null){
            //Hay un error, terminamos la ejecucion he informamos con una notificacion
            enviarNotification("Error 1 al recuperar CONFIG.txt del dir:  +" +pathCesaralMagicImageC +", saliendo de la aplicacion");
            enviarNotificationConNumero("E1");
            metodoMostrarError("E1", "Error reading file: CONFIG.txt from dir: " +pathCesaralMagicImageC);
            Log.d(xxx, "En metodoPrincipal_2, arrayLineasTexto es null, salimos de la app");


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
        //List<PojoCoordenadas> listaCoordenadas = generarPojoGenerarUrl(arrayLineasTexto);
        //listaCoordenadas = generarPojoGenerarUrl(arrayLineasTexto);

        //*****************************************************************************
        //*****************************************************************************
        //*****************************************************************************
        //Usar clase DatosConfigTxt
        //Juan, 25-10-17, paso el metodo generarPojoGenerarUrl a la clase DatosConfigTxt

        //DatosConfigTxt datosConfigTxt = new DatosConfigTxt(IntentServiceMagic.this);
        datosConfigTxt = new DatosConfigTxt(IntentServiceMagic.this);
        //Leer coordenadas N y T, URL, user, password,SOR, overwrite del array de lineas obtenido del fichero CONFIG.txt
        listaCoordenadas = datosConfigTxt.getCoordenadasN(arrayLineasTexto);

        arrayPojoCoordenadasAlfanumerico = datosConfigTxt.getArrayPojoCoordenadasAlfanumerico();
        urlServidor = datosConfigTxt.getUrlServidor();
        user = datosConfigTxt.getUser();
        password = datosConfigTxt.getPassword();
        stringSOR = datosConfigTxt.getStringSOR();
        stringOverwrite = datosConfigTxt.getStringOverwrite();
        //Parametro offset y scale para modificar coordenadas N y T
        intOffset_x = datosConfigTxt.getIntOffset_x();
        intOffset_y = datosConfigTxt.getIntOffset_y();
        doubleScale_x = datosConfigTxt.getDoubleScale_x();
        intCenterConfig = datosConfigTxt.getIntCenterConfig();
        boolUsarCenter = datosConfigTxt.getBoolUsarCenter();
        intMX = datosConfigTxt.getintMX();
        boolMX = datosConfigTxt.getboolMX();


        Log.d(xxx, "xxx Variable urlServidor: " +urlServidor
                +"\n"  +"xxx Variable user: " +user
                +"\n"  +"xxx Variable password: " +password
                +"\n"  +"xxx Variable SOR: " +stringSOR
                +"\n"  +"xxx Variable overwrite: " +stringOverwrite
                +"\n"  +"xxx Variable intOffset_x: " +intOffset_x
                +"\n"  +"xxx Variable intOffset_y: " +intOffset_y
                +"\n"  +"xxx Variable intCenterConfig: " +intCenterConfig
                +"\n"  +"xxx Variable boolUsarCenter: " +boolUsarCenter
                +"\n"  +"xxx Variable doubleScale_x: " +doubleScale_x
                +"\n"  +"xxx Variable boolMX: " +boolMX
                +"\n"  +"xxx Variable intMX: " +intMX);


        //FIN Usar clase DatosConfigTxt
        //*****************************************************************************
        //*****************************************************************************
        //*****************************************************************************

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
        obtenerImagen = new ObtenerImagen(IntentServiceMagic.this);
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



        //Ejecucion correcta, guardar imagen en la memoria externa del dispoositivo
        GuardarImagenFinal guardarImagenFinal = new GuardarImagenFinal(IntentServiceMagic.this, mergedImages, datosConfigTxt.getquality());
        //Guardar imagen el directorio pictures/predict

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

        //Guardar imagen en el directorio DCIM/predict
        //if (!guardarImagenFinal.guardarImagenMethod(Environment.DIRECTORY_DCIM, "/predict/", "predict.jpg")){
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






        //*****************************************************************************************
        //*****************************************************************************************
        //*****************************************************************************************
        //Avisar al media scanner
        //Como en:
        //https://www.grokkingandroid.com/adding-files-to-androids-media-library-using-the-mediascanner/
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        //File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String directorio = picturesDir.getAbsolutePath() ;
        Log.d(xxx, "antes del media scanner El directorio es: " + directorio);
        //Environment.DIRECTORY_DCIM, "/predict/", nombreFicheroJpg
        File file = new File(directorio + "/predict/", nombreFicheroJpg);
        Log.d(xxx, "antes del media scanner El directorio COMPLETO es: " + file.getAbsolutePath());


        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{file.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d(xxx, "soy juan y el media scanner" +
                                "file " + path + " was scanned seccessfully: " + uri);
                    }
                });
        //*****************************************************************************************
        //*****************************************************************************************
        //*****************************************************************************************




        return true;
    }//Fin de metodoPrincipal_2
    //Nombre de la imagen compuesta a guardar y a enviar con ftp
    String nombreFicheroJpg = "predict.jpg";


    private boolean loopPrincipalImagenesTipoN(){

        //parte 1, 2 nov 2017, Posicionamiento de ficheros en modo proporcional, parametro CENTER_P=nnn,
        // nuevo req del mail proximos requerimientos
        //Afecta a la cadena de numeros y alfanumericos
        int[] arrayAnchuraImagenesPequeñas = new int[arrayImagesSequence.length];
        int anchoTotalDeLasImagenesPequenas = 0;
        PosicionamientoProporcional posicionamientoProporcional = null;
        if(datosConfigTxt.getIntCenter_p() != 0){
            Log.d(xxx, "metodo loopPrincipalImagenesTipoN, CENTER_P=nnn existe y vale: " +datosConfigTxt.getIntCenter_p() );
            posicionamientoProporcional = new PosicionamientoProporcional(IntentServiceMagic.this);
            arrayAnchuraImagenesPequeñas = posicionamientoProporcional.getArrayAnchurasImagenesPequenas(pathCesaralMagicImageC, arrayImagesSequence);
            if(arrayAnchuraImagenesPequeñas == null){
                Log.d(xxx, "metodo loopPrincipalImagenesTipoN, CENTER_P=nnn existe y arrayAnchuraImagenesPequeñas es nuul" );

                //Hay un error con Center_P
                //Lanzamos error y salimos
                enviarNotification("Error de param CENTER_P=nnn: arrayAnchuraImagenesPequeñas es null,  saliendo de la aplicacion");
                enviarNotificationConNumero("E1");
                metodoMostrarError("E1", "Error in CENTER_P: can not get width of images");
                Log.d(xxx, "metodo loopPrincipalImagenesTipoN, Error de param CENTER_P=nnn: " +
                        "arrayAnchuraImagenesPequeñas es null, salimos de la app");

                //Acabamos la ejecucion
                return false;

            }
            anchoTotalDeLasImagenesPequenas = posicionamientoProporcional.getAnchoTotalDeTodasLasImagenes(arrayAnchuraImagenesPequeñas);
        }//FIN parte 1, 2 nov 2017, Posicionamiento de ficheros en modo proporcional, parametro CENTER_P=nnn,


        //Loop principal de la aplicacion
        Bitmap imagenParaSuperponerConOrigin;

        //Nuevo requerimiento, centrado de cadenas/numeros recibido el 26 oct 2017
        CentradoCadenasNumeros centradoCadenasNumeros = new CentradoCadenasNumeros(IntentServiceMagic.this);
        int offsetX_ParaCentrarN = centradoCadenasNumeros
                .getOffsetX_ParaCentrarImagenN(pathCesaralMagicImageC, "cualquier cosa, no la uso",
                        cadenaNumericaEmpleada, listaCoordenadas, intCenterConfig);
        Log.d(xxx, "metodo loopPrincipalImagenesTipoN, la formula de centradp da: " +offsetX_ParaCentrarN);
        //FIN de Nuevo requerimiento, centrado de cadenas/numeros recibido el 26 oct 2017

        //6 nov 2017, nuevo req en mail Plan lunes - Modo rotacional
        //este string es para usarlo cuando n=1
        String nombreImagenMM= "";
        for(int i = 0; i < arrayImagesSequence.length; i++) {
            //Nuevo req recibido el 27oct17, parametro m_x
            //Se trata de un parámetro opcional para el modo numérico solamente, denominado M_X
            //Boolean para saber si hay que dibujar o no la imagen correspondiente
            boolean boolDibujar = true;


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

            //2 nov 2017 Parte 1, nuevo req param mode_t en CONFIG.txt en mail proximos requerimientos
            //solo aplica a cadenas numericas con 4 digitos, los demas no valen
            Log.d(xxx, "metodo loopPrincipalImagenesTipoN, datosConfigTxt.getMode_t() es: " +datosConfigTxt.getMode_t());
            if(datosConfigTxt.getMode_t().equals("1")){
                //Chequear cuantos digitos tiene la cadena
                if(arrayImagesSequence.length < 4){
                    //Lanzamos error y salimos
                    enviarNotification("Error de param mode_t: numero de digitos es menor a 4 " +", saliendo de la aplicacion");
                    enviarNotificationConNumero("E1");
                    metodoMostrarError("E1", "Error in mode_t: number of digits should be 4");
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, Error de param mode_t: numero de digitos es menor a 4 , salimos de la app");

                    //Acabamos la ejecucion
                    return false;
                }
                //Numero de digitos correcto, sequimos
                //Estamos haciendo el bucle, pero con el param mode_t el bucle solo se ejecuta hasta el cuartos digito. Si
                //hay mas digitos, no se usan

                //Controlamos si se dibuja en este ciclo o no.
                //para ello, hacemos el chequeo de i entre 0 y 3
                if(i == 0){
                    //No hay que dibujar nada en este ciclo
                    boolDibujar = false;
                }else if (i == 1 || i == 3){
                    //el nombre de la imagen se forma juntando las posiciones 0 y 1 de
                    //Obtener la imagen a superponer como un bitmap
                    imagenParaSuperponerConOrigin = obtenerImagen.getImagenMethod(pathCesaralMagicImageC
                            +arrayImagesSequence[i-1] +arrayImagesSequence[i]+".bmp");

                    if(imagenParaSuperponerConOrigin == null){//No encuentra la imagen con extension .bmp
                        //Buscamos la imagen a superponer con extension .xbmp
                        Log.d(xxx, "metodo loopPrincipalImagenesTipoN, No existe la imagen a superponer: " +i +"con extension .bmp, buscamos con extension .xbmp");
                        imagenParaSuperponerConOrigin = obtenerImagen.getImagenMethod(pathCesaralMagicImageC
                                +arrayImagesSequence[i-1] +arrayImagesSequence[i]+".xbmp");

                        //
                        if(imagenParaSuperponerConOrigin == null) {
                            //Hay un error, terminamos la ejecucion he informamos con una notificacion
                            enviarNotification("Error con mode_t al recuperar imagen pequeña numero: "+(i-1) + i + ", saliendo de la aplicacion");
                            enviarNotificationConNumero("E1");
                            metodoMostrarError("E1", "Error when getting image file from external storage");
                            Log.d(xxx, "metodo loopPrincipalImagenesTipoN, fallo con imagen de dos digitos de mode_t,  jpg, imagenParaSuperponerConOrigin == null, salimos de la app");

                            //Acabamos la ejecucion
                            return false;
                        }
                    }
                }else if(i == 2){
                    //No hay que dibujar nada en este ciclo
                    boolDibujar = false;
                }else if(i > 3){
                    //No hay que dibujar nada cuando el indice es mayor que 3
                    boolDibujar = false;
                }
            }//FIN de if(datosConfigTxt.getMode_t().equals("1"))
            //FIN de 2 nov 2017 Parte 1, nuevo req param mode_t en CONFIG.txt en mail proximos requerimientos

            //***************************************************************************************************
            //***************************************************************************************************
            //Parte 1: 6 nov 2017, nuevo req en mail Plan lunes - Modo rotacional
            //Te paso los requerimientos del modo nuevo de rotación:
            //parámetro opcional MODE_C=1
            //Solo aplica al modo numerico

            Log.d(xxx, "metodo loopPrincipalImagenesTipoN, datosConfigTxt.getMode_t() es: " +datosConfigTxt.getMode_c());
            if(datosConfigTxt.getMode_c().equals("1")){
                //Chequear cuantos digitos tiene la cadena
                if(arrayImagesSequence.length < 4 || arrayImagesSequence.length > 4){
                    //Lanzamos error y salimos
                    enviarNotification("Error de param mode_c: numero de digitos es menor o mayor a 4 " +", saliendo de la aplicacion");
                    enviarNotificationConNumero("E1");
                    metodoMostrarError("E1", "Error in mode_c, wrong data format: number of digits should be 4");
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, Error de param mode_c: numero de digitos es menor o mayor a 4 , salimos de la app");

                    //Acabamos la ejecucion
                    return false;
                }else{
                    //La longitud del array es correcta, hay que chequear los rangos

                    //Imagina que el programa recibe los datos hhmm
                    // (siempre se recibirán 4 dígitos en este modo. En caso contrario
                    // no se hará nada y se reportará el error en el log: “Wrong data format”)

                    //Valores posibles:
                    //hh Puede tener los valores: 00, 01, 02, …,11
                    //mm Puede tener los valores: 00, 01, 02, …,59
                    //Si alguno de estos valores no está en este rango se reportará el error en el log: “Wrong hh:mm data format”)


                }

                //Solo vamos a dibujar en dos ciclos: i = 0 e i = 1, todos los demas ciclos no se dibujan
                String nombreFicheroImagen = "";
                if(i == 0){
                    //Chequea si mm esta fuera del rango 00-59

                    //Obtenemos el string mm
                    Character character = (Character)arrayImagesSequence[2];
                    nombreFicheroImagen = character.toString();
                    character = (Character)arrayImagesSequence[3];
                    nombreFicheroImagen += character.toString();
                    nombreImagenMM = nombreFicheroImagen;
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, mode_c imagen mm es: "  +nombreFicheroImagen);
                    //Verificamos el rango de mm
                    if(Integer.parseInt(nombreFicheroImagen) <= 59){
                        //mm esta dentro de rango, seguimos

                    }else{
                        //Error mm fuera de rango Lanzamos error y salimos
                        enviarNotification("Error de param mode_c: Wrong hh:mm data format " +", saliendo de la aplicacion");
                        enviarNotificationConNumero("E1");
                        metodoMostrarError("E1", "Error in mode_c: Wrong hh:mm data format");
                        Log.d(xxx, "metodo loopPrincipalImagenesTipoN, Error de param mode_c: Wrong hh:mm data format, error mm: " +nombreFicheroImagen);

                        //Acabamos la ejecucion
                        return false;
                    }

                }else if(i == 1){
                    //Chequea si hh esta fuera del rango 00-11

                    //Obtenemos el string mm
                    Character character = (Character)arrayImagesSequence[0];
                    nombreFicheroImagen = character.toString();
                    character = (Character)arrayImagesSequence[1];
                    nombreFicheroImagen += character.toString();
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, mode_c imagen hh es: "  +nombreFicheroImagen);
                    //Verificamos el rango de mm
                    if(Integer.parseInt(nombreFicheroImagen) <= 11){
                        //mm esta dentro de rango, seguimos

                    }else{
                        //Error mm fuera de rango Lanzamos error y salimos
                        enviarNotification("Error de param mode_c: Wrong hh:mm data format " +", saliendo de la aplicacion");
                        enviarNotificationConNumero("E1");
                        metodoMostrarError("E1", "Error in mode_c: Wrong hh:mm data format");
                        Log.d(xxx, "metodo loopPrincipalImagenesTipoN, Error de param mode_c: Wrong hh:mm data format, error hh: " +nombreFicheroImagen);

                        //Acabamos la ejecucion
                        return false;
                    }


                }else{
                    //Para todos los demas indices del loop, NO se dibuja nada
                    boolDibujar = false;
                }

                //Obtenemos la imagen, solo si i es 0 o 1, si no, no hay que dibujar nada
                if(i == 0 || i == 1){
                    imagenParaSuperponerConOrigin = obtenerImagen.getImagenMethod(pathCesaralMagicImageC
                            +nombreFicheroImagen+".bmp");

                    if(imagenParaSuperponerConOrigin == null){//No encuentra la imagen con extension .bmp
                        //Buscamos la imagen a superponer con extension .xbmp
                        Log.d(xxx, "metodo loopPrincipalImagenesTipoN, No existe la imagen a superponer: " +"con extension .bmp, buscamos con extension .xbmp");
                        imagenParaSuperponerConOrigin = obtenerImagen.getImagenMethod(pathCesaralMagicImageC
                                +nombreFicheroImagen+".xbmp");

                        //
                        if(imagenParaSuperponerConOrigin == null) {
                            //Hay un error, terminamos la ejecucion he informamos con una notificacion
                            enviarNotification("Error con mode_c al recuperar imagen pequeña numero: " + ", saliendo de la aplicacion");
                            enviarNotificationConNumero("E1");
                            metodoMostrarError("E1", "Error in mode_c when getting image file from external storage");
                            Log.d(xxx, "metodo loopPrincipalImagenesTipoN, fallo con imagen de dos digitos de mode_c, imagenParaSuperponerConOrigin == null, salimos de la app");

                            //Acabamos la ejecucion
                            return false;
                        }
                    }
                }

                //Rotamos, solo si i es 0 o 1, si no, no hay que dibujar nada
                if(i == 0){
                    //Rotamos mm
                    //Primero cogerá la imagen m.xbmp , la rotará hacia la derecha los grados (mm x 6), teniendo en cuenta
                    // el centro de la imagen pasada, y lo insertará en las coordenadas indicadas por N1
                    float floatRotarMM = Integer.parseInt(nombreImagenMM) * 6;
                    imagenParaSuperponerConOrigin = RotateBitmap(imagenParaSuperponerConOrigin, floatRotarMM);

                }else if(i == 1){
                    float floatRotarHH = (Integer.parseInt(nombreFicheroImagen) * 30)+ (Integer.parseInt(nombreImagenMM) /2);
                    imagenParaSuperponerConOrigin = RotateBitmap(imagenParaSuperponerConOrigin, floatRotarHH);
                }


            }//FIN de if(datosConfigTxt.getMode_c().equals("1"))

            //FIN Parte 1: de 6 nov 2017, nuevo req en mail Plan lunes - Modo rotacional
            //***************************************************************************************************
            //***************************************************************************************************


            if(imagenParaSuperponerConOrigin == null){
                //Hay un error, terminamos la ejecucion he informamos con una notificacion
                enviarNotification("Error al recuperar imagen pequeña numero: " +i +", saliendo de la aplicacion");
                enviarNotificationConNumero("E1");
                metodoMostrarError("E1", "Error when getting image file from external storage");
                Log.d(xxx, "metodo loopPrincipalImagenesTipoN, fallo con imagen 0-9 jpg, imagenParaSuperponerConOrigin == null, salimos de la app");

                //Acabamos la ejecucion
                return false;
            }else{
                //Continuamos con el procesamiento
                //Se muestra la imagen pequeña en la UI, solo para pruebas
                //ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
                //imageView2.setImageBitmap(imagenParaSuperponerConOrigin);

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


                //2 nov 2017 Parte 2, nuevo req param mode_t en CONFIG.txt en mail proximos requerimientos
                //solo aplica a cadenas numericas con 4 digitos, los demas no valen
                //Solo se usan las coordenadas N1 y N2
                if(datosConfigTxt.getMode_t().equals("1")){
                    if(i == 1){//Obtenemos N1
                        xFloat = Float.parseFloat(listaCoordenadas.get(0).getCoordX());
                        yFloat = Float.parseFloat(listaCoordenadas.get(0).getCoordY());

                    }else if(i == 3){//Obtenemos N2
                        xFloat = Float.parseFloat(listaCoordenadas.get(1).getCoordX());
                        yFloat = Float.parseFloat(listaCoordenadas.get(1).getCoordY());

                    }
                }//FIN de 2 nov 2017 Parte 2, nuevo req param mode_t en CONFIG.txt en mail proximos requerimientos




                //Chequear que xFloat y yFloat son validos, si no, cerrar el programa
                //Float.isNaN retorna true si no es un numero
                if(Float.isNaN(xFloat) || Float.isNaN(yFloat)){
                    enviarNotification("Error, coordenadas no son un numero valido, saliendo de la aplicacion");
                    enviarNotificationConNumero("E1");
                    metodoMostrarError("E1", "Error with coordenates N: some coordenate is not a number");
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, Error en coordenadas x o y no son un numero, revisar CONFIG.txt, salimos de la app");

                    return false;//Cerrar aplicacion y evitar fallo en el procesamiento
                }

                //**************************************************************************************
                //**************************************************************************************
                //**************************************************************************************
                //req de offset el 25 oct 2017, modificar coordenadas de acuerdo a offset_x, offset_y y sacale_x
                DatosConfigTxt datosConfigTxtLocal = new DatosConfigTxt(IntentServiceMagic.this);
                xFloat = datosConfigTxtLocal.modificarCoordenadaX(xFloat, doubleScale_x, intOffset_x);
                yFloat = datosConfigTxtLocal.modificarCoordenadaY(yFloat, intOffset_y);

                //FIN req de offset el 25 oct 2017
                //**************************************************************************************
                //**************************************************************************************
                //**************************************************************************************


                //Nuevo requerimiento, centrado de cadenas/numeros recibido el 26 oct 2017
                //Modificamos xFloat con offsetX_ParaCentrarN:
                if(boolUsarCenter) {
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, xFloat sin centrado: " + xFloat);
                    xFloat = xFloat + offsetX_ParaCentrarN;
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, xFloat con centrado: " + xFloat);
                }

                //****************************************************************************************
                //****************************************************************************************
                //****************************************************************************************
                //Nuevo req recibido el 27oct17, parametro m_x
                //Se trata de un parámetro opcional para el modo numérico solamente, denominado M_X
                if(boolMX){
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, boolMX es true: " + boolMX);

                    //Chequeo de las posiciones pares para detectar si hay que modificar
                    if ((i+1) % 2 == 0) {
                        //Las posiciones pares, modifican su coordenada X con el
                        //parametro M_X
                        //Chequeamos si la posicion i del array numerico es cero
                        if (arrayImagesSequence[i-1] == '0') {
                            //si se cumple estas condiciones, hay que modificar la coordenada xFloat
                            Log.d(xxx, "metodo loopPrincipalImagenesTipoN, posicion par: " + i);
                            Log.d(xxx, "metodo loopPrincipalImagenesTipoN, hay que modificar la coordenada X de i");
                            Log.d(xxx, "metodo loopPrincipalImagenesTipoN, xFloat ANTES DE USAR M_X" + xFloat);
                            xFloat = xFloat - intMX;
                            Log.d(xxx, "metodo loopPrincipalImagenesTipoN, xFloat DESPUES DE USAR M_X" + xFloat);
                        }
                    }

                    //Chequeo de las posiciones impares para detectar si hay que mezclar la imagen de esta posicion
                    if ((i+1) % 2 != 0) {
                        //Chequea si hay un cero en i = 0, 2, 4, 6, etc
                        if (arrayImagesSequence[i] == '0') {
                            //No dibujar esta imagen
                            Log.d(xxx, "metodo loopPrincipalImagenesTipoN, NO dibujar");
                            boolDibujar = false;
                        }

                    }

                }
                //****************************************************************************************
                //****************************************************************************************
                //****************************************************************************************


                //parte 2, 2 nov 2017, Posicionamiento de ficheros en modo proporcional, parametro CENTER_P=nnn,
                // nuevo req del mail proximos requerimientos
                //Afecta a la cadena de numeros y alfanumericos
                if(datosConfigTxt.getIntCenter_p() != 0){
                    //Calculo de la coordenada xFloat
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, CENTER_P=nnn existe y xFloat original es: " +xFloat );
                    xFloat = posicionamientoProporcional.center_pGetPosicionX(datosConfigTxt.getIntCenter_p(), i,
                            arrayAnchuraImagenesPequeñas, anchoTotalDeLasImagenesPequenas);
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoN, CENTER_P=nnn existe y xFloat NUEVO es: " +xFloat );


                }//FIN parte 2, 2 nov 2017, Posicionamiento de ficheros en modo proporcional, parametro CENTER_P=nnn,


                //***********************************************************************************************
                //***********************************************************************************************
                //Parte dos: 6 nov 2017, nuevo req en mail Plan lunes - Modo rotacional
                //Te paso los requerimientos del modo nuevo de rotación:
                //parámetro opcional MODE_C=1
                //Solo aplica al modo numerico

                Log.d(xxx, "metodo loopPrincipalImagenesTipoN, mode_c asignamos las coordenadas");
                if(datosConfigTxt.getMode_c().equals("1")){
                    if(i == 0){//Obtenemos N2
                        xFloat = Float.parseFloat(listaCoordenadas.get(1).getCoordX());
                        yFloat = Float.parseFloat(listaCoordenadas.get(1).getCoordY());

                    }else if(i == 1){//Obtenemos N1
                        xFloat = Float.parseFloat(listaCoordenadas.get(0).getCoordX());
                        yFloat = Float.parseFloat(listaCoordenadas.get(0).getCoordY());

                    }
                }
                //FIN Parte dos: 6 nov 2017, nuevo req en mail Plan lunes - Modo rotacional
                //***********************************************************************************************
                //***********************************************************************************************



                //Mezclar la imagen pequeña con origin.jpg en las coordenada que corresponden en CONGIG.txt
                if(boolDibujar) {
                    //Mezclar la imagen pequeña con origin.jpg en las coordenada que corresponden en CONGIG.txt
                    mergedImages = createSingleImageFromMultipleImagesWithCoord(originJpg, imagenParaSuperponerConOrigin,
                            xFloat, yFloat);
                    //En cada pasada, originJpg se tiene que refrescar con las imagenes mezcladas.
                    originJpg = mergedImages;
                    if (mergedImages != null) {
                        //Comando de prueba. Comentar esta linea en la version final
                        //collageImage.setImageBitmap(mergedImages);
                    } else {
                        //Ha habido un error al mezclar las imagenes
                        enviarNotification("Error mezclando imagen: " + i + ", saliendo de la aplicacion");
                        enviarNotificationConNumero("E1");
                        metodoMostrarError("E1", "Error mixing images");
                        Log.d(xxx, "metodo loopPrincipalImagenesTipoN, mergedImages es null, no se ha generado la imagen, salimos de la app");

                        return false;

                    }
                }//Fin de boolDibujar

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
                //Siempre chequeo que i no sea mayor o igual que la lista de coordenadas, por si acaso
                //el fichero CONFIG.txt no tiene las 16 coordenadas sino un numero menor.
                if(i >= arrayPojoCoordenadasAlfanumerico.size()){
                    enviarNotification("Error en indice de coordenadas alfanumericas, saliendo de la aplicacion");
                    enviarNotificationConNumero("E1");
                    metodoMostrarError("E1", "Error in index of T coordenates");
                    Log.d(xxx, "metodo loopPrincipalImagenesTipoT, Error en indice de coordenadas, salimos de la app");
                    return false;//Cerrar aplicacion y evitar un null pointer
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

        int integerSOR = 0;
        try {
            integerSOR = Integer.parseInt(stringSOR);
        } catch (NumberFormatException e) {
            Log.d(xxx, "En ejecutarConParametroSor, SOR= " +integerSOR  +", ERROR: el sor No es un numero");
            //Si da este fallo, asignamos el valor de SOR a cualquier valor fuera del rango 1-6
            integerSOR=0;
        }
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

    //ArrayList para guardar las coordenadas alfanumericas, es un variable de la clase
    ArrayList<PojoCoordenadas> arrayPojoCoordenadasAlfanumerico = new ArrayList<>();



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
                Log.d(xxx, "xxx, Hay una linea que empieza con web y tiene: " +arrayLineasTextoLocal.get(i));
                arrayStringUser = arrayLineasTextoLocal.get(i).split(regexUser);
            }
            if(arrayLineasTextoLocal.get(i).startsWith("password")){
                Log.d(xxx, "xxx, Hay una linea que empieza con web y tiene: " +arrayLineasTextoLocal.get(i));
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
                //Solo busca pixeles blancos
                if(bitmap2.getPixel(x, y)==Color.rgb(0xff, 0xff, 0xff))
                //if(bitmap2.getPixel(x, y)<=Color.rgb(0xd7, 0xd7, 0xd7))

                //tenia esta hasta el nuevo req de solo comparar con pixeles blancos el 27oct17
                //if(bitmap2.getPixel(x, y)>= Color.rgb(0xd7, 0xd7, 0xd7))
                {
                    int alpha = 0x00;
                    bitmap2.setPixel(x, y , Color.argb(alpha,0xff,0xff,0xff));  // changing the transparency of pixel(x,y)
                }
            }
        }
        return bitmap2;
    }


    //6 nov 2017, nuevo req en mail Plan lunes - Modo rotacional
    //Te paso los requerimientos del modo nuevo de rotación:
    //parámetro opcional MODE_C=1
    //Solo aplica al modo numerico
    //Como en:
    //https://stackoverflow.com/questions/9015372/how-to-rotate-a-bitmap-90-degrees
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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

                //Exito
                //finish();

                //Nuevo req 28oct17: fichero de log
                escribirDatosEnLog("OK");

            }else{
                Log.d(xxx, "En onPostExecute: FAIL, Imagen NO enviada al servidor ftp, saliendo de la app");
                //progressBar.setVisibility(View.INVISIBLE); //To Hide ProgressBar
                //Cerrar aplicacion, ha habido un fallo
                //finish();

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
                //collageImage.setImageBitmap(mergedImages);

                //Lanzamos el asynctask de enviar imagen al servidor con ftp
                //new IntentServiceMagic.FtpAsyncTask().execute("string1", "string2", "string3");

                //Lanzamos el asynctask de enviar imagen al servidor con ftp
                //pero solo si la variable urlServidor es distinta de ""
                if(urlServidor.equals("")){
                    //Do nothing else, stop here
                    escribirDatosEnLog("2");
                }else{//Then continue with ftp
                    new IntentServiceMagic.FtpAsyncTask().execute("string1", "string2", "string3");
                }

            }else{
                Log.d(xxx, "En onPostExecute: FAIL, Imagen jpg NO generada, saliendo de la app");
                //progressBar.setVisibility(View.INVISIBLE); //To Hide ProgressBar
                //Cerrar aplicacion, ha habido un fallo
                //finish();

                //Nuevo req 28oct17: fichero de log
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
    //       Final         Copia de todo lo que esta fuera del onCreate de MezclarFinal


    //*************************************************************************************
    //*************************************************************************************
    //*************************************************************************************
    //Nuevo req 28oct17: fichero de log
    //Metodo para mostrar mensajes de error E1 y E2 por pantalla
    String stringTipoDeError = "";//Tb se envia a txt.log
    String stringMensajeDeError = "";//Tb se envia a txt.log
    //Metodo para mostrar mensajes de error E1 y E2 por pantalla
    //Desde el service no puedo tocar componenetes de la UI
    private void metodoMostrarError(String tipoDeError, String mensaje){
        Log.d(xxx, "estoy en metodoMostrarError, mensaje: " +mensaje);
        //textViewErrores.setText(tipoDeError +": " +mensaje);


        //Mensajes a poner en el log.txt
        stringTipoDeError = tipoDeError;
        stringMensajeDeError = mensaje;

    }

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


    String  mensajeLog2="";
    //Nuevo req 28oct17: fichero de log
    private void escribirDatosEnLog(String  mensajeError){
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
                +cadenaNumericaEmpleada +separador +"No data" +separador +mensajeError +"\r\n";
        Log.d(xxx, "escribirDatosEnLog el mensaje del log es: " +mensajeLog);

        mensajeLog2 = mensajeLog;

        //Asi no funcione, hay que hacerlo con looper
        /*
        EscribirEnFicheroTxt escribirEnFicheroTxt = new EscribirEnFicheroTxt(IntentServiceMagic.this);
        if(escribirEnFicheroTxt.appendDateEnFichero(pathCesaralMagicImageC + "log.txt", mensajeLog)){

            Log.d(xxx, "escribirDatosEnLog fichero escrito correctamente");

        }else{
            Log.d(xxx, "escribirDatosEnLog fichero NO escrito correctamente");


        }
        */

        //Escribir en el fichero log.txt hay que hacerlo con looper
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                EscribirEnFicheroTxt escribirEnFicheroTxt = new EscribirEnFicheroTxt(IntentServiceMagic.this);
                if(escribirEnFicheroTxt.appendDateEnFichero(pathCesaralMagicImageC + "log.txt", mensajeLog2)){

                    Log.d(xxx, "escribirDatosEnLog fichero escrito correctamente");

                }else{
                    Log.d(xxx, "escribirDatosEnLog fichero NO escrito correctamente");


                }

            }
        });
    }
    //*************************************************************************************
    //*************************************************************************************
    //*************************************************************************************
    //FIN Nuevo req 28oct17: fichero de log

//FINAL de la clase *****************************************************************************************
}//Fin de IntentServiceMagic
