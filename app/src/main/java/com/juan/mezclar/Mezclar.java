package com.juan.mezclar;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.BoolRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

//Para gestionar imagenes
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.juan.mezclar.ftpClases.FtpClient;
import com.juan.mezclar.retrofit.ServicioRetrofit2;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;



//NOTA FINAL: CLASE DEPRECADA, LA MANTENGO PARA FUTURAS REFERENCIAS (TIENE EL CODIGO DE RETROFIT)
//USAR LA CLASE MezclarFinal


//*************************************************************************
//Notas del 8 oct 2017
//Cosas que me faltan:

//leer el fichero config y guardar las coordenadas x e y en dos arrays String, lo he hecho en parte
//Almacenar la foto en internet
//Decidir como manejar la app Mezclar: la ejecuto sin interfaz grafica o pongo un servicio.
//Ver como poner mas de una linea en las notificaciones
//Adecentar un poco la UI de LaunchMezclar y ver como posicionar los controles en un ConstraintLayout y que
//   no se puedan escribir mas de 16 digitos
//Este es el commit 4322

public class Mezclar extends AppCompatActivity {
    //String para usar en log.d con el nombre de la clase
    String xxx = this.getClass().getSimpleName();

    private ImageView collageImage;
    private ImageView finalImage;
    //Array para almacenar la secuencia de imagenes a superponer
    char[] arrayImagesSequence;
    //String de secuencia de imagenes inicializada con la imagen 0.
    String stringImagesSecuence; //Para prueba con el array vacio
    //String stringImagesSecuence = "0";
    //Path a agregar al dir raiz del telefono
    String pathCesaralMagicImageC = "/CesaralMagic/ImageC/";
    String imagenPrincipal = "origin.jpg";
    String ficheroConfigTxt = "CONFIG.txt";
    ProgressBar progressBar;
    FloatingActionButton fabOnPostExecute;//Para deshabilitar el boton FAB si la ejecucion es completa

    //Fichero de prueba para probar fallo de ArrayIndexOutOfBoundsException
    //String ficheroConfigTxt = "CONFIG[1].txt";

    //Fichero de prueba para probar fallo de URL no valida
    //String ficheroConfigTxt = "CONFIG[1][1].txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Obtener datos iniciales. Si no hay datos, cerrar la app
        recuperarIntentConDatosIniciales();


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
        Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Secuencia de imágenes recibidas: " +stringImagesSecuence, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        //Llamo al metodo desde el Floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        });


        Log.d(xxx, "Hola " );
        collageImage = (ImageView)findViewById(R.id.imageView3);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        //Boton anulado. Uso el fab
        /*Button combineImage = (Button)findViewById(R.id.combineimage);
        combineImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodoPrincipal();
            }
        });
        */

    }//Fin del onCreate

    //Metodo que recupera los datos recibidos en un intent lanzado por otra aplicacion,
    //por ejemplo, Launch Mezclar.
    //Si el intent es nulo, o no hay datos, la app se cierra automaticamente
    public String textoSnackBarInicial;
    private void recuperarIntentConDatosIniciales(){
        //Recibir datos de la app Launh Mezclar
        //String myString;
        Bundle data = getIntent().getExtras();
        if(data!=null){
            String myString = data.getString("KeyName");
            //Hay que chequear myString para que no lanze el toast with null cuando lanzo la app desde el movil
            if(myString!=null && !myString.isEmpty()) {
                //Copiamos la secuencia de imagenes recibidas
                stringImagesSecuence = null;
                stringImagesSecuence = myString;
                //Toast.makeText(this, myString, Toast.LENGTH_SHORT).show();

                Log.d(xxx, "Datos de Launch Mezclar: " + stringImagesSecuence);
                //Muestro el string character a character
                for(int i = 0; i < stringImagesSecuence.length(); i++) {
                    Log.d(xxx, "Caracter " +i +":" + stringImagesSecuence.charAt(i));
                }

                //Convertir el string de secuencia de imagenes en un array de secuencia de imagenes, character a character
                arrayImagesSequence = stringImagesSecuence.toCharArray();
                //Lo muestro con
                for (char temp : arrayImagesSequence) {
                    Log.d(xxx, "Caracter " +temp);
                }//OK
            }else{//Salta aqui si no hay datos en el intent
                Log.d(xxx, "Datos de Launch Mezclar: No hay datos");
                //Datos fake para probar
                //Si la app no ha sido abierta desde otra app, Launh Mezclar en mi caso, la cierro automaticamente
                this.finish();

            }

        }else{//Salta aqui si recibe nulo en el intent
            Log.d(xxx, "Datos de Launch Mezclar: NULL 2 del else");
            //Si la app no ha sido abierta desde otra app, Launh Mezclar en mi caso, la cierro automaticamente
            this.finish();
        }
    }

    //ESte metodo mezcla dos imagenes que estan en la carpeta drawable. Es solo para probar
    private void metodoPrincipal(){

                Bitmap bigImage = BitmapFactory.decodeResource(getResources(), R.drawable.imagen11);
                Bitmap smallImage = BitmapFactory.decodeResource(getResources(), R.drawable.imagen2);
                enviarNotification("1");
                smallImage = changeSomePixelsToTransparent(smallImage);
                enviarNotification("2");
                Bitmap mergedImages = createSingleImageFromMultipleImages(bigImage, smallImage);
                collageImage.setImageBitmap(mergedImages);
                enviarNotification("3");
                GuardarImagen guardarImagen = new GuardarImagen(Mezclar.this, mergedImages);
                guardarImagen.guardarImagenMethod();
    }

    //Metodo final y OK
    ObtenerImagen obtenerImagen;
    private boolean metodoPrincipal_2(){
        progressBar.setVisibility(View.VISIBLE); //To Hide ProgressBar

        //Chequeo el array de secuencia de imagenes: si es null o esta vacio, termina el programa
        if (arrayImagesSequence != null) {
            if (arrayImagesSequence.length == 0) {
                //Hay un error, terminamos la ejecucion he informamos con una notificacion
                enviarNotification("Error: el array de imagenes esta vacio, saliendo de la aplicacion");
                return false;
            }else{
                //El array de sequencia existe, continuamos
            }
        }else
        {
            enviarNotification("Error: el array de imagenes es null, saliendo de la aplicacion");
            return false;
        }

        //Obtener todas las lineas del fichero CONFIG.txt en el dir del dispositivo: pathCesaralMagicImageC
        LeerFicheroTxt leerFicheroTxt = new LeerFicheroTxt(Mezclar.this);
        List<String> arrayLineasTexto = leerFicheroTxt.getFileContentsLineByLineMethod(pathCesaralMagicImageC + ficheroConfigTxt);
        if(arrayLineasTexto == null){
            Log.d(xxx, "arrayLineasTexto es null");
            //Hay un error, terminamos la ejecucion he informamos con una notificacion
            enviarNotification("Error 1 al recuperar CONFIG.txt, saliendo de la aplicacion");
            return false;
        }

        if(arrayLineasTexto.isEmpty()){
            Log.d(xxx, "arrayLineasTexto esta vacio");
            //Hay un error, terminamos la ejecucion he informamos con una notificacion
            enviarNotification("Error 1 al recuperar CONFIG.txt, saliendo de la aplicacion");
            return false;
        }

        //Recorro y muestro la lista con el contenido de CONFIG.txt, solo para pruebas
        /*
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
        } */

        //Leer coordenadas y URL del array de lineas obtenido del fichero CONFIG.txt
        List<PojoCoordenadas> listaCoordenadas = generarPojoGenerarUrl(arrayLineasTexto);

        //Caso de try/ctach para ArrayIndexOutOfBoundsException
        if(listaCoordenadas == null){
            //Este error indica que alguna coordenada x, y no es un numero valido, alomejor es
            //un error tipografico, como poner una letra en vez de un digito.
            enviarNotification("Error ArrayIndexOutOfBoundsException, saliendo de la aplicacion");
            return false;
        }

        if(listaCoordenadas.isEmpty()){
            //Hay un fallo en CONFIG text y no se han leido las coordenadas
            //Enviar notificacion de error y cerrar programa
            enviarNotification("Error al recuperar coordenadas, saliendo de la aplicacion");
            return false;
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
        obtenerImagen = new ObtenerImagen(Mezclar.this);
        Bitmap originJpg = obtenerImagen.getImagenMethod(pathCesaralMagicImageC + imagenPrincipal);
        if(originJpg == null){
            //Hay un error, terminamos la ejecucion he informamos con una notificacion
            enviarNotification("Error al recuperar origin.jpg, saliendo de la aplicacion");
            return false;
        }
        //Se muestra origin.jpg en la UI
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(originJpg);
        //Instruccion para cargar directamente de la memoria una imagen
        //imageView.setImageBitmap(BitmapFactory.decodeFile(pathToPicture));

        //Loop principal de la aplicacion
        Bitmap imagenParaSuperponerConOrigin;
        Bitmap mergedImages = null;
        for(int i = 0; i < arrayImagesSequence.length; i++) {
            Log.d(xxx, "mezclando imagen: " +i);
            enviarNotification("mezclando imagen: " +i);
            //Obtener la imagen a superponer como un bitmap
            imagenParaSuperponerConOrigin = obtenerImagen.getImagenMethod(pathCesaralMagicImageC
                    +arrayImagesSequence[i]+".jpg");
            if(imagenParaSuperponerConOrigin == null){
                //Hay un error, terminamos la ejecucion he informamos con una notificacion
                enviarNotification("Error al recuperar imagen pequeña numero: " +i +", saliendo de la aplicacion");
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
                //Siempre chequeo que i no sea mayor o igual que la lista de coordenadas, por si acaso
                //el fichero CONFIG.txt no tiene las 16 coordenadas sino un numero menor.
                if(i >= listaCoordenadas.size()){
                    enviarNotification("Error en indice de coordenadas, saliendo de la aplicacion");
                    return false;//Cerrar aplicacion y evitar un null pointer
                }
                xFloat = Float.parseFloat(listaCoordenadas.get(i).getCoordX());
                yFloat = Float.parseFloat(listaCoordenadas.get(i).getCoordY());

                //Chequear que xFloat y yFloat son validos, si no, cerrar el programa
                //Float.isNaN retorna true si no es un numero
                if(Float.isNaN(xFloat) || Float.isNaN(yFloat)){
                    enviarNotification("Error, coordenadas no son un numero valido, saliendo de la aplicacion");
                    return false;//Cerrar aplicacion y evitar fallo en el procesamiento
                }


                //Mezclar la imagen pequeña con origin.jpg en las coordenada que corresponden en CONGIG.txt
                mergedImages = createSingleImageFromMultipleImagesWithCoord(originJpg, imagenParaSuperponerConOrigin,
                                        xFloat, yFloat);
                //En cada pasada, originJpg se tiene que refrescar con las imagenes mezcladas.
                originJpg = mergedImages;
                if(mergedImages != null) {
                    //Comando de prueba. Comentar esta linea en la version final
                    collageImage.setImageBitmap(mergedImages);
                }else{
                    //Ha habido un error al mezclar las imagenes
                    enviarNotification("Error mezclando imagen: " +i  +", saliendo de la aplicacion");
                    return false;

                }
                //
            }

        }//Fin del loop principal

        //Ejecucion correcta, guardar imagen en la memoria externa del dispoositivo
        GuardarImagenFinal guardarImagenFinal = new GuardarImagenFinal(Mezclar.this, mergedImages, 5);
        //Guardar imagen el directorio pictures/predict
        //No hace falta, guardo directamente en DCIM/predict
        /*
        if (!guardarImagenFinal.guardarImagenMethod(Environment.DIRECTORY_PICTURES, "/predict/", "predict.jpg")){
            //Ha habido un error al guardar la imagen, devolver false
            enviarNotification("Error guardando imagen predict" +" ,saliendo de la aplicacion");
            return false;
        } */

        //Guardar imagen en el directorio DCIM/predict
        if (!guardarImagenFinal.guardarImagenMethod(Environment.DIRECTORY_DCIM, "/predict/", "predict.jpg")){
            //Ha habido un error al guardar la imagen, devolver false
            enviarNotification("Error guardando imagen predict" +", saliendo de la aplicacion");
            return false;
        }

        //Return true al final del metodo. La app se queda abierta, esperando el resultado de la subida de predict.jpg con ftp
        enviarNotification("Imagen guardada en /DCIM/predict/  Ejecucion correcta" +"\n" +"Esperando resultado ftp...");

        //Enviar predict.jpg al server con POST
        //java.lang.IllegalArgumentException: baseUrl must end in /: http://www.cesaral.com/test
        //subirImagenConRetrofit2(obtenerImagen, urlServidor +"/");

        //**************************************
        //Solo falta activar un progress bar y ejecutar el asynctask para enviar la imagen al servidor con ftp
        new FtpAsyncTask().execute("string1", "string2", "string3");
        //**************************************



        //Provisionalmente devuelve true aqui
        return true;

        //Dejo este codigo comentado hasta que Cesar arregle lo del acceso al ftp server
        /*
        if(metodoSubirImagenConFtp(obtenerImagen)){
            return true;
        }else{
            return false;
        }*/



    }//Fin de metodoPrincipal_2



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

    //Metodo para:
    //Generar array de PojoCoordenadas con las coordenadas x e Y de posicionamiento de imagenes
    //Generar la URL para subir y almacenar la imagen generada a un servidor
    String urlServidor;
    private List<PojoCoordenadas> generarPojoGenerarUrl(List<String> arrayLineasTextoLocal){
        ArrayList<PojoCoordenadas> arrayPojoCoordenadas = new ArrayList<>();
        String regex = "[^\\d]+";
        for(int i = 0; i < arrayLineasTextoLocal.size(); i++){
            //Extrae las coordenadas x e y de cada linea con regex y genera pojo de
            //coordenadas por cada linea y lo guarda en el array de coordenadas
            String[] str = arrayLineasTextoLocal.get(i).split(regex);
            //Si la linea no tiene digitos, hago un break y continua el loop
            if(str.length == 0) break;
            PojoCoordenadas pojoCoordenadas = new PojoCoordenadas();
            try {
                pojoCoordenadas.setCoordX(str[2]);
                pojoCoordenadas.setCoordY(str[3]);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                Log.d(xxx, "ArrayIndexOutOfBoundsException:  " +e.getMessage());
                return null;
            }
            arrayPojoCoordenadas.add(pojoCoordenadas);
        }

        //Este for extrae la URL del servidor
        String[] stringURLFinal = null;
        for(int i = 0; i < arrayLineasTextoLocal.size(); i++){
            //Obtener URL del Servidor para almacenar imagen generada
            String regexUrl = "web=";
            stringURLFinal = arrayLineasTextoLocal.get(i).split(regexUrl);
        }

        if(stringURLFinal != null) {
            int i = 0;
            for (String st : stringURLFinal) {
                Log.d(xxx, "xxx Dato en stringURLFinal " + i + " es: " + st);
                urlServidor = stringURLFinal[i];
                i++;
            }
        }

        //Imprime las coordenadas
        for (int i = 0; i < arrayPojoCoordenadas.size(); i++ ){
            Log.d(xxx, "Coordenada X en arraPojo " + i + " es: " + arrayPojoCoordenadas.get(i).getCoordX()
                            +"\n"
                             +"Coordenada y en arraPojo " + i + " es: " + arrayPojoCoordenadas.get(i).getCoordY());
        }

        //Imprime la url
        if(stringURLFinal != null) {
            int i = 0;
            for (String st : stringURLFinal) {
                Log.d(xxx, "xxx Dato en stringURLFinal " + i + " es: " + st);
                urlServidor = stringURLFinal[i];
                i++;
            }
        }


        return arrayPojoCoordenadas;
    }//Fin de generarPojoGenerarUrl


    //Coordenadas globales para colocar la imagen transparente sobre origin.jpg
    private float xFloat;
    private float yFloat;
    //Este metodo es solo para hacer pruebas
    private void leerCoordenadasDeSuperposicion(int i){
        //TODO: extraer las coordenadas del fichero CONFIG.text en CesaralMagic/ImageC


        //Este es manual, con las coordenadas de CONFIG:
        switch (i){
            case 0://N1
                xFloat = 94;
                yFloat = 1;
                break;
            case 1://N2
                xFloat = 115;
                yFloat = 1;
                break;
            case 2://N3
                xFloat = 94;
                yFloat = 27;
                break;
            case 3://N4
                xFloat = 115;
                yFloat = 27;
                break;
            case 4://N5
                xFloat = 94;
                yFloat = 53;
                break;
            case 5://N6
                xFloat = 115;
                yFloat = 53;
                break;
            case 6://N7
                xFloat = 94;
                yFloat = 79;
                break;
            case 7://N8
                xFloat = 115;
                yFloat = 79;
                break;
            case 8://N9
                xFloat = 94;
                yFloat = 105;
                break;
            case 9://N10
                xFloat = 115;
                yFloat = 105;
                break;
            case 10://N11
                xFloat = 94;
                yFloat = 173;
                break;
            case 11://N12
                xFloat = 115;
                yFloat = 173;
                break;
            case 12://N13
                xFloat = 94;
                yFloat = 199;
                break;
            case 13://N14
                xFloat = 115;
                yFloat = 199;
                break;
            case 14://N15
                xFloat = 0;
                yFloat = 0;
                break;
            case 15://N16
                xFloat = 0;
                yFloat = 50;
                break;

        }

    }


    //Metodo de prueba
    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage){

        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, 10, 10, null);
        return result;
    }

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

    private void enviarNotification(String mensaje){
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

        mNotificationManager.notify(001, mBuilder.build());
    }

    private void enviarNotificationFtp(String mensaje){
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

        mNotificationManager.notify(002, mBuilder.build());
    }



    //Metodo para operar con ftp y subir el la imagen predict.jpg al servidor:
    //http://ftp.cesaral.com/test/
    //Como en: http://tutoandroidblog.blogspot.com.es/2013/01/servidor-ftp-de-subida-de-archivo.html


    private boolean metodoSubirImagenConFtp(ObtenerImagen obtenerImagen) {

        //Campos----------------------------------------------------

        //EditText nombreArhivo;        // Almacena el id del componente donde está localizado el nombre del archivo a subir
        //Button subir;                // Almacena el id del componente donde está localizado el botón para subir el archivo

        //Credenciales
        String ip;                    //Almacena la direción ip del servidor
        String usuario;                //Almacena el user
        String contrasena;            //Almacena la contraseña

        FtpClient ftp;                    //Instancia manejador ftp

        //-----------------------------------------------------------
        //Inicializa las credenciales
        //ip = "192.168.0.1";
        //user = "admin";
        //password = "admin";

        //ip = "ftp.cesaral.com/test";
        ip = urlServidor;
        usuario = "textx";
        contrasena = "test.2017";

        //**********************************************
        //Mi codigo:
        //File filePathDePredictJpg = obtenerImagen.getFilePathOfPicture(Environment.DIRECTORY_DCIM, "/predict/", "predict.jpg");
        File filePathDePredictJpg = obtenerImagen.getFilePathOfPicture(Environment.DIRECTORY_DCIM, "/predict/", "predict.jpg");

        if(filePathDePredictJpg == null){
            enviarNotificationFtp("Error al obtener el file de predict.jpg para upload ftp" +", saliendo de la aplicacion");
            return false;

        }else {//Hemos obtenido el file de la imagen a subir, seguimos

            //Chequeamos que el path a predict.jpg es correcto:
            Log.d(xxx, "Path a predict.jpg para enviar al servidor con ftp: " + filePathDePredictJpg.getName());
            Log.d(xxx, "Absolute Path a predict.jpg para enviar al servidor con ftp: " + filePathDePredictJpg.getAbsolutePath());



        //**********************************************


        //Establece los ids de la vista
        //nombreArhivo = (EditText) findViewById(R.id.edtxtNombreArchivo);
        //subir = (Button) findViewById(R.id.btnSubir);

        //Evento OnClick (btnSubir)
        //subir.setOnClickListener(new OnClickListener() {

            //public void onClick(View v) {

                //Establece un servidor
                ftp = new FtpClient(ip, usuario, contrasena, getApplicationContext());

                //Realiza login en el servidor

                try {
                    if(ftp.login(usuario, contrasena)){
                        //Login correcto, enviamos el fichero con el try catch de abajo
                    }else{
                        enviarNotificationFtp("Error: El login o la conexion al servidor ftp ha fallado" +", saliendo de la aplicacion");
                        Log.d(xxx, "Error: El login o la conexion al servidor ftp ha fallado" +", saliendo de la aplicacion");
                        return false;
                    }
                } catch (SocketException e) {
                    //e.printStackTrace();
                    enviarNotificationFtp("Error Socket Exception en ftp login: " +e.getMessage() +", saliendo de la aplicacion");
                    Log.d(xxx, "Error Socket Exception en ftp login: " +e.getMessage());

                    return false;
                } catch (IOException e) {
                    //e.printStackTrace();
                    enviarNotificationFtp("Error IOException en ftp login: " +e.getMessage() +", saliendo de la aplicacion");
                    Log.d(xxx, "Error IOException en ftp login: " +e.getMessage());

                    return false;
                }

                //Sube el archivo al servidor
                try {
                    //if(ftp.enviarFile(nombreArhivo)){
                    //if(ftp.enviarFile("predict.jpg")){
                    if(ftp.enviarFileFinalFinal(filePathDePredictJpg, "predict.jpg")){
                        enviarNotificationFtp("Fichero predict.jpg enviado al servidor");
                        Log.d(xxx, "Archivo predict.jpg enviado al servidor");

                        return true;
                    }else{
                        enviarNotificationFtp("Error: Fallo al enviar el fichero predict.jpg al servidor" +", saliendo de la aplicacion");
                        Log.d(xxx, "Error: Fallo al enviar el fichero predict.jpg al servidor");
                        return false;
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    enviarNotificationFtp("Error IOException en ftp al enviar el fichero al servidor: " +e.getMessage() +", saliendo de la aplicacion");
                    Log.d(xxx, "Error IOException en ftp al enviar el fichero al servidor: " +e.getMessage());

                    return false;
                }

            //}
        //});



            //return true;

        }//Fin del else de if(filePathDePredictJpg == null)

    }//Fin de metodo metodoSubirImagenConFtp



    //**********************************************************************************************************
    //NOTA 10 oct 2017: ESte codigo no lo uso, ni las clases en la carpeta retrofit, ya que para funcionar requiere
    //un servidor node.js o php que tenga implementado web services.
    //Me queda como ejercicio para hacer el servidor en node.js, como en la web de donde extraje este codigo.
    //Servidor node.js para subir imagenes:  http://hidrodixtion.github.io/2016/06/02/create-simple-image-upload-server-in-node-js/

    //Uso de retrofit para subir la imagen generada al servidor
    //Recibe la instancia de ObtenerImagen usada en el metodo metodoPrincipal_2
    //Como en https://medium.com/@adinugroho/upload-image-from-android-app-using-retrofit-2-ae6f922b184c
    ServicioRetrofit2 servicioRetrofit2;
    private boolean subirImagenConRetrofit2(ObtenerImagen obtenerImagen, String Url){
        File filePathDePredictJpg = obtenerImagen.getFilePathOfPicture(Environment.DIRECTORY_DCIM, "/predict/",
                "predict.jpg");

        if(filePathDePredictJpg == null){
            enviarNotification("Error al obtener el file de predict.jpg para retrofit2" +" ,saliendo de la aplicacion");
            return false;

        }else{//Hemos obtenido el file de la imagen a subir, seguimos

            //Chequeamos que el path a predict.jpg es correcto:
            Log.d(xxx, "Path a predict.jpg: " + filePathDePredictJpg.getName());

            //Usamos interceptor para loggear retrofit
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            //Creamos una instancia de ServicioRetrofit2
            servicioRetrofit2 = new Retrofit.Builder().baseUrl(Url).client(client).build().create(ServicioRetrofit2.class);

            //Definimos los parametros necesarios para retrofit2
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), filePathDePredictJpg);
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", filePathDePredictJpg.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "prueba_de_subida");

            retrofit2.Call<okhttp3.ResponseBody> req = servicioRetrofit2.postImage(body, name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Toast.makeText(Mezclar.this, "Success " + response.message(), Toast.LENGTH_LONG).show();
                    Toast.makeText(Mezclar.this, "Success " + response.body().toString(), Toast.LENGTH_LONG).show();
                    Log.d(xxx, "Imagen subida correctamente con retrofit2: " +response.message());
                    Log.d(xxx, "response.message: " +response.message());
                    Log.d(xxx, "response.body().toString(): " +response.body().toString());
                    enviarNotification("Imagen subida correctamente al servidor: " +response.message());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //t.printStackTrace();
                    Log.d(xxx, "Error de retrofit2: " +t.getMessage());
                    enviarNotification("Error al obtener el file de predict.jpg para retrofit2" +" ,saliendo de la aplicacion");
                }
            });




            //**********************************************************************************
            return true;
        }
    }

    //Clase para subir la imagen al servidor en un thread distinto de la UI
    private class FtpAsyncTask extends AsyncTask<String, Integer, Boolean> {
        public FtpAsyncTask() {
            super();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Log.d(xxx, "estoy en doInBackground");

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
            Log.d(xxx, "onPostExecute, el resultado de doInBackground es: " +boolResultado);

            if(boolResultado) {
                Log.d(xxx, "En onPostExecute: Success, Imagen enviada al servidor ftp");
                //Mantener la app abierta
                Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Imagen predict.jpg guardada en DCIM/predict y enviada al servidor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //Si todo va bien, dejo la app abierta pero deshabilito el fab:
                fabOnPostExecute = (FloatingActionButton) findViewById(R.id.fab);
                fabOnPostExecute.setVisibility(View.INVISIBLE);

            }else{
                Log.d(xxx, "En onPostExecute: FAIL, Imagen NO enviada al servidor ftp");
                progressBar.setVisibility(View.INVISIBLE); //To Hide ProgressBar
                //Cerrar aplicacion, ha habido un fallo
                finish();

            }

        }

        protected void onProgressUpdate(Integer[] values) {
        }



        protected void onCancelled() {
        }
    }
}//Fin del activity
