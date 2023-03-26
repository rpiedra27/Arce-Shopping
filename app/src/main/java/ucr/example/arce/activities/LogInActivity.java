package ucr.example.arce.activities;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.concurrent.Executor;

import ucr.example.arce.R;

/** For internet checking */
import ucr.example.arce.controllers.NetworkChangeReceiver;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.net.ConnectivityManager;

public class LogInActivity extends AppCompatActivity {

    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private JSONObject jsonObject;
    private String validPassword;
    private String validUser;
    private String isNew;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private static final int REQUEST_CODE = 1;
    private Executor executor;
    private androidx.biometric.BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    ImageView imageViewLogin;
    SharedPreferences sharedPreferences;

    /** For internet checking */
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    //CollectionReference users = mFirestore.collection("users");


    @Override
    /*
     * Do: Se crea un objeto usuario con las credenciales las cuales se deben validar
     * Param: Bundle savedInstanceState
     * */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        tilUsername = (TextInputLayout) findViewById(R.id.til_usuario);
        tilPassword = (TextInputLayout) findViewById(R.id.til_contrasenia);

        /*Huella*/

        imageViewLogin = findViewById(R.id.FingerPrint);

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(this, "App can authenticate using biometrics.",Toast.LENGTH_LONG);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "No biometric features available on this device..",Toast.LENGTH_LONG);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                Toast.makeText(this, "Biometric features are currently unavailable.",Toast.LENGTH_LONG);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;



        }
        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean("isLogin",false);
        if(isLogin){

            imageViewLogin.setVisibility(View.VISIBLE);
        }


        /*
         * Do: Autenticador de huella en casi de error
         * Param: None
         * */
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LogInActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Error en la autenticacion: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            /*
             * Do: Autenticador de huella en caso que la huella sea valida
             * Param: None
             * */
            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Huella Dactilar Valida", Toast.LENGTH_SHORT).show();
                String email = sharedPreferences.getString("email","");
                String password = sharedPreferences.getString("password","");
                validateUserDataFingerprint(email,password);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Error de Autenticacion",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        imageViewLogin.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });


        // Referencia Botón
        Button acceptButton = (Button) findViewById(R.id.boton_ingresar);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserData();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserData();
            }
        });

        broadcastReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

    }


    /*
     * Do: Intent para cambiar contrasena en caso de olvido login
     * Param: none
     * Return: none
     * */
    public void callForgotPassword(View view){
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    /*
     * Do: Validación del nombre de usuario
     * Param: String username
     * Return: boolean true si la validación es correcta
     * */
    private boolean userIsValid(String username) {
        if (username.length() > 256 || !username.equals(validUser) || !Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            return false;
        }
        return true;
    }

    /*
     * Do: Validación de la contraseña
     * Param: String password
     * Return: boolean true si la contraseña es valida
     * */
    private boolean passwordIsValid(String password) {
        if (!password.equals(validPassword)) {
            return false;
        }
        return true;
    }
    /*
     * Do: Se validan los datos ingresados por el usuario
     * Param: View v
     * */
    public void validateUserDataFingerprint(String emailFinger, String passFinger) {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        String username = emailFinger;
        String password = passFinger;

        validUser = username;
        boolean a = userIsValid(username);
        boolean b = passwordIsValid(password);

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this, "Inicio de sesión exitoso",Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putString("email",username);
                            editor.putString("password",password);
                            editor.putBoolean("isLogin",true);
                            editor.apply();
                            ChangePass();
                        } else {
                            Toast.makeText(LogInActivity.this, "Credenciales Invalidas", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*
     * Do: Se validan los datos ingresados por el usuario
     * Param: View v
     * */
    public void validateUserData() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        String username = tilUsername.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        validUser = username;
        boolean a = userIsValid(username);
        boolean b = passwordIsValid(password);

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this, "Inicio de session exitoso",Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putString("email",username);
                            editor.putString("password",password);
                            editor.putBoolean("isLogin",true);
                            editor.apply();
                            ChangePass();
                        } else {
            Toast.makeText(LogInActivity.this, "Credenciales Invalidas", Toast.LENGTH_SHORT).show();
        }
    }
});
        }

    /*
     * Do: Cambio de contrasena
     * Param: none
     * Return: none
     * */
        public void ChangePass() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
        DocumentReference documentReference=  mFirestore.collection("users").document(mAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if( document.getBoolean("new").toString().equals("true")){
                        Intent intent = new Intent(LogInActivity.this, ChangePasswordActivity.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(LogInActivity.this, NavBarActivity.class);
                        startActivity(intent);
                    }

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