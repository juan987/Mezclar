package com.juan.mezclar;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.content.Intent;

//Para gestionar imagenes
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mezclar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    Snackbar.make(view, "Resultado correcto", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    enviarNotification("Imagen guardada en /DCIM/predict/  Ejecucion correcta" +"\n" +"Aplicacion cerrada");
                }else{
                    //Forzar el cierre de la app por que ha habido un error
                    Snackbar.make(view, "Cerrando app debido a un ERROR", Snackbar.LENGTH_LONG)
                           .setAction("Action", null).show();
                    //Si la app no ha sido abierta desde otra app, Launh Mezclar en mi caso, la cierro automaticamente
                    //enviarNotification("Aplicacion cerrada debido a un error de ejecucion");
                    finish();
                }
            }
        });


        Log.d(xxx, "Hola " );
        collageImage = (ImageView)findViewById(R.id.imageView3);


        //Recibir datos de la app Launh Mezclar
        String myString;
        Bundle data = getIntent().getExtras();
        if(data!=null){
            myString = data.getString("KeyName");
            //Hay que chequear myString para que no lanze el toast with null cuando lanzo la app desde el movil
            if(myString!=null && !myString.isEmpty()) {
                //Copiamos la secuencia de imagenes recibidas
                stringImagesSecuence = null;
                stringImagesSecuence = myString;
                Toast.makeText(this,
                        myString, Toast.LENGTH_SHORT).show();
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

        //Boton anulado. Uso el fab
        /*Button combineImage = (Button)findViewById(R.id.combineimage);
        combineImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodoPrincipal();
            }
        });
        */

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
    private boolean metodoPrincipal_2(){
        //ObtenerImagen obtenerImagen = new ObtenerImagen(Mezclar.this);

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
        String[] coordenates;
        String linea;
        for (int i=0; i < arrayLineasTexto.size(); i++){
            Log.d(xxx, "Linea "  +(i+1) +" contiene: " +arrayLineasTexto.get(i));
            linea = arrayLineasTexto.get(i);
            //Hacemos split de linea
            coordenates = linea.split("\\s+");
            int index = 1;
            for(String dato : coordenates){
                //Dato tiene cada string de una linea de CONFIG.txt: N12,
                Log.d(xxx, "Dato " +index +" es: " +dato);
                index++;

            }
            leerCoordenadasDeConfigTxt(arrayLineasTexto.get(i));
        }

        //Obtener la imagen origin.jpg como un bitmap
        ObtenerImagen obtenerImagen = new ObtenerImagen(Mezclar.this);
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
                enviarNotification("Error al recuperar imagen pequeña numero: " +i +" ,saliendo de la aplicacion");
                //Acabamos la ejecucion
                return false;
            }else{
                //Continuamos con el procesamiento
                //Se muestra la imagen pequeña en la UI,
                ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
                imageView2.setImageBitmap(imagenParaSuperponerConOrigin);

                //Modificar la imagen a superponer: pixels blancos son convertidos a transparentes con channel alpha
                imagenParaSuperponerConOrigin = changeSomePixelsToTransparent(imagenParaSuperponerConOrigin);
                leerCoordenadasDeSuperposicion(i);
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
                    enviarNotification("Error mezclando imagen: " +i  +" ,saliendo de la aplicacion");
                    return false;

                }
                //
            }

        }//Fin del loop principal

        //Ejecucion correcta, guardar imagen en la memoria externa del dispoositivo
        GuardarImagenFinal guardarImagenFinal = new GuardarImagenFinal(Mezclar.this, mergedImages);
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
            enviarNotification("Error guardando imagen predict" +" ,saliendo de la aplicacion");
            return false;
        }

        //Return true al final del metodo. es un fake return, este valor no se recoje en ningun sitio
        return true;
    }//Fin de metodoPrincipal_2


    //ESte metodo separa los numeros de cada linea de config.txt en el array str:
    //str[1] muestra el indice N, el str[2] el valor de la coordenada X, str[3] la coordenada Y
    //str[0] siempre muestra un char vacio.
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



    //Coordenadas globales para colocar la imagen transparente sobre origin.jpg
    private float xFloat;
    private float yFloat;
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
                        .setContentTitle("My notification")
                        .setContentText(mensaje);
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


}
