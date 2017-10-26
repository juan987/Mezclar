package com.juan.mezclar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Juan on 25/10/2017.
 * Como en:
 * https://developer.android.com/reference/android/app/DialogFragment.html
 * y como en:
 * https://developer.android.com/guide/topics/ui/dialogs.html
 *
 */

public class BorrarConfiguracionActivity extends AppCompatActivity
                    implements BorrarConfigurationDialogFragment.FragmentAlertDialog{

    String xxx = this.getClass().getSimpleName();
    ConfiguracionesMultiples configuracionesMultiples = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_configuracion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        Button button = (Button)findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog();

            }
        });

        //REQ: Borrar la configuracion activa el 25-10-17
        //obtener getActiveDirectory de la clase ConfiguracionesMultiples
        configuracionesMultiples = new ConfiguracionesMultiples(BorrarConfiguracionActivity.this);
        String pathCesaralMagicImageC = configuracionesMultiples.getActiveDirectory();
        //El directorio activo de la app es:
        Log.d(xxx, "En onCreate, la configuracion activa es: " +pathCesaralMagicImageC);
        //Obtengo solo el nombre del sub directorio
        String[] arrayStringDirActivo = pathCesaralMagicImageC.split("/");
        //Solo para pruebas
        /*
        for (int i = 0; i < arrayStringDirActivo.length; i++){
            Log.d(xxx, "En metodo spinnerSetSeleccion, la cadena de directorio activo tiene en el indice: "
                    +i +": " +arrayStringDirActivo[i] );
        } */

        //El string del subdir que busco siempre esta en el ultimo indice de actdir
        //Puede ser ImageC o cualquiera de los que cuelgan de ImageC
        actdir = arrayStringDirActivo[arrayStringDirActivo.length - 1];
        TextView textView = (TextView)findViewById(R.id.text_view_active_directory);
        //textView.setText(pathCesaralMagicImageC);
        textView.setText(actdir);



    }//Fin del onCreate
    String actdir = "";

    void showDialog() {
        DialogFragment newFragment = BorrarConfigurationDialogFragment.newInstance(
                "cualquier cosa");
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void doPositiveClick() {
        boolean dirBorrado = configuracionesMultiples.deleteSubDirDeDirCesaralMagicImageC(actdir);

        if(dirBorrado) {
            Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Active configuration deleted", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            configuracionesMultiples.setActiveDirectory("default directory");
            //Volvemos a la pantalla principal
            finish();
        }else{
            Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Error, Active configuration not deleted", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void doNegativeClick() {
        Snackbar.make(findViewById(R.id.coordinatorlayout_1), "Delete action canceled by user", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        //Volvemos a la pantalla principal
        finish();

    }
}
