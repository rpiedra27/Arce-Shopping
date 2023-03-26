package ucr.example.arce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ucr.example.arce.R;

/** For internet checking */
import ucr.example.arce.controllers.NetworkChangeReceiver;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.net.ConnectivityManager;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

public class ProductInformation extends AppCompatActivity {

    TextView NombreProducto2, PrecioProducto2,CategoriaProducto,DescripcionProducto,CantidadSeleccionada;
    String NombreProducto1, imagenProducto;
    ImageCarousel carousel;
    final int maxPurchaseAmount = 10;
    int count = 0;
    int PrecioProducto1, ProductQuantity;

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    String idUserFirebase;
    String productId;

    /** For internet checking */
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    /*
     * Do: Se mapea un id unico para cada producto para desplegar su informacion
     * Param: Bundle savedInstanceState
     * Return: None
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information);

        productId = getIntent().getStringExtra("id");

        NombreProducto2 = findViewById(R.id.NombreProducto2);
        PrecioProducto2 = findViewById(R.id.PrecioProducto2);
        CategoriaProducto = findViewById(R.id.CategoriaProducto);
        DescripcionProducto = findViewById(R.id.DescripcionProducto);
        CantidadSeleccionada = (TextView) findViewById(R.id.CantidadSeleccionada);


        carousel = findViewById(R.id.imageViewProducto);
        // Register lifecycle. For activity this will be lifecycle/getLifecycle() and for fragments it will be viewLifecycleOwner/getViewLifecycleOwner().
        carousel.registerLifecycle(getLifecycle());


        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUserFirebase = mAuth.getCurrentUser().getUid();

        getProduct(productId);

        broadcastReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    /*
     * Do: Se incrementa la cantidad seleccionada
     * Param: View v
     * Return: None
     * */
    public void increment(View v) {
        DocumentReference productRef = mFirestore.collection("products").document(productId);
        productRef.get().addOnCompleteListener(productTask -> {
            if (productTask.isSuccessful()) {
                DocumentSnapshot productDoc = productTask.getResult();
                if (productDoc.exists()) {
                    if(count == maxPurchaseAmount){
                        Toast.makeText(this, "El límite de cantidad es 10", Toast.LENGTH_LONG).show();
                    } else {
                        int currentStock = Integer.parseInt(productDoc.get("stock").toString());
                        if (currentStock > 0) {
                            count++;
                            CantidadSeleccionada.setText("" + count);
                            productRef.update("stock", FieldValue.increment(-1));
                        } else {
                            Toast.makeText(this, R.string.msg_no_inventory, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "No existe el producto", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Fallo al pedir la información del producto", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
     * Do: Se decrementa la cantidad seleccionada
     * Param: View v
     * Return: None
     * */
    public void decrement(View v) {
        if(count <= 0){
            count = 0;
        } else {
            count--;
            DocumentReference productRef = mFirestore.collection("products").document(productId);
            productRef.update("stock", FieldValue.increment(1));
        }
        CantidadSeleccionada.setText("" + count);
    }

    /*
     * Do: Se despliega en pantalla la informacion especifica de un producto relacionado a un id en firestore
     * Param: Id del producto especifico
     * Return: None
     * */
    private void getProduct(String id) {
        DocumentReference productRef = mFirestore.collection("products").document(id);
        productRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String productName = document.getString("title");
                    String productImage = document.getString("thumbnail");
                    String productCategory = document.getString("category");
                    String productDescription = document.getString("description");
                    String productPrice = document.get("price").toString();

                    List<CarouselItem> list = new ArrayList<>();
                    List<String> productImages = (List<String>) document.get("images");
                    for(int i = 0; i < productImages.size(); i++){
                        list.add(
                                new CarouselItem(
                                        productImages.get(i)
                                )
                        );
                    }
                    carousel.setData(list);
                    NombreProducto2.setText(productName);
                    NombreProducto1 = productName;
                    imagenProducto = document.get("thumbnail").toString();
                    ProductQuantity = Integer.parseInt(document.get("stock").toString());
                    //PrecioProducto2.setText("$ " + String.valueOf(products.getPrice()));
                    PrecioProducto1 = Integer.parseInt(productPrice);
                    PrecioProducto2.setText("$ " + productPrice);
                    CategoriaProducto.setText(productCategory);
                    DescripcionProducto.setText(productDescription);
                } else {
                    Toast.makeText(ProductInformation.this, "No existe el producto", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ProductInformation.this, "Fallo al pedir la información", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
     * Do: Se resta la cantidad seleccionada al stock y se agrega la orden al carrito
     * Param: View v
     * Return: None
     * */
    public void buyProduct(View v) {
        if (count > ProductQuantity || count == 0) {
            Toast.makeText(this, "Cantidad seleccionada no disponible", Toast.LENGTH_LONG).show();
        } else {
            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();

            Date currentTime = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            String actualDate = dateFormat.format(currentTime);

            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("id", productId);
            productInfo.put("name", NombreProducto1);
            productInfo.put("quantity", count);
            productInfo.put("price", PrecioProducto1);
            productInfo.put("date", actualDate);
            productInfo.put("thumbnail",imagenProducto);

            mFirestore.collection("shoppingCart").document(idUserFirebase).collection("products").document(productId)
                    .set(productInfo,SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProductInformation.this, "Producto agregado al carrito", Toast.LENGTH_LONG).show();
                            // Restar stock del producto agregado
                            DocumentReference productRef = mFirestore.collection("products").document(productId);
                            productRef.update("stock", FieldValue.increment(-count));
                            Intent intent = new Intent (ProductInformation.this, NavBarActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProductInformation.this, "Error al al agregar producto", Toast.LENGTH_LONG).show();
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