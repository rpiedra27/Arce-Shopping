package ucr.example.arce.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ucr.example.arce.R;
import ucr.example.arce.adapters.HistoryDetailsAdapter;
import ucr.example.arce.entities.CartItem;

/** For internet checking */
import ucr.example.arce.controllers.NetworkChangeReceiver;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.net.ConnectivityManager;

/*
    Activity que despliega los detalles de una compra particular. Incluyen el nombre del usuario, su foto y ubicación
    la fecha de la compra, el total y todos los productos con su foto, precio unitario, cantidad comprada y total.
    Además permite compartir los detalles en redes sociales y mensajería.
 */
public class HistoryDetailsActivity extends AppCompatActivity {
    ImageView profilePic;

    /** For internet checking */
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    /*
     * Do: Todos los metodos necesarios y relacionados al historial de productos
     * Param: none
     * Return: none
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);
        profilePic = findViewById(R.id.profile_pic);
        setTitle(getString(R.string.shopping_history_details_title));
        Bundle extra = getIntent().getExtras();
        final String purchaseDate = extra.getString("date");
        List<CartItem> historyDetailsList = new ArrayList<>();
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
        StorageReference fotoPerfil = storageRef.child("perfil" + currentUser.getUid().toString());
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

        TextView dateText = findViewById(R.id.purchase_date);
        dateText.setText("Fecha de la compra: " + purchaseDate);

        // Request para obtener los detalles de las compras y poblar el RecyclerView
        db.collection("purchaseHistory").document(idUserFirebase).collection(purchaseDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            historyDetailsList.add(document.toObject(CartItem.class));
                        }
                        TextView totalText = findViewById(R.id.purchase_total);
                        int total = 0;
                        for(int i = 0; i < historyDetailsList.size(); i++) {
                            total += historyDetailsList.get(i).getTotalPrice();
                        }
                        totalText.setText("Total: $ " + total);
                        HistoryDetailsAdapter adapter = new HistoryDetailsAdapter(this, historyDetailsList);
                        RecyclerView historyItemsRV = findViewById(R.id.purchase_items_RV);
                        historyItemsRV.setLayoutManager(new LinearLayoutManager(this));
                        historyItemsRV.setAdapter(adapter);
                    } else {
                        Log.d("get history details", "Error getting documents: ", task.getException());
                    }
                });
        Button shareBtn = findViewById(R.id.share_btn);
        NestedScrollView scrollview = (NestedScrollView) findViewById(R.id.scrollview);
        // Listener para el boton de compartir, obtiene las dimensiones del ScrollView entero para hacer el llamado al metodo que toma la screenshot.
        shareBtn.setOnClickListener(v -> sharePurchase(getBitmapFromView(scrollview, scrollview.getChildAt(0).getHeight(), scrollview.getChildAt(0).getWidth())));

        broadcastReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

    }

    /*
     * Do: Toma una screenshot de toda la pantalla, no solo la parte visible.
     * Param: view: la vista actual
     * Param: height: la altura de la pantalla
     * Param: width: el ancho de la pantalla
     * Return: Bitmap que contiene la screenshot
     * */
    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            int nightModeFlags = getResources().getConfiguration().uiMode &
                    Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    canvas.drawColor(Color.BLACK);
                    break;

                case Configuration.UI_MODE_NIGHT_NO:

                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    canvas.drawColor(Color.WHITE);
                    break;
            }
        }
        view.draw(canvas);
        return bitmap;
    }

    /*
     * Do: Abre el diálogo que permite compartir la screenshot tomada en redes sociales o aplicaciones de mensajería.
     * Param: bitmap: Screenshot con todos los detalles de la compra.
     * Return: nada
     * */
    private void sharePurchase(Bitmap bitmap) {
        String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"title", null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mi compra en Arce");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Mi compra en Arce"));
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