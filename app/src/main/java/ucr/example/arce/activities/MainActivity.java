package ucr.example.arce.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.content.BroadcastReceiver;


import androidx.appcompat.app.AppCompatActivity;

import ucr.example.arce.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ucr.example.arce.controllers.NetworkChangeReceiver;

public class MainActivity extends AppCompatActivity {

    /** For internet checking */
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    /*
     * Do: Metodo encargado de llamar a la interfaz grafica de la pantalla de inicio
     * Param: Instancia actual
     * Return: None
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(MainActivity.this, NavBarActivity.class);
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        broadcastReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    /*
     * Do: Metodo encargado de llamar y redirgir a la interfaz grafica de registro
     * Param: Vista de registro
     * Return: None
     * */

    public void callRegister(View view){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    /*
     * Do: Metodo encargado de llamar y redirgir a la interfaz grafica de inicio de sesion
     * Param: Vista de inicio de sesion
     * Return: None
     * */

    public void iniciarSesion(View view) {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    /*
     * Do: Registra que el receptor escuche (cierto tipo de ecentos del sistema) a eventos de tipo conexión.
     *     Define que el receptor escuchará los eventos que coinciden con el intent
     *     (usado como filtro al indicar de qué es el intent para escuchar cierto tipo de eventos del sistema)
     *     será llamado con cada intent que coincida con el intent filter (en este caso).
     * Param: None
     * Return: None
     * */
    protected void registerNetworkBroadcastReceiver(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        //if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    /*
     * Do: Desactiva el receptor de cambios en el estado de red de este proyecto/aplicación
     *     para evitar filtrar el receptor fuera del contexto de la actividad
     * Param: None
     * Return: None
     * */
    protected void unregisterNetwork(){
        try{
            unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    /*
     * Do: Cuando termina el ciclo de vida del Activity, antes de que la actividad se destruya,
     * invoca al método que da de baja al receiver/receptor
     * Param: None
     * Return: None
     * */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterNetwork();
    }

}