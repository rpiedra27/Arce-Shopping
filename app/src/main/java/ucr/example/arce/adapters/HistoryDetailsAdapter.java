package ucr.example.arce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import ucr.example.arce.R;
import ucr.example.arce.entities.CartItem;

import java.util.List;

/*
    Adaptador para crear los items del RecyclerView que contiene los detalles de una compra en el historial.
 */
public class HistoryDetailsAdapter extends RecyclerView.Adapter<HistoryDetailsAdapter.MyViewHolder>{
    private final Context context;
    private final List<CartItem> purchaseItems;

    /*
     * Do: Constructor
     * Param: Context, lista de items de la compra.
     * */
    public HistoryDetailsAdapter(Context context, List<CartItem> purchaseItems) {
        this.context = context;
        this.purchaseItems = purchaseItems;
    }

    /*
     * Do: Se crea el viewHolder en history_details_item.xml donde irán los items.
     * Param: ViewGroup parent y viewType
     * Return: ViewHolder con los datos
     * */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.history_details_item, parent, false);
        return new MyViewHolder(v);
    }

    /*
     * Do: Llama los valores de la clase CartItem para asignarlos al ViewHolder y luego al RecyclerView
     * Param: MyViewHolder holder y posicion de los elementos
     * */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int quantity = purchaseItems.get(position).getQuantity();
        holder.name.setText(purchaseItems.get(position).getName());
        holder.price.setText("Precio unitario: $" + purchaseItems.get(position).getTotalPrice() / quantity);
        holder.totalPrice.setText("Total: $" + purchaseItems.get(position).getTotalPrice());
        if(quantity > 1)
            holder.quantity.setText(String.valueOf(quantity) + " unidades");
        else
            holder.quantity.setText(String.valueOf(quantity) + " unidad");
        //Se utiliza la libreria Glide para el llamado a imagenes en recyclerView
        Glide.with(context).load(purchaseItems.get(position).getThumbnail()).into(holder.thumbnail);
    }

    /*
     * Do: Reporta el número de items en la lista.
     * Param: Nada
     * Return: Número de elementos en la lista
     * */
    @Override
    public int getItemCount() {
        return purchaseItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView totalPrice;
        TextView quantity;
        ImageView thumbnail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.unit_price);
            totalPrice = itemView.findViewById(R.id.total_price);
            quantity = itemView.findViewById(R.id.item_quantity);
            thumbnail = itemView.findViewById(R.id.item_picture);
        }
    }
}
