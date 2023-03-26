package ucr.example.arce.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ucr.example.arce.R;
import ucr.example.arce.adapters.HistoryItemAdapter;
import ucr.example.arce.entities.HistoryItem;

/** For internet checking */
import ucr.example.arce.controllers.NetworkChangeReceiver;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.net.ConnectivityManager;

/*
    Activity que contiene el historial de compras de un usuario. Se lista mediante un RecyclerView
    que contiene fechas, al seleccionar una fecha se carga un nuevo Activity con los detalles de esa
    compra.
 */
public class ShoppingHistoryActivity extends AppCompatActivity {
    private RecyclerView historyItemsRV;
    private List<HistoryItem> historyItemList;
    private HistoryItemAdapter adapter;
    ImageView profilePic;

    /** For internet checking */
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    /*
     * Do: Todos los metodos relacioandos al historial de compras de un usuario dado
     * Param: None
     * Return: None
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_history);
        profilePic = findViewById(R.id.profile_pic);
        setTitle(getString(R.string.shopping_history_title));

        historyItemList = new ArrayList<>();
        FirebaseAuth mAuth;
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String idUserFirebase = mAuth.getCurrentUser().getUid();

        // Request de los datos del usuario para cargar la información desplegada en la parte superior
        DocumentReference userRef = db.collection("users").document(idUserFirebase);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> userData = document.getData();
                    TextView usernameText = findViewById(R.id.name_text);
                    usernameText.setText(userData.get("name").toString());
                    TextView userProvinceText = findViewById(R.id.location_text);
                    userProvinceText.setText(userData.get("province").toString());
                } else {
                    Log.d("get user data", "No such document");
                }
            } else {
                Log.d("get user data", "get failed with ", task.getException());
            }
        });

        // Request para la imagen del usuario que se despliega en la parte superior de la pantalla
        FirebaseStorage storage  = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        StorageReference fotoPerfil = storageRef.child("perfil"+currentUser.getUid().toString());
        final long ONE_MEGABYTE = 1024 * 1024;
        fotoPerfil.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes){
                Drawable image = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                profilePic.setImageDrawable(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        // Request para obtener las fechas de las compras y poblar el RecyclerView
        DocumentReference historyRef = db.collection("purchaseHistory").document(idUserFirebase);
        historyRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> timestampList = (List<String>) document.get("timestamps");
                    for(int i = 0; i < timestampList.size(); i++) {
                        HistoryItem item = new HistoryItem(timestampList.get(i));
                        historyItemList.add(item);
                    }
                    adapter = new HistoryItemAdapter(this, historyItemList,
                            this::detailsBtnClicked
                    );
                    historyItemsRV = findViewById(R.id.purchase_items_RV);
                    historyItemsRV.setLayoutManager(new LinearLayoutManager(this));
                    historyItemsRV.setAdapter(adapter);
                } else {
                    Log.d("get history", "No such document");
                }
            } else {
                Log.d("get history", "get failed with ", task.getException());
            }
        });

        broadcastReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    /*
     * Do: Carga un Activity con los detalles de la compra seleccionada.
     * Param: position: Indica la posición en el arreglo historyItemList para extraer la fecha y cargar la Activity
     * Return: nada
     * */
    public void detailsBtnClicked(int position) {
        Intent intent = new Intent(this, HistoryDetailsActivity.class);
        intent.putExtra("date", historyItemList.get(position).getDate());
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