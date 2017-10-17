package com.juan.mezclar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//Notas del 6 oct 2017
//Ver como limitar la entrada a 16 digitos
//commit 4321

public class ActivityLauncher extends AppCompatActivity {
    //String para usar en log.d con el nombre de la clase
    String xxx = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                sendIntentToAppMezclar();
            }
        });  */

        final Button button = (Button)findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendIntentToAppMezclar();
            }
        });

        recuperarIntentConDatosIniciales();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendIntentToAppMezclar(){
        //declaro el edittext
        EditText secuenciaDeImagenes   = (EditText)findViewById(R.id.secImagenes);
        //Chequeo que el string no esta vacio
        if(secuenciaDeImagenes.length() != 0) {
            //Especifico el paquete que quiero lanzar, la app Mezclar
            //Intent launchMezclarApplication = getPackageManager().getLaunchIntentForPackage("com.juan.mezclar");
            Intent intent = new Intent(this, MezclarFinal.class);

            //launchMezclarApplication.putExtra("KeyName","Hola, te estoy llamando");
            intent.putExtra("KeyName", secuenciaDeImagenes.getText().toString());

            startActivity(intent);
        }else{
            //Mostrar un snakc bar:
            Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Introduce Digits", Snackbar.LENGTH_LONG)
                   .setAction("Action", null).show();
        }
    }

    //Metodo que recupera los datos recibidos en un intent lanzado por otra aplicacion,
    //por ejemplo, Launch Mezclar.
    //Si el intent es nulo, o no hay datos, la app se cierra automaticamente
    String stringImagesSecuence; //Para prueba con el array vacio
    private void recuperarIntentConDatosIniciales() {
        //Recibir datos de la app Launh Mezclar
        //String myString;
        Bundle data = getIntent().getExtras();
        if (data != null) {
            String myString = data.getString("KeyName");
            //Hay que chequear myString para que no lanze el toast with null cuando lanzo la app desde el movil
            if (myString != null && !myString.isEmpty()) {
                //Copiamos la secuencia de imagenes recibidas
                stringImagesSecuence = null;
                stringImagesSecuence = myString;
                //Toast.makeText(this, myString, Toast.LENGTH_SHORT).show();


                Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Datos de Launch Mezclar: " + stringImagesSecuence);
                //Muestro el string character a character
                for (int i = 0; i < stringImagesSecuence.length(); i++) {
                    Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Caracter " + i + ":" + stringImagesSecuence.charAt(i));
                }

                if(!stringImagesSecuence.isEmpty()){
                    Intent intent = new Intent(this, MezclarFinal.class);

                    //launchMezclarApplication.putExtra("KeyName","Hola, te estoy llamando");
                    intent.putExtra("KeyName", stringImagesSecuence);

                    startActivity(intent);
                    this.finish();
                }
                //OK, la app continua

            } else {//Salta aqui si no hay datos en el intent
                Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Datos de Launch Mezclar: No hay datos");
                //La app sigue

            }

        } else {//Salta aqui si recibe nulo en el intent
            Log.d(xxx, "En metodo recuperarIntentConDatosIniciales, Datos de Launch Mezclar: NULL 2 del else");
            //Si la app no ha sido abierta desde otra app, Launh Mezclar en mi caso, la cierro automaticamente
            //this.finish();
            //finish();

        }
    }
}//Fin de la clase