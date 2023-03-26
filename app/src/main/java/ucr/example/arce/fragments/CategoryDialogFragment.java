package ucr.example.arce.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import ucr.example.arce.R;
import com.google.gson.Gson;
import java.util.ArrayList;

/** Dialogo que se utiliza para desplegar y elegir categorias de productos, para luego desplegar
 * la lista de productos filtrada.
 * Contiene una interfaz que debe ser implementada en la Activity que lo contiene, esta sirve para
 * pasar los eventos de vuelta.
 */
public class CategoryDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String catChoice);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener listener;

    /*
     * Do: Crea el dialogo para elegir categorias
     * Param: Bundle savedInstanceState
     * Return: instancia del fragmento con todas las categorias y eventos
     * */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getArguments() != null) {
            Gson gson = new Gson();
            ArrayList<String> categoryArrayList = gson.fromJson(getArguments().getString(getString(R.string.category_arg)), ArrayList.class);
            categoryArrayList.add(0, getString(R.string.category_option_all));
            String[] catArray = categoryArrayList.toArray(new String[0]);
            final String[] choice = new String[1];

            builder.setTitle(R.string.cat_dialog_title)
                // Especifica las opciones del dialogo, usa el arreglo de opciones y la opcion por defecto
                .setSingleChoiceItems(catArray, 0, (dialog, which)
                    -> choice[0] = catArray[which])
                // Listeners para los botones de accion
                .setPositiveButton(R.string.positive_category_btn, (dialog, id) ->
                    listener.onDialogPositiveClick(CategoryDialogFragment.this, choice[0]))
                .setNegativeButton(R.string.negative_category_btn, (dialog, id) ->
                    listener.onDialogNegativeClick(CategoryDialogFragment.this));
        }
        return builder.create();
    }

    /*
     * Do: Verificar que la actividad que contiene a este fragment implementa la interfaz requerida
     * Param: Context
     * Return: Nada
     * */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instancia NoticeDialogListener para poder enviar eventos al host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Debe implementar la interfaz NoticeDialogListener" +
                    " en la actividad contenedora");
        }
    }
}
