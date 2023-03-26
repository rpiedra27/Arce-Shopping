package ucr.example.arce.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import ucr.example.arce.R;
import ucr.example.arce.activities.ProductInformation;
import ucr.example.arce.entities.Products;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    ArrayList<Products> listProducts;
    private Context context;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    FragmentManager fm;


    /*
     * Do: Constructor del adaptador de productos
     * Param: ArrayList<Products> listProducts
     * Return: none
     * */
    public ProductListAdapter(ArrayList<Products> listProducts,Context context){
        this.listProducts = listProducts;
        this.context = context;
    }

    /*
     * Do: Se llama la vista individual del producto product_item
     * Param: ViewGroup parent, int viewType
     * Return: ProductViewHolder
     * */
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, null,false);
        return new ProductViewHolder(view);
    }

    /*
     * Do: Se llama de la entidad el tipo requerido
     * Param: ProductViewHolder holder, int position
     * Return: none
     * */
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        holder.name.setText(listProducts.get(position).gettitle());
        holder.price.setText("$ " + String.valueOf(listProducts.get(position).getPrice()));

        //Se utiliza la libreria Glide para el llamado a imagenes en recycleView
        Glide.with(context).load(listProducts.get(position).getThumbnail()).into(holder.thumbnail);

    }

    /*
     * Do: Cantidad de productos en la lista
     * Param: none
     * Return: size lista
     * */
    @Override
    public int getItemCount() {
       return listProducts.size();
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView name; //Nombre del producto
        TextView price; //Precio del producto
        ImageView thumbnail; //Imagen del producto

        /*
         * Do: Mapea los campos seleccionados con los datos especificos y ademas espera al click de
         * un producto especifico
         * Param: View itemView
         * Return: size lista
         * */
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.price_tag);
            thumbnail = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ProductInformation.class);
                    intent.putExtra("ID", listProducts.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });

        }
    }
}
