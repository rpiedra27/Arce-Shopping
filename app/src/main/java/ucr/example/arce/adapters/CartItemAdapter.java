package ucr.example.arce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ucr.example.arce.entities.CartItem;

import ucr.example.arce.R;

import java.util.List;

/*
    Adaptador que permite la creación de cada item del RecyclerView que contiene cada producto del
    carrito de compras.
 */
public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.MyViewHolder>{
    private Context context;
    private List<CartItem> productData;
    private final OnItemClickListener listener;

    /*
        Interfaz para manejar los clicks de los botones para incrementar/decrementar la cantidad de
        un producto. La Activity o Fragment hospedadora de este RecyclerView debe implementar estos métodos
     */
    public interface OnItemClickListener {
        void plusBtnOnClick(int position);
        void minusBtnOnClick(int position);
    }

    /*
     * Do: Constructor
     * Param: Context, lista de productos, listener para los botones
     * */
    public CartItemAdapter(Context context, List<CartItem> productData, OnItemClickListener listener) {
        this.context = context;
        this.productData = productData;
        this.listener = listener;
    }

    /*
     * Do: Se crea el viewHolder en cart_item.xml donde irán los productos.
     * Param: ViewGroup parent y viewType
     * Return: ViewHolder con los datos
     * */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.cart_item, parent, false);
        return new MyViewHolder(v, listener);
    }

    /*
     * Do: Llama los valores de la clase ProductClassModel para asignarlos al ViewHolder y luego al recyclerView
     * Param: MyViewHolder holder y posicion de los elementos
     * */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(productData.get(position).getName());
        holder.price.setText("$ " + String.valueOf(productData.get(position).getPrice()));
        holder.quantity.setText(String.valueOf(productData.get(position).getQuantity()));
        //Se utiliza la libreria Glide para el llamado a imagenes en recyclerView
        Glide.with(context).load(productData.get(position).getThumbnail()).into(holder.thumbnail);
    }

    /*
     * Do: Reporta el número de productos en la lista.
     * Param: Nada
     * Return: Número de elementos en la lista
     * */
    @Override
    public int getItemCount() {
        return productData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name; //Nombre del producto
        TextView price; //Precio del producto
        TextView quantity; //Cantidad de este articulo
        ImageView thumbnail; //Imagen del producto
        ImageButton minusBtn; //Boton para incrementar cantidad de un producto
        ImageButton plusBtn; //Boton para disminuir cantidad de un producto

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.price_tag);
            quantity = itemView.findViewById(R.id.cart_item_count);
            thumbnail = itemView.findViewById(R.id.imageView);
            minusBtn = itemView.findViewById(R.id.minus_button);
            plusBtn = itemView.findViewById(R.id.plus_button);

            minusBtn.setOnClickListener(v -> listener.minusBtnOnClick(getAdapterPosition()));
            plusBtn.setOnClickListener(v -> listener.plusBtnOnClick(getAdapterPosition()));
        }
    }
}
