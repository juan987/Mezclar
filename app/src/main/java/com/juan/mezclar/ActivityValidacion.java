package com.juan.mezclar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class ActivityValidacion extends AppCompatActivity {
    String xxx = this.getClass().getSimpleName();
    char[] arrayMacSequence;
    String clave;
    Button button;
    EditText claveUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(xxx, "onCreate, nueva instancia de ActivityValidacion");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validacion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String macAddress = getMacAddr();

        //1 dic 17, voy a buscar el fallo reportado en mail problema con codigo de activacion
        //fallo encontrado: cuando el digito era un 4, se devolvia el resultado del algoritmo + 1 espacio
        //macAddress = "84:34:38:D7:92:8F";

        Log.d(xxx, "En metodo onCreate la Mac address es: "  +macAddress);

        TextView textview_2 = (TextView) findViewById(R.id.textview_2);
        textview_2.setText(macAddress);

        //Convertir  los cuatro primero caracteres  de la mac address en strings
        macAddress = macAddress.toUpperCase();
        Log.d(xxx, "En metodo onCreate el mac en mayusculas es: "  +macAddress);

        //Suma 1 al primer caracter
        arrayMacSequence = macAddress.toCharArray();
        Character character1 = (Character)arrayMacSequence[1];
        String charDeLaSecuenciaRecibida = character1.toString();
        String char_1_clave = algoritmoSuma_1(charDeLaSecuenciaRecibida);

        //resta 1 al segundo caracter
        character1 = (Character)arrayMacSequence[4];
        charDeLaSecuenciaRecibida = character1.toString();
        String char_2_clave = algoritmoResta_1(charDeLaSecuenciaRecibida);

        //Suma 1 al tercer caracter
        character1 = (Character)arrayMacSequence[7];
        charDeLaSecuenciaRecibida = character1.toString();
        String char_3_clave = algoritmoSuma_1(charDeLaSecuenciaRecibida);

        //resta 1 al cuarto caracter
        character1 = (Character)arrayMacSequence[10];
        charDeLaSecuenciaRecibida = character1.toString();
        String char_4_clave = algoritmoResta_1(charDeLaSecuenciaRecibida);

        //La clave es:
        clave = char_1_clave + char_2_clave + char_3_clave + char_4_clave;
        Log.d(xxx, "En metodo onCreate la clave es: "  +clave);

        claveUsuario   = (EditText)findViewById(R.id.claveUsuario);


        //Boton
        button = (Button)findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if((claveUsuario.length() == 0)) {
                    Snackbar.make(findViewById(R.id.validation_layout), "Enter data", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    if(clave.equals(claveUsuario.getText().toString())){
                        //Lanzamos la app
                        Log.d(xxx, "En metodo onCreate boton, la clave es: "  +clave);
                        Log.d(xxx, "En metodo onCreate boton, la clave del usuario es valida: "  +claveUsuario.getText().toString());
                        Log.d(xxx, "En metodo onCreate boton, la clave es valida: ");
                        //Modificamos shared preferences para que se pueda usar la app
                        ConfiguracionLicencia configuracionLicencia = new ConfiguracionLicencia(ActivityValidacion.this);
                        configuracionLicencia.setLicencia("Licencia valida");

                        //Lanzamos la app
                        Intent intent = new Intent(ActivityValidacion.this, ActivityLauncher.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        ActivityValidacion.this.finish();

                    }else{
                        //mensaje de error
                        Snackbar.make(findViewById(R.id.validation_layout), "Code not valid", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }

            }
        });
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }


    private String algoritmoSuma_1(String numero){
        if(numero.equals("0")){
            return "1";
        }
        if(numero.equals("1")){
            return "2";
        }
        if(numero.equals("2")){
            return "3";
        }
        if(numero.equals("3")){
            return "4";
        }
        if(numero.equals("4")){
            return "5";
        }
        if(numero.equals("5")){
            return "6";
        }
        if(numero.equals("6")){
            return "7";
        }
        if(numero.equals("7")){
            return "8";
        }
        if(numero.equals("8")){
            return "9";
        }
        if(numero.equals("9")){
            return "A";
        }
        if(numero.equals("A")){
            return "B";
        }
        if(numero.equals("B")){
            return "C";
        }
        if(numero.equals("C")){
            return "D";
        }
        if(numero.equals("D")){
            return "E";
        }
        if(numero.equals("E")){
            return "F";
        }
        if(numero.equals("F")){
            return "0";
        }

        return null;
    }


    private String algoritmoResta_1(String numero){
        if(numero.equals("0")){
            return "F";
        }
        if(numero.equals("1")){
            return "0";
        }
        if(numero.equals("2")){
            return "1";
        }
        if(numero.equals("3")){
            return "2";
        }
        if(numero.equals("4")){
            return "3";
        }
        if(numero.equals("5")){
            return "4";
        }
        if(numero.equals("6")){
            return "5";
        }
        if(numero.equals("7")){
            return "6";
        }
        if(numero.equals("8")){
            return "7";
        }
        if(numero.equals("9")){
            return "8";
        }
        if(numero.equals("A")){
            return "9";
        }
        if(numero.equals("B")){
            return "A";
        }
        if(numero.equals("C")){
            return "B";
        }
        if(numero.equals("D")){
            return "C";
        }
        if(numero.equals("E")){
            return "D";
        }
        if(numero.equals("F")){
            return "E";
        }

        return null;
    }


    //Como en:
    //https://stackoverflow.com/questions/33159224/getting-mac-address-in-android-6-0
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

}