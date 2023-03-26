package ucr.example.arce.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import ucr.example.arce.controllers.ClosestLocation;
import ucr.example.arce.controllers.EmailSender;
import ucr.example.arce.controllers.NetworkChangeReceiver;

import ucr.example.arce.R;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /** For internet checking */
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    private TextInputLayout tilId;
    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilAge;
    private String tilProvincia;
    String sEmail,sPass;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    /** GPS */
    public static final int REQUEST_CODE = 1;
    EditText lat, lon, dir;
    Button obtener, salida;
    ProgressBar progressBar;
    FusedLocationProviderClient fusedLocationProviderClient;
    /** GPS */

    /** Get closest location */
    ClosestLocation closestLocation = new ClosestLocation();
    private TextView tvLatitude, tvLongitude, tvState;
    /** Get closest location */

    /** User location */
    String userLocation = "";
    /** User location */

    /*DATE PICKER */
    DatePickerDialog picker;
    EditText eText;
    Button btnGet;
    TextView tvw;
    int yearOfBirth;
    int monthOfBirth;
    int dayOfBirth;
    @Override
    /*
    * Do: Metodo encargado de recibir lo datos ingresados desde la interfaz grafica y establecer esperar a la accion que lleve a la
    * validacion de los dto
    * Param: Instancia actual
    * Return: none
    * */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tilId = (TextInputLayout) findViewById(R.id.til_ID);
        tilName = (TextInputLayout) findViewById(R.id.til_nombre);
        tilEmail = (TextInputLayout) findViewById(R.id.til_correo);
        //tilDate = (TextInputLayout) findViewById(R.id.til_date);
        Button buttonRegister = (Button) findViewById(R.id.boton_registrar);
        Button buttonProvince = (Button) findViewById(R.id.btnObtenerProvincia);

        /*DATE PICKER*/

        showDatePicker();

        Spinner spinner = (Spinner) findViewById(R.id.campo_Provincia);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ProvinciasNames, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        spinner.setSelection(0);
        userLocation = spinner.getSelectedItem().toString();

        buttonRegister.setOnClickListener(v -> {
            try {
                if (validateUserData()){
                    userLocation = spinner.getSelectedItem().toString();
                    Intent intent = new Intent(this, LogInActivity.class);
                    startActivity(intent);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        /** For checkin internet connection */
        broadcastReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

    }

    /*
     * Do: Despliega datepicker para seleccioanr la fecha
     * Param: None
     * Return: None
     * */
    public  void showDatePicker(){

        eText=(EditText) findViewById(R.id.editText1);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(Register.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                dayOfBirth = dayOfMonth;
                                monthOfBirth =  monthOfYear+1;
                                yearOfBirth = year;
                            }
                        }, year, month, day);
                picker.show();
            }
        });

    }
    /*
     * Do: Verifica que el id sea un valor valido
     * Param: Id que ingreso el usuario
     * Return: Retorna booleano indicando si el valor es valido
     * */
    private boolean idIsValid(String id) {
        if(id.equals("")){
            tilId.setError("ID Invalido");
            return false;
        }
        return true;
    }


    /*
     * Do: Verifica que el nombre sea un valor valido y no sobrepase los 30 digitos
     * Param: Nombre que ingreso el usuario
     * Return: Retorna booleano indicando si el valor es valido
     * */
    private boolean nameIsValid(String name) {
        Pattern patron = Pattern.compile("^[a-zA-Z\\s]*$");
        if (name.equals("") || !patron.matcher(name).matches() || name.length() > 30) {
            tilName.setError("Nombre Invalido");
            return false;
        } else {
            tilName.setError(null);
        }
        return true;
    }

    /*
     * Do: Verifica que el correo sea un valor valido y tenga sintaxis de correo
     * Param: Correo que ingreso el usuario
     * Return: Retorna booleano indicando si el valor es valido
     * */
    private boolean emailIsValid(String email) {
        if (email.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Correo electrónico inválido");
            return false;
        } else {
            tilEmail.setError(null);
        }
        return true;
    }

    /*
     * Do: Verifica que la edad sea un valor valido y sea mayor a 0 y menor a 120
     * Param: edad que ingreso el usuario
     * Return: Retorna booleano indicando si el valor es valido
     * */
    private boolean ageIsValid(int age) {
        if(age>0 && age<120){
            return true;
        }
        return false;
    }

    /*
     * Do: Metodo encargado de la validacion de datos en general que fueron ingresados por el usuario
     * Param: None
     * Return: booleano que indica si los datos son validos
     * */
    private boolean validateUserData() throws IOException {
            String id = tilId.getEditText().getText().toString();
            String name = tilName.getEditText().getText().toString();
            String email = tilEmail.getEditText().getText().toString();
            String date = eText.getText().toString();

            if(id.equals("") || name.equals("") || email.equals("") || date.equals("") ){
                Toast.makeText(this, "Favor llenar los campos", Toast.LENGTH_LONG).show();
                return false;
            }

            LocalDate today = LocalDate.now();

            boolean a = idIsValid(id);
            boolean b = nameIsValid(name);
            boolean c = emailIsValid(email);
            boolean d = ageIsValid(getAge());

            if (a && b && c && d ) {
                // OK, se pasa a la siguiente acción
                //Toast.makeText(this, "Se guarda el registro", Toast.LENGTH_LONG).show();

                String pass = GetPassword(8);
                EmailSender emailer = new EmailSender();
                emailer.sendPasswordEmail(email, pass);

                Toast.makeText(this, "Se envió una contraseña provisional al correo", Toast.LENGTH_LONG).show();

                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String idUserFirebase = mAuth.getCurrentUser().getUid();
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", idUserFirebase); //id tabla
                        map.put("userIdentification", id); //Cedula
                        map.put("name", name);
                        map.put("email", email);
                        map.put("age", getAge());
                        map.put("new", true);
                        map.put("deviceToken",null);
                        map.put("province", userLocation);

                        mFirestore.collection("users").document(idUserFirebase).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Register.this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, "Error al registrar usuario", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Error al registrar usuario", Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                Toast.makeText(this, "Error, favor verifique la información ingresada", Toast.LENGTH_LONG).show();
                return false;
            }

        return true;
    }


    /*
     * Do: Calcula la edad segun la informacion insertada en el date picker
     * Param: None
     * Return: edad en int
     * */
    public int getAge(){

        LocalDate today = LocalDate.now();
        int daysSubs = today.getDayOfMonth() - dayOfBirth;
        int monthSubs = today.getMonthValue() - monthOfBirth;
        int yearSubs = today.getYear() - yearOfBirth;

        if (monthSubs < 0) {
            yearSubs  = yearSubs  - 1;
        } else
        if (monthSubs == 0) {
            if (daysSubs < 0) {
                yearSubs = yearSubs - 1;
            }
        }
        return yearSubs;
    }
    /*
    * Do: Metodo encargado de la generacion de una contrasena del tamano que se desee y de forma aleatoria para que sea segura
    * Param: tamano de la contrasela deseada
    * Return: String con la contrasena creada
    * */
    public String GetPassword(int length){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        //char[] chars = "1".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        for(int i = 0; i < length; i++){
            char c = chars[rand.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    /*
     * Do: Selecciona la provincia en donde se encuentra
     * Param: AdapterView, View, position y id
     * */
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        tilProvincia =  parent.getItemAtPosition(pos).toString();
    }

    /*
     * Do: No hay ninguna opcion seleciconada de provincia
     * Param: AdapterView
     * */
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    /*
     * Do: Pedir permiso para acceder al GPS y cuando lo tenga, obtiene la coordenada
     * Param: View
     * Return:
     * */

    /** GPS */
    public void obtenerCoordendasActual(View view) {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions( Register.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        } else {

            getCoordenada();
        }
    }

    /*
     * Do: Indicar si el permiso fue denegado o pedir la coordenada
     * Param: requestCode, permisos y resultados concedidos
     * Return: None
     * */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCoordenada();
            } else {
                Toast.makeText(this, "Permiso Denegado ..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * Do: Funcion encargada de obtener la localización
     * Param: None
     * Return: None
     * */

    private void getCoordenada() {

        try {
            //progressBar.setVisibility(View.VISIBLE);
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(Register.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        String provincia = closestLocation.getProvinciaMasCercana(latitude, longitude);
                        Toast.makeText(Register.this, provincia, Toast.LENGTH_SHORT).show();
                        userLocation=provincia;
                        Spinner spinner = (Spinner) findViewById(R.id.campo_Provincia);
                        // Create an ArrayAdapter using the string array and a default spinner layout
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Register.this,
                                R.array.ProvinciasNames, android.R.layout.simple_spinner_item);
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        spinner.setAdapter(adapter);
                        if (provincia != null) {
                            int spinnerPosition = adapter.getPosition(provincia);
                            spinner.setSelection(spinnerPosition);
                        }
                    }
                }

            }, Looper.myLooper());

        }catch (Exception ex){
            System.out.println("Error es :" + ex);
        }

    }

    /*
     * Do: Sale de la aplicación
     * Param: View
     * Return: None
     * */

    public  void Exit(View view){
        this.finish();
    }
    /** GPS */

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