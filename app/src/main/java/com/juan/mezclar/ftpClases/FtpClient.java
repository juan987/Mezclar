package com.juan.mezclar.ftpClases;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Juan on 10/10/2017.
 *
 */

//NOTA FINAL: CLASE CON LAS FUNCIONES NECESARIAS PARA SUBIR PREDICT.JPG AL SERVER

//Como en: http://tutoandroidblog.blogspot.com.es/2013/01/servidor-ftp-de-subida-de-archivo.html

//ESTO ES MUY IMPORTANTE: COMO FUNCIONA commons.net
/*    Immediately after connecting is the only real time you need to check the reply code (because connect is of type void).
            The convention for all the FTP command methods in FTPClient is such that they either return a boolean value or
            some other value. The boolean methods return true on a successful completion reply from the FTP server and false
            on a reply resulting in an error condition or failure. The methods returning a value other than boolean return
            a value containing the higher level data produced by the FTP command, or null if a reply resulted in an error condition or failure.
            If you want to access the exact FTP reply code causing a success or failure, you must call getReplyCode after a success or failure.
*/

public class FtpClient {
    //String para usar en log.d con el nombre de la clase
    String xxx = this.getClass().getSimpleName();

    private String ip;                //Almacena la dirección del servidor

    private String usuario;            //Almacena el nombre de usuario
    private String contrasena;        //Almacena la contraseña del usuario

    FTPClient ftpClient;            //Crea la conexión con el servidor
    BufferedInputStream buffer;        //Crea una buffer de lectura
    File rutaSd;                    //Almacena la ruta sd
    File rutaCompleta;                //Almacena la ruta completa del archivo

    Context context;                //Almacena el contexto de la aplicacion
    // util a la hora de mostrar mensajes

           //------------------------------------------------------------------

    //Constructores-----------------------------------------------------

    /**
     * Crea una instancia de FTP sin credenciales
     */
    public FtpClient(String ip, Context context) {

        //Inicialización de campos
        this.ip = ip;
        usuario = null;
        contrasena = null;

        ftpClient = null;
        buffer = null;
        rutaSd = null;
        rutaCompleta = null;

        this.context = context;
    }

    /**
     * Crea una instancia de FTP con credenciales
     * @param usuario		El nombre de usuario
     * @param contrasena	La contraseña de usuario
     */
    public FtpClient (String ip, String usuario, String contrasena, Context context){


        //Inicialización de campos
        this.ip = ip;
        this.usuario = usuario;
        this.contrasena = contrasena;

        ftpClient = null;
        buffer = null;
        rutaSd = null;
        rutaCompleta = null;

        this.context = context;

    }


    //------------------------------------------------------------------

    //Propiedades-------------------------------------------------------

    /**
     * Obtiene el nombre de usuario
     * @return	El nombre de usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Establece el nombre de usuario
     * @param usuario	El nombre de usuario
     */

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene la contraseña de usuario
     * @return	La contraseña de usuario
     */

    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña de usuario
     * @param contrasena	La contraseña de usuario
     */

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    //------------------------------------------------------------------

    /**
     * Realiza el login en el servidor
     * @param usuario	El nombre de usuario
     * @param contrasena	La contraseña de usuario
     * @return	Verdad en caso de haber realizado login correctamente
     * @throws SocketException
     * @throws IOException
     */
    public boolean login (String usuario, String contrasena) throws SocketException, IOException {
        Log.d(xxx, "Estoy en el metodo login");

        //Almacena los valores en la clase
        this.usuario = usuario;
        this.contrasena = contrasena;

        try{


            ftpClient = new FTPClient();

            //Para ver los traceos en el monitor
            //ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));//No funciona?????

            //Establece conexión con el servidor
            //ftpClient.connect("ftp.cesaral.com");
            Log.d(xxx, "URL para el ftp que esta en el fichero CONTEXT.txt: " +ip);
            ftpClient.connect(ip);
            Log.d(xxx, "Codigo de respuesta del connect:   " +ftpClient.getReplyCode());


            //Hace login en el servidor
            if (ftpClient.login(usuario, contrasena)){

                Log.d(xxx, "Conexion y Login al servidor correctos: ");
                Log.d(xxx, "Codigo de respuesta del login:   " +ftpClient.getReplyCode());

                return true;	//En caso de login correcto
            }
            else{


                Log.d(xxx, "Fallo en el login al servidor ftp ftpClient.login(usuario, contrasena))");
                Log.d(xxx, "Codigo de respuesta del login:   " +ftpClient.getReplyCode());

                return false;	//En caso de login incorrecto
            }

        }


     catch (SocketException e) {
         e.printStackTrace();
         Log.d(xxx, "Fallo 1 en la conexion al server ftpClient.connect(ip): " + e.getMessage());
         return false;	//En caso de que no sea posible la conexion, Si no retorno, me sale el fallo de strict mode


     }
     catch (UnknownHostException e) {
         e.printStackTrace();
         Log.d(xxx, "Fallo  2 en la conexion al server ftpClient.connect(ip): " + e.getMessage());
         return false;	//En caso de que no sea posible la conexion


     }
     catch (IOException e) {
        e.printStackTrace();
         Log.d(xxx, "Fallo 3 en la conexion al server ftpClient.connect(ip): " + e.getMessage());
         return false;	//En caso de que no sea posible la conexion

     }



        /*
        //Hace login en el servidor
        if (ftpClient.login(usuario, contrasena)){

            //Informa al usuario
            Toast.makeText(context, "Login correcto . . .", Toast.LENGTH_SHORT).show();
            return true;	//En caso de login correcto
        }
        else{

            //Informa al usuario
            Toast.makeText(context, "Login incorrecto . . .", Toast.LENGTH_SHORT).show();

            Log.d(xxx, "Fallo en el login al servidor ftp ftpClient.login(usuario, contrasena))");
            return false;	//En caso de login incorrecto
        }  */

    }//Fin del metodo login

    /**
     * Sube un archivo al servidor FTP si previamente se ha hecho login correctamente
     * @param nombreArchivo		Nombre del archivo que se quiere subir
     * @return	Verdad en caso de que se haya subido con éxito
     * @throws IOException
     */

    //Metodo original, no lo uso, deprecated
    public boolean enviarFile (String nombreArchivo) throws IOException{

        ftpClient.enterLocalActiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        //Cambia la carpeta Ftp
        //if (ftpClient.changeWorkingDirectory("ftp")){

        //Cambiar a la carpeta test
        if (ftpClient.changeWorkingDirectory("test")){

            //Informa al usuario
            Toast.makeText(context, "Carpeta ftp cambiada . . .", Toast.LENGTH_SHORT).show();

            //Obtiene la dirección de la ruta sd
            Toast.makeText(context, "Ruta SD obtenida . . .", Toast.LENGTH_SHORT).show();
            rutaSd = Environment.getExternalStorageDirectory();

            //Obtiene la ruta completa donde se encuentra el archivo
            Toast.makeText(context, "Ruta completa archivo obtenida . . .", Toast.LENGTH_SHORT).show();
            rutaCompleta = new File(rutaSd.getAbsolutePath(), nombreArchivo);

            //Crea un buffer hacia el servidor de subida
            buffer = new BufferedInputStream(new FileInputStream(rutaCompleta));

            if (ftpClient.storeFile(nombreArchivo, buffer)){

                //Informa al usuario
                Toast.makeText(context, "Archivo subido . . .", Toast.LENGTH_SHORT).show();

                buffer.close();		//Cierra el bufer
                return true;		//Se ha subido con éxito
            }
            else{

                //Informa al usuario
                Toast.makeText(context, "Imposible subir archivo . . .", Toast.LENGTH_SHORT).show();

                buffer.close();		//Cierra el bufer
                return false;		//No se ha subido
            }
        }
        else{

            //Informa al usuario
            Toast.makeText(context, "Carpeta ftp imposible cambiar . . .", Toast.LENGTH_SHORT).show();

            return false;		//Imposible cambiar de directo en servidor ftp
        }

    }//Fin de enviarFile, no uso este metodo

    //Metodo deprecated
    public boolean enviarFileFinal (File ruta, String nombreArchivo) throws IOException{
        Log.d(xxx, "Estoy en el metodo enviarFileFinal");

        ftpClient.enterLocalActiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        //Cambia la carpeta Ftp
        if (ftpClient.changeWorkingDirectory("test")){
        //No cambio nada, a ver que pasa
        //if (true){
            /*
            //Informa al usuario
            Toast.makeText(context, "Carpeta ftp cambiada . . .", Toast.LENGTH_SHORT).show();

            //Obtiene la dirección de la ruta sd
            Toast.makeText(context, "Ruta SD obtenida . . .", Toast.LENGTH_SHORT).show();
            rutaSd = Environment.getExternalStorageDirectory();

            //Obtiene la ruta completa donde se encuentra el archivo
            Toast.makeText(context, "Ruta completa archivo obtenida . . .", Toast.LENGTH_SHORT).show();
            rutaCompleta = new File(rutaSd.getAbsolutePath(), nombreArchivo);

            //Crea un buffer hacia el servidor de subida
            buffer = new BufferedInputStream(new FileInputStream(rutaCompleta));
            */

            //Crear buffer al servidor
            buffer = new BufferedInputStream(new FileInputStream(ruta));

            if (ftpClient.storeFile(nombreArchivo, buffer)){

                //Informa al usuario
                Toast.makeText(context, "Archivo subido . . .", Toast.LENGTH_SHORT).show();

                buffer.close();		//Cierra el bufer
                return true;		//Se ha subido con éxito
            }
            else{

                //Informa al usuario
                Toast.makeText(context, "Imposible subir archivo . . .", Toast.LENGTH_SHORT).show();

                buffer.close();		//Cierra el bufer
                return false;		//No se ha subido
            }
        }
        else{

            //Informa al usuario
            Toast.makeText(context, "Carpeta ftp imposible cambiar . . .", Toast.LENGTH_SHORT).show();

            return false;		//Imposible cambiar de directo en servidor ftp
        }

    }//Fin de enviarFileFinal






    //Enviar con try catch
    public boolean enviarFileFinalFinal (File ruta, String nombreArchivo) throws IOException{
        Log.d(xxx, "Estoy en el metodo enviarFileFinalFinal");

        try {
            ftpClient.enterLocalActiveMode();
            //ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //Cambia la carpeta Ftp
            boolean boolCambioDirectorioHecho = false;
            boolCambioDirectorioHecho = ftpClient.changeWorkingDirectory("test");


            if(boolCambioDirectorioHecho){
                //Cambio de directorio hecho
                Log.d(xxx, "Cambio de directorio hecho");
                //Continuamos
                Log.d(xxx, "Cambio de directorio hecho, codigo de respuesta:  " +ftpClient.getReplyCode());
                Log.d(xxx, "Cambio de directorio hecho:  " +ftpClient.printWorkingDirectory());


            }else{
                Log.d(xxx, "Cambio de directorio ES FALSE");
                Log.d(xxx, "Cambio de directorio Es FALSE, NO HECHO:  " +ftpClient.getReplyCode());

                return false;
            }

            //Crear buffer al servidor
            buffer = new BufferedInputStream(new FileInputStream(ruta));

            boolean boolStoredFileHecho = ftpClient.storeFile(nombreArchivo, buffer);
            if(boolStoredFileHecho){
                //Cambio de directorio hecho
                Log.d(xxx, "Fichero almacenado en el servidor");
                //Continuamos
                Log.d(xxx, "Fichero almacenado en el servidor en el dir:  " +ftpClient.printWorkingDirectory());
                buffer.close();        //Cierra el bufer
                return true;        //Se ha subido con éxito

            }else{
                Log.d(xxx, "ERROR AL ALMACENAR FICHERO EN EL SERVIDOR CON ftpClient.storeFile(nombreArchivo, buffer);");

                return false;
            }


            //buffer.close();        //Cierra el bufer
            //return true;        //Se ha subido con éxito
        }

        catch (SocketException e) {
            e.printStackTrace();
            Log.d(xxx, "Fallo 1 en la conexion al server ftpClient.connect(ip): " + e.getMessage());
            return false;	//En caso de que no sea posible la conexion, Si no retorno, me sale el fallo de strict mode


        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d(xxx, "Fallo  2 en la conexion al server ftpClient.connect(ip): " + e.getMessage());
            return false;	//En caso de que no sea posible la conexion


        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d(xxx, "Fallo 3 en la conexion al server ftpClient.connect(ip): " + e.getMessage());
            return false;	//En caso de que no sea posible la conexion


        }

        catch (Exception e) {
            e.printStackTrace();
            Log.d(xxx, "Fallo 4 en la conexion al server ftpClient.connect(ip): " + e.getMessage());
            return false;	//En caso de que no sea posible la conexion


        }


    }//Fin de enviarFileFinalFinal



    //Metodo deprecated
    public boolean login_original (String usuario, String contrasena) throws SocketException, IOException {

        //Almacena los valores en la clase
        this.usuario = usuario;
        this.contrasena = contrasena;

        //Establece conexión con el servidor
        Toast.makeText(context, "Conectando . . .", Toast.LENGTH_SHORT).show();
        try{
            ftpClient = new FTPClient();

            //Para ver los traceos en el monitor
            ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));//No funciona?????


            //Usar puerto 21


            //ftpClient.connect(ip);//Fallo
            ftpClient.connect("ftp.cesaral.com");//OK, pero anulando el strict mode, si no, me da fallo
            Log.d(xxx, "Codigo de respuesta del connect:   " +ftpClient.getReplyCode());


            //ftpClient.connect("ftp.cesaral.com/test");//Falla con error unable to resolve host
            //ftpClient.connect("ftp.cesaral.com/");//Fallo
            //ftpClient.connect("ftp://ftp.cesaral.com");//Fallo
            //ftpClient.connect("ftp://ftp.cesaral.com/");//Fallo
            //ftpClient.connect("http://ftp.cesaral.com");//Fallo
            //ftpClient.connect("http://www.cesaral.com/");//Fallo
            //ftpClient.connect("http://www.cesaral.com/", 21);//Fallo
            ftpClient.login(usuario, contrasena);
            Log.d(xxx, "Codigo de respuesta del login:   " +ftpClient.getReplyCode());


            Log.d(xxx, "Conexion y Login al servidor correctos: ");



            return true;	//En caso de login correcto



        }
        /*
        catch (Exception e){

            //Informa al usuario
            Toast.makeText(context, "Imposible conectar . . .", Toast.LENGTH_SHORT).show();

            Log.d(xxx, "Fallo en la conexion al server ftpClient.connect(ip): " + e.getMessage());
            Log.d(xxx, "Fallo en la conexion al server ftpClient.connect(ip): " + e.getLocalizedMessage());
            Log.d(xxx, "Fallo en la conexion al server ftpClient.connect(ip): " + e.getCause());
            Log.d(xxx, "Fallo en la conexion al server ftpClient.connect(ip): " + e.getStackTrace().toString());
            return false;	//En caso de que no sea posible la conexion
        } */


        catch (SocketException e) {
            e.printStackTrace();
            Log.d(xxx, "Fallo 1 en la conexion al server ftpClient.connect(ip): " + e.getMessage());
            return false;	//En caso de que no sea posible la conexion, Si no retorno, me sale el fallo de strict mode


        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d(xxx, "Fallo  2 en la conexion al server ftpClient.connect(ip): " + e.getMessage());
            return false;	//En caso de que no sea posible la conexion


        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d(xxx, "Fallo 3 en la conexion al server ftpClient.connect(ip): " + e.getMessage());
            return false;	//En caso de que no sea posible la conexion


        }



        /*
        //Hace login en el servidor
        if (ftpClient.login(usuario, contrasena)){

            //Informa al usuario
            Toast.makeText(context, "Login correcto . . .", Toast.LENGTH_SHORT).show();
            return true;	//En caso de login correcto
        }
        else{

            //Informa al usuario
            Toast.makeText(context, "Login incorrecto . . .", Toast.LENGTH_SHORT).show();

            Log.d(xxx, "Fallo en el login al servidor ftp ftpClient.login(usuario, contrasena))");
            return false;	//En caso de login incorrecto
        }  */

    }
}//Fin de la clase
