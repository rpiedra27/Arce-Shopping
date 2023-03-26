package ucr.example.arce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

import ucr.example.arce.R;

/** For internet checking */
import ucr.example.arce.controllers.NetworkChangeReceiver;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.net.ConnectivityManager;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputLayout tilUsername;
    String sEmail,sPass;
    private FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    /** For internet checking */
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    /*
     * Do: Todos los metodos necesarios para realizar el cambio de contrasena
     * Param: none
     * Return: none
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        tilUsername = (TextInputLayout) findViewById(R.id.til_usuario1);
        Button acceptButton = (Button) findViewById(R.id.boton_cambio);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserData(v);
            }
        });

        broadcastReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

    }

    /*
     * Do: Validación del nombre de usuario
     * Param: String username
     * Return: boolean true si la validación es correcta
     * */
    private boolean userIsValid(String username) {
        if (username.length() > 256 || !Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            return false;
        }
        return true;
    }

    /*
     * Do: Metodo encargado de la generacion de una contraseña del tamano que se desee y de forma aleatoria para que sea segura
     * Param: Tamano de la contraseña deseada
     * Return: String con la contraseña creada
     * */
    public String GetPassword(int length){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        for(int i = 0; i < length; i++){
            char c = chars[rand.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    /*
     * Do: Se validan los datos ingresados por el usuario, se envia un correo con la contraseña provisional
     * y se guardan los cambios en la base de datos.
     * Param: View v
     * */
    private void validateUserData(View v) {
        String username = tilUsername.getEditText().getText().toString();
        boolean a = userIsValid(username);

        if (a) {
            String pass = GetPassword(16);
           // EmailSender emailer = new EmailSender();
            //emailer.sendForgotPasswordEmail(username, pass);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(username)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Se envio un correo para restablecimiento de la contraseña", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ForgotPasswordActivity.this, LogInActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

        }else{
            Toast.makeText(this, "¡Credenciales Inválidas!", Toast.LENGTH_LONG).show();
        }
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