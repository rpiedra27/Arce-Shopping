package ucr.example.arce.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import ucr.example.arce.R;
import ucr.example.arce.activities.ProductInformation;
import ucr.example.arce.entities.Products;

public class FirebaseProductAdapter extends FirestoreRecyclerAdapter<Products, FirebaseProductAdapter.ViewHolder> {

    private Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FirebaseProductAdapter(@NonNull FirestoreRecyclerOptions<Products> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull FirebaseProductAdapter.ViewHolder viewHolder, int i, @NonNull Products products) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAbsoluteAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.name.setText(products.gettitle());
        viewHolder.price.setText("$ " + String.valueOf(products.getPrice()));
        Glide.with(context).load(products.getThumbnail()).into(viewHolder.thumbnail);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, ProductInformation.class);
                intent.putExtra("id",id);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public FirebaseProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name; //Nombre del producto
        TextView price; //Precio del producto
        ImageView thumbnail; //Imagen del producto

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.price_tag);
            thumbnail = itemView.findViewById(R.id.imageView);

        }
    }
}
