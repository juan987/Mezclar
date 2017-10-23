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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

//19 OCT 17: ESTA ACTIVIDAD se muestra cuando se hace click en el icono y es llamada desde
//la actividad ActivityLauncher con un intent.
//Esta actividad tiene una UI para introducir secuencias numericas y secuencias alfanumericas.

public class ActivityLauncherUI extends AppCompatActivity {
    //String para usar en log.d con el nombre de la clase
    Button button = null;
    Button buttonCrearDir = null;
    String xxx = this.getClass().getSimpleName();
    EditText secuenciaDeImagenes;
    EditText secuenciaDeImagenesAlfanumerica;
    Spinner spinner;
    EditText nuevoDir;
    ConfiguracionesMultiples configuracionesMultiples = null;
    //Array con los nombres de los directorios opcionales
    String[] dir = null;
    ArrayAdapter<String> spinnerArrayAdapter = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_launcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        secuenciaDeImagenes   = (EditText)findViewById(R.id.secImagenes);
        secuenciaDeImagenesAlfanumerica   = (EditText)findViewById(R.id.secImagenesAlfanumerica);
        nuevoDir   = (EditText)findViewById(R.id.nuevo_dir);





        //Nuevo requerimiento, spinner, el 23 oct 2017

        spinner   = (Spinner) findViewById(R.id.spinner);
        //String[] dir = {"A","B","C","D","E"};

        //**************************************************************************************************
        //REQ: Gestion de configuraciones multiples recibido el 23-10-17
        //obtener getActiveDirectory de la clase ConfiguracionesMultiples
        configuracionesMultiples = new ConfiguracionesMultiples(ActivityLauncherUI.this);

        //Prueba crear nuevo subdir
        //configuracionesMultiples.createSubDirDeDirCesaralMagicImageC("nuevo_dir_2");

        String pathCesaralMagicImageC = configuracionesMultiples.getActiveDirectory();
        //El directorio activo de la app es:
        Log.d(xxx, "En onCreate, al inicio de la app  el directorio activo es: " +pathCesaralMagicImageC);

        //Leer los sub directorios que cuelgan de, Prueba OK

        List<String> subDirs = configuracionesMultiples.getSubDirDeDirCesaralMagicImageC();

        //String[] dir = new String[subDirs.size()];
        //dir = new String[subDirs.size()];
        dir = new String[subDirs.size()+1];
        dir[0] = "none";

        for (int i=0; i < subDirs.size(); i++){
            Log.d(xxx, "onCreatwe, sub directorio es: " +subDirs.get(i));
            dir[i+1] = subDirs.get(i);
        }

        //Leer el directorio activo del share preferences, tb en IntentServiceMagic
        //pathCesaralMagicImageC = configuracionesMultiples.getActiveDirectory();
        //El directorio activo de la app es:
        //Log.d(xxx, "En metodoPrincipal_2, el directorio activo es: " +pathCesaralMagicImageC);

        //**************************************************************************************************



        //spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        //Como en:
        //https://android--code.blogspot.com.es/2015/08/android-spinner-text-size.html
        // Initializing an ArrayAdapter
        //ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
        spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,dir);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        //Como alternativa mas limpia ver aqui:
        //https://stackoverflow.com/questions/10409871/how-to-increase-spinner-item-font-size

        //Coger el click del spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            //public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id)
            {
                //Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(pos), Toast.LENGTH_SHORT).show();
                if(pos == 0){
                    //Si la posicion es cero, tiewne none, no hacemos nada
                    Log.d(xxx, "spinner, onItemSelected default, sub directorio es: " +(String) adapterView.getItemAtPosition(pos));


                }else{
                    Log.d(xxx, "spinner, onItemSelected cualquiera, sub directorio es: " +(String) adapterView.getItemAtPosition(pos));
                    //Guardamos el dir seleccionado en shared preferences
                    configuracionesMultiples.setActiveDirectory((String) adapterView.getItemAtPosition(pos));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(xxx, "spinner, onNothingSelected ");


            }
        });





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


        //Nuevo req el 20 oct 2017: se envia directamente los dos editText a MezclarFinal,
        //tengan o no tengan datos, de eso se ocupa la act MezclarFinal
        button = (Button)findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if((secuenciaDeImagenes.length() == 0 && secuenciaDeImagenesAlfanumerica.length() == 0)) {
                    Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Enter data in one of the boxes or both", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    //Antes del intent, mostrar el directorio activo
                    //Leer el directorio activo del share preferences, tb en IntentServiceMagic
                    String pathCesaralMagicImageC = configuracionesMultiples.getActiveDirectory();
                    //El directorio activo de la app es:
                    Log.d(xxx, "En setOnClickListener,despues del boton,  el directorio activo es: " +pathCesaralMagicImageC);
                    //Uno o ambos campos tienen datos, enviamos el intent a MezclarFinal
                    Intent intent = new Intent(ActivityLauncherUI.this, MezclarFinal.class);
                    intent.putExtra("KeyName", secuenciaDeImagenes.getText().toString());
                    intent.putExtra("KeyAlfanumerico", secuenciaDeImagenesAlfanumerica.getText().toString());
                    startActivity(intent);
                }

            }
        });

        //nuevo req para crear un nuevo dir
        buttonCrearDir = (Button)findViewById(R.id.button_crear_dir);
        buttonCrearDir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            String stringNuevoDirectorio = nuevoDir.getText().toString();
            //Crear nuevo subdir
            configuracionesMultiples.createSubDirDeDirCesaralMagicImageC(stringNuevoDirectorio);
            button.setVisibility(View.VISIBLE);
            secuenciaDeImagenes.setVisibility(View.VISIBLE);
            secuenciaDeImagenesAlfanumerica.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            nuevoDir.setVisibility(View.GONE);
            buttonCrearDir.setVisibility(View.GONE);




            //Volver a leer los sub directorios que cuelgan de, Prueba OK
            List<String> subDirs = configuracionesMultiples.getSubDirDeDirCesaralMagicImageC();
                dir = null;
                //dir = new String[subDirs.size()];
                dir = new String[subDirs.size()+1];
                dir[0] = "none";
                for (int i=0; i < subDirs.size(); i++){
                    Log.d(xxx, "onCreatwe, sub directorio es: " +subDirs.get(i));
                    dir[i+1] = subDirs.get(i);
                }

                // Initializing an ArrayAdapter
                spinnerArrayAdapter = null;
                spinnerArrayAdapter = new ArrayAdapter<String>(
                        ActivityLauncherUI.this,R.layout.spinner_item,dir);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinner.setAdapter(spinnerArrayAdapter);



            }
        });

        //Codigo original para el boton,
        //a partir del 20 oct ya no lo uso
        /*
        final Button button = (Button)findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(editTextNumOrAlfa()){
                    if((secuenciaDeImagenes.length() != 0 && secuenciaDeImagenesAlfanumerica.length() == 0)) {

                        sendIntentToAppMezclar();
                    }
                    if((secuenciaDeImagenes.length() == 0 && secuenciaDeImagenesAlfanumerica.length() != 0)) {

                        sendIntentToAppMezclarAlfanumerico();
                    }
                }else{
                    //los dos edittext tienen algo o ambos estan vacios
                    Log.d(xxx, "En metodo onCreate los dos edittext tienen algo o ambos estan vacios");

                }
            }
        });
        */

    }//Fin del onCreate

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
            //**************************************************************
            //Button button = null;
            //String xxx = this.getClass().getSimpleName();
            //EditText secuenciaDeImagenes;
            //EditText secuenciaDeImagenesAlfanumerica;
            //Spinner spinner;
            //EditText nuevoDir;
            button.setVisibility(View.GONE);
            secuenciaDeImagenes.setVisibility(View.GONE);
            secuenciaDeImagenesAlfanumerica.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            nuevoDir.setVisibility(View.VISIBLE);
            buttonCrearDir.setVisibility(View.VISIBLE);
            //**************************************************************
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Nuevo req el 20 oct 2017: se envia directamente los dos editText a MezclarFinal,
    //tengan o no tengan datos, de eso se ocupa la act MezclarFinal


    //*************************************************************************************************************
    //*************************************************************************************************************
    //*************************************************************************************************************
    //*************************************************************************************************************
    //*************************************************************************************************************
    //*************************************************************************************************************

    //DE aqui para abajo es el codigo original



    public boolean editTextNumOrAlfa(){
        //Chequea si ambos textEdit son vacios o tiene algo simultaneamente
        if((secuenciaDeImagenes.length() != 0 && secuenciaDeImagenesAlfanumerica.length() != 0)) {
            Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Click only one box", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            secuenciaDeImagenesAlfanumerica.setText("");
            secuenciaDeImagenes.setText("");
            return false;
        }else if((secuenciaDeImagenes.length() == 0 && secuenciaDeImagenesAlfanumerica.length() == 0)) {
                Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Enter data in one of the boxes", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return false;
        }

            return true;
    }

    //Intent cuando el string es solo digitos
    public void sendIntentToAppMezclar(){

        //Chequeo que el string no esta vacio
        if(secuenciaDeImagenes.length() != 0) {

            Intent intent = new Intent(this, MezclarFinal.class);

            //launchMezclarApplication.putExtra("KeyName","Hola, te estoy llamando");
            intent.putExtra("KeyName", secuenciaDeImagenes.getText().toString());

            startActivity(intent);
        }else{
            Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Type Digits", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    //Intent con la secuencia alfanumerica
    public void sendIntentToAppMezclarAlfanumerico(){

        //Chequeo que el string no esta vacio
        if(secuenciaDeImagenesAlfanumerica.length() != 0) {
            //chequea que el string solo tiene letras (mayusculas y minusculas) y digitos.
            String pruebaRegex = secuenciaDeImagenesAlfanumerica.getText().toString();
            if(isLettersAndDigits(pruebaRegex)){
                Log.d(xxx, "En metodo sendIntentToAppMezclarAlfanumerico, regex TRUE para: " +pruebaRegex);
                //El string es correcto: solo tiene letras en May/min y digitos. No tiene las letras ñ ni Ñ
                Intent intent = new Intent(this, MezclarFinal.class);
                intent.putExtra("KeyAlfanumerico", secuenciaDeImagenesAlfanumerica.getText().toString());
                startActivity(intent);
            }else{
                Log.d(xxx, "En metodo sendIntentToAppMezclarAlfanumerico, regex FALSE para: " +pruebaRegex);
                secuenciaDeImagenesAlfanumerica.setText("");
                Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Type only letters and numbers", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }

            //Intent intent = new Intent(this, MezclarFinal.class);
            //intent.putExtra("KeyName", secuenciaDeImagenesAlfanumerica.getText().toString());
            //startActivity(intent);
        }else{
            //Mostrar un snakc bar:
            Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Type letters and digits", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    //devuelve true si el string solo contiene letras, mayusculas o minusculas, digitos
    // y no tiene la Ñ, para cualquier numero de caracteres
    //Ver http://www.vogella.com/tutorials/JavaRegularExpressions/article.html#regular-expressions
    public boolean isLettersAndDigits(String s){

        //Requerimiento de Cesar: que se trague todo el string, sin importar los caracteres que tenga.
        return true;
        //return s.matches("([\\w&&[^ñÑ]])*");
    }

    //Metodo que recupera los datos recibidos en un intent lanzado por otra aplicacion,
    //por ejemplo, Launch Mezclar.
    //Si el intent es nulo, o no hay datos, la app se cierra automaticamente
    String stringImagesSecuence; //Para prueba con el array vacio

    //En esta version de la activity, NO hay que recojer datos del intent.
    /*
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
    }//Fin de recuperarIntentConDatosIniciales
    */

}//Fin de la clase
