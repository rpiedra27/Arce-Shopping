package ucr.example.arce.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import ucr.example.arce.R;

/** For internet checking */
import ucr.example.arce.controllers.NetworkChangeReceiver;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.net.ConnectivityManager;

public class NewProductActivity extends AppCompatActivity {

    EditText txtProduct,txtDescription,txtPrice,txtRate,txtStock,txtCategoria,txtThumbnail,txtImages,txtId,txtBrand;
    Button buttonAddProduct;

    /** For internet checking */
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    /*
     * Do: Se mapean los elementos de input para que la informacion ingrese a la base de datos
     * Param: Bundle savedInstanceState
     * Return: None
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        txtProduct = findViewById(R.id.txtProduct);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);
        txtRate = findViewById(R.id.txtRate);
        txtStock = findViewById(R.id.txtStock);
        txtCategoria = findViewById(R.id.txtCategoria);
        txtThumbnail = findViewById(R.id.txtThumbnail);
        txtImages = findViewById(R.id.txtImages);
        txtId = findViewById(R.id.txtId);
        txtBrand = findViewById(R.id.txtBrand);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);

        broadcastReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }


    /*
     * Do: Se limpian las casillas de informacion producto
     * Param: none
     * Return: None
     * */
    private void clearData() {
        txtProduct.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtRate.setText("");
        txtStock.setText("");
        txtBrand.setText("");
        txtCategoria.setText("");
        txtThumbnail.setText("");
        txtImages.setText("");
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