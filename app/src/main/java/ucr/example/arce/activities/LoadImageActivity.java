package ucr.example.arce.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import ucr.example.arce.R;

/** For internet checking */
import ucr.example.arce.controllers.NetworkChangeReceiver;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.net.ConnectivityManager;

public class LoadImageActivity extends AppCompatActivity {
    Button takePhoto, enterGallery;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    /** For internet checking */
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    /*
     * Do: Todos los metodos necesarios y relacionados a cargar la foto de perfil
     * Param: none
     * Return: none
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_load_image);
       takePhoto =(Button)  findViewById(R.id.cameraPhoto);
       enterGallery =(Button) findViewById(R.id.gallery);
       chooseProfilePicture();

        broadcastReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    /*
     * Do: Seleccionar la imagen de perfil
     * Param: none
     * Return: none
     * */
    public void chooseProfilePicture(){

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermissions()) {
                    takePictureFromCamera();
                }
            }
        });

        enterGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureFromGallery();
            }
        });
    }

    /*
     * Do: Seleccionar la imagen de perfil de la galeria del dispositivo
     * Param: none
     * Return: none
     * */
    public void takePictureFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    /*
     * Do: Tomar imagen de la camara
     * Param: none
     * Return: none
     * */
    public void takePictureFromCamera(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePicture, 2);
        }
    }

    /*
     * Do: Se realiza la carga de imagen desde el disposiotivo para desplegarla en el perfil
     * Param: codigo, resultado de confirmacion y datos de la imagen
     * Return: none
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        switch (requestCode)
        {
            case 1:
                if(resultCode == RESULT_OK){ //galeria
                    Uri selectedImageUri = data.getData();

                    FirebaseStorage storage  = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference fotoPerfil = storageRef.child("perfil"+currentUser.getUid().toString());
                    UploadTask uploadTask = fotoPerfil.putFile(selectedImageUri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(LoadImageActivity.this, "Fallo la carga de la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(LoadImageActivity.this, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent(LoadImageActivity.this, NavBarActivity.class);
                    startActivity(intent);
                }

                break;
            case 2:
                if(resultCode == RESULT_OK){ //CAMARA
                    Bundle bundle = data.getExtras();
                    Bitmap bitmapImage = (Bitmap) bundle.get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    //bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmapImage, "perfil"+currentUser.getUid().toString(), null);
                    FirebaseStorage storage  = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference fotoPerfil = storageRef.child("perfil"+currentUser.getUid().toString());
                    UploadTask uploadTask = fotoPerfil.putFile(Uri.parse(path));
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(LoadImageActivity.this, "Fallo la carga de la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(LoadImageActivity.this, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent(LoadImageActivity.this, NavBarActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    /*
     * Do: Permiso para el suo del sensor de la camara
     * Param: none
     * Return: boolean permiso
     * */
    private boolean checkAndRequestPermissions(){
        if(Build.VERSION.SDK_INT >= 23){
            int cameraPermission = ActivityCompat.checkSelfPermission(LoadImageActivity.this, Manifest.permission.CAMERA);
            if(cameraPermission == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(LoadImageActivity.this, new String[]{Manifest.permission.CAMERA}, 20);
                return false;
            }
        }
        return true;
    }

    /*
     * Do: Metodo de accion al aceptar el permiso de camara
     * Param: none
     * Return: none
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 20 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            takePictureFromCamera();
        }
        else
            Toast.makeText(LoadImageActivity.this, "Permission not Granted", Toast.LENGTH_SHORT).show();
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
