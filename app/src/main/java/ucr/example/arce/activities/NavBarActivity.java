package ucr.example.arce.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ucr.example.arce.R;
import ucr.example.arce.databinding.ActivityNavBarBinding;
import ucr.example.arce.fragments.CategoryDialogFragment;
import ucr.example.arce.fragments.StoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** For internet checking */
import ucr.example.arce.controllers.NetworkChangeReceiver;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.net.ConnectivityManager;

public class NavBarActivity extends AppCompatActivity implements CategoryDialogFragment.NoticeDialogListener {

    private ActivityNavBarBinding binding;

    /** For internet checking */
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    /*
     * Do: Creacion actividad barra de navegación
     * Param: Bundle savedInstanceState
     * Return: None
     * */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder( R.id.navigation_user, R.id.navigation_store, R.id.navigation_cart).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        broadcastReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    /*
     * Do: Implementacion de interfaz para recibir eventos del DialogFragment
     * Param: DialogFragment dialog, String choice
     * Return: None
     * */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String choice) {
        Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        ((StoreFragment) fragment).getDialogSelection(choice);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
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