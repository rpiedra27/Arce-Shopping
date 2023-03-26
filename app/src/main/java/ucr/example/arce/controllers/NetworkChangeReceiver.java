package ucr.example.arce.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.auth.FirebaseAuth;
import ucr.example.arce.activities.MainActivity;
import ucr.example.arce.R;

/*
    Clase encargada de verificar la conexión a internet del usuario
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    /*
     * Do: En caso de un evento, despliega un mensaje si no hay conexión a internet y saca al usuario al Main Activity
     * Param: context, intent
     * Return: None
     * */
    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            if (!isOnline(context)) { // Si no existe conexión a internet

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View layout_dialog = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog, null);
                builder.setView(layout_dialog);

                AppCompatButton btnRetry = layout_dialog.findViewById(R.id.btnRetry);

                // Show dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCancelable(false);

                dialog.getWindow().setGravity(Gravity.CENTER);
                btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        onReceive(context, intent);
                        if(!isOnline(context)){
                            goToMainScreen(context);
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /*
     * Do: Verifica si hay conexión a internet
     * Param: context
     * Return: true si el dispositivo tiene conexión a internet, false si el dispositivo tiene conexión a internet
     * */
    public boolean isOnline(Context context){
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return (networkInfo!=null && networkInfo.isConnected());
        }catch(NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Do: Se sale del activity actual y se mueve al Main Activity
     * Param: context
     * Return: true si el dispositivo tiene conexión a internet, false si el dispositivo no tiene conexión a internet
     * */
    private void goToMainScreen(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }
}