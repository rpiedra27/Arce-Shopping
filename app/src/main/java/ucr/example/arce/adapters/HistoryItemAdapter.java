package ucr.example.arce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ucr.example.arce.entities.CartItem;

import ucr.example.arce.R;
import ucr.example.arce.entities.HistoryItem;

import java.util.List;

/*
    Adaptador que permite la creación de los items del RecyclerView que contiene el historial de compras.
 */
public class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.MyViewHolder>{
    private Context context;
    private List<HistoryItem> historyItems;
    private final OnItemClickListener listener;

    /*
        Interfaz para manejar los clicks del boton para ver los detalles de una compra.
        La Activity o Fragment hospedadora de este RecyclerView debe implementar este método
     */
    public interface OnItemClickListener {
        void detailsBtnOnClick(int position);
    }

    /*
     * Do: Constructor
     * Param: Context, lista de items del historial, listener para el botón.
     * */
    public HistoryItemAdapter(Context context, List<HistoryItem> historyItems, OnItemClickListener listener) {
        this.context = context;
        this.historyItems = historyItems;
        this.listener = listener;
    }

    /*
     * Do: Se crea el viewHolder en shopping_history_item.xml donde irán los items.
     * Param: ViewGroup parent y viewType
     * Return: ViewHolder con los datos
     * */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.shopping_history_item, parent, false);
        return new MyViewHolder(v, listener);
    }

    /*
     * Do: Llama los valores de la clase HistoryItem para asignarlos al ViewHolder y luego al RecyclerView
     * Param: MyViewHolder holder y posicion de los elementos
     * */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.date.setText(historyItems.get(position).getDate());
    }

    /*
     * Do: Reporta el número de items en la lista.
     * Param: Nada
     * Return: Número de elementos en la lista
     * */
    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date; //Fecha de la compra
        Button detailsBtn; //Boton para ver los detalles de una compra

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            date = itemView.findViewById(R.id.purchase_date);
            detailsBtn = itemView.findViewById(R.id.details_button);
            detailsBtn.setOnClickListener(v -> listener.detailsBtnOnClick(getAdapterPosition()));
        }
    }
}
