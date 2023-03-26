package ucr.example.arce.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import ucr.example.arce.R;
import ucr.example.arce.fragments.CartFragment;

public class PushNotificationService extends FirebaseMessagingService {

    /*
     * Do: Prints new token for device
     * Param: token
     * Return: None
     * */
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        System.out.println("Token: " + s);
        guardarToken(s);

    }

    /*
     * Do: Guarda el token de un usuario especifico
     * Param: token
     * Return: None
     * */
    private void guardarToken(String s) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("token");
        ref.child("ejemplo").setValue(s);
    }

    /*
     * Do: Listener de las notificaciones entrantes
     * Param: token
     * Return: None
     * */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String from = remoteMessage.getFrom();
        if (remoteMessage.getData().size() > 0) {
            String titulo = remoteMessage.getData().get("titulo");
            String detalle = remoteMessage.getData().get("detalle");

            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                oreoVersion(titulo,detalle);
            //}
        }

    }

    /*
     * Do: Listener en caso de tener una version diferente
     * Param: Titlo mensaje y detalle del mismo
     * Return: None
     * */
    private void oreoVersion(String titulo, String detalle) {
        String id = "mensaje";

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id, "nuevo",NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);
        }

        builder.setAutoCancel(true).setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentText(detalle)
                .setContentIntent(clicknotification())
                .setContentInfo("nuevo");
        Random random = new Random();
        int idNotify = random.nextInt(8000);

        assert nm != null;
        nm.notify(idNotify, builder.build());
    }

    /*
     * Do: Metodo que consigue el context de un activity a un fragment
     * Param: none
     * Return: Intent
     * */
    public PendingIntent clicknotification() {
        Intent nf = new Intent(getApplicationContext(), CartFragment.class);
        nf.putExtra("color", "rojo");
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this,0,nf,0);
    }

}
