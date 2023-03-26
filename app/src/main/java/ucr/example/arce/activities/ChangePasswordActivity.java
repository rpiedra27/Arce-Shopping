package ucr.example.arce.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import ucr.example.arce.R;

/** For internet checking */
import ucr.example.arce.controllers.NetworkChangeReceiver;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.net.ConnectivityManager;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout tilPassword, tilRepeatedPassword;
    public String actualUserName;
    private FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    /** For internet checking */
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    @Override
    /*
     * Do: Valida si los datos de la cuenta son correctos
     * Param: savedInstanceState
     * */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        tilPassword = (TextInputLayout) findViewById(R.id.til_password);
        tilRepeatedPassword = (TextInputLayout) findViewById(R.id.til_password_repeat);
        Button acceptButton = (Button) findViewById(R.id.login_btn);
        acceptButton.setOnClickListener(this::validateData);

        broadcastReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    /*
     * Do: Valida que las contraseñas sean iguales y que tengan el largo adecuado
     * Param: password y repeatedPassword
     * Return: boolean true en caso de ser password valida
     * */
    private boolean isPasswordValid(String password, String repeatedPassword) {
        if (password.length() < 8) {
            tilPassword.setError(getString(R.string.pass_too_short));
            return false;
        }
        if (!password.equals(repeatedPassword)) {
            tilPassword.setError(getString(R.string.pass_not_equal));
            tilRepeatedPassword.setError(getString(R.string.pass_not_equal));
            return false;
        } else {
            tilPassword.setError(null);
        }
        return true;
    }

    /*
     * Do: Valida los datos del usuario, realiza el cambio en la base de datos y redirige al Login
     * Param: view
     * Return: none
     * */
    private void validateData(View v) {
        String password = tilPassword.getEditText().getText().toString();
        String repeatedPassword = tilRepeatedPassword.getEditText().getText().toString();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (isPasswordValid(password, repeatedPassword)) {
            assert currentUser != null;
            currentUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                        editor.putString("email",currentUser.getEmail());
                        editor.putString("password",password);
                        editor.putBoolean("isLogin",true);
                        editor.apply();
                        Toast.makeText(ChangePasswordActivity.this, getString(R.string.pass_change_success), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, NavBarActivity.class);
                        startActivity(intent);
                    }
                }
            });

            //Se inicializa el usuairo como nuevo para poder crear una contrasena nueva la primera vez del registro
            mFirestore = FirebaseFirestore.getInstance();
            mFirestore.collection("users").document(currentUser.getUid()).update("new",false);
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        task.getException();
                        return;
                    }
                    // Token telefono notificacion
                    String tokenNotification = task.getResult();
                    //dbUsers.insertToken(tokenNotification,email);
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    mFirestore.collection("users").document(currentUser.getUid()).update("deviceToken",tokenNotification);
                }
            });
            DocumentReference documentReference=  mFirestore.collection("users").document(currentUser.getUid());
                   documentReference.update(
                           "new",false
                   ).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()) {

                           }
                       }
                   });

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