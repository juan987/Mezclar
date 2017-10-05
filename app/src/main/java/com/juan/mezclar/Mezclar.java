package com.juan.mezclar;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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

public class Mezclar extends AppCompatActivity {
    private ImageView collageImage;
    private ImageView finalImage;


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
                metodoPrincipal();
            }
        });


        String xxx = this.getClass().getSimpleName();
        Log.d(xxx, "Hola " );
        collageImage = (ImageView)findViewById(R.id.imageView3);


        //Recibir datos de la app Launh Mezclar
        Bundle data = getIntent().getExtras();
        if(data!=null){
            String myString = data.getString("KeyName");
            //Hay que chequear myString para que no lanze el toast with null cuando lanzo la app desde el movil
            if(myString!=null) {
                Toast.makeText(this,
                        myString, Toast.LENGTH_SHORT).show();
                Log.d(xxx, "Datos de Launch Mezclar: " + myString);
                //Muestro el string character a character
                for(int i = 0; i < myString.length(); i++) {
                    Log.d(xxx, "Caracter " +i +":" + myString.charAt(i));
                }
            }else{
                Log.d(xxx, "Datos de Launch Mezclar: No hay datos");
            }

        }else{
            Log.d(xxx, "Datos de Launch Mezclar: NULL 2 del else");
        }

        Button combineImage = (Button)findViewById(R.id.combineimage);
        combineImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodoPrincipal();
            }
        });

    }
    //ESte metodo mezcla dos imagenes que estan en la carpeta drawable.
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

    private void metodoPrincipal_2(){
        //Obtener el path de DCIM/

    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage){

        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, 10, 10, null);
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
