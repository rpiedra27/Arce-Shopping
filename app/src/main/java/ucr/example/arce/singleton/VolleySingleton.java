package ucr.example.arce.singleton;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    private RequestQueue requestQueue;
    private static VolleySingleton mInstance;

    /*
     * Do: Constructor clase Singleton
     * Param: Context
     * Return: none
     * */
    private VolleySingleton(Context context){
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /*
     * Do: getmInstance para el llamado de las imagenes en el carrusel
     * Param: Context
     * Return: instancia de la imagen
     * */
    public static synchronized VolleySingleton getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }

}
