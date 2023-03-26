package ucr.example.arce.fragments;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ucr.example.arce.R;
import ucr.example.arce.activities.ShoppingHistoryActivity;
import ucr.example.arce.adapters.CartItemAdapter;
import ucr.example.arce.controllers.EmailSender;
import ucr.example.arce.databinding.FragmentCartBinding;
import ucr.example.arce.entities.CartItem;

/*
    Fragmento que se utiliza para representar el carrito de compras de un usuario. Despliega todos
    los productos actualmente en el carrito y contiene funcionalidades para borrarlos y para
    confirmar una compra.
 */
public class CartFragment extends Fragment{
    private List<CartItem> productList;
    private RecyclerView productRV;
    CartItemAdapter adapter;
    private FragmentCartBinding binding;
    private EmailSender emailer;
    private int totalPrice;
    final int maxPurchaseAmount = 10;
    Button confirmPurchaseBtn, deleteCartBtn, viewHistoryBtn;
    TextView userProvinceText, usernameText, totalPriceText;
    ImageView profilePic;
    private Map<String, Object> userData;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    String idUserFirebase;

    /* Do: Metodo encargado de establecer la vista del fragmento
     * Param: Layout inflater para vista, container
     * Return: bindig
     */

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /* Do: Metodo encargado de establecer la vista del fragmento para el xml
     * Param: Vista y bundle
     * Return: none
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Llama a los objetos de vista
        productList = new ArrayList<>();
        productRV = view.findViewById(R.id.cart_items_RV);
        totalPrice = 0;
        userProvinceText = view.findViewById(R.id.location_text);
        usernameText = view.findViewById(R.id.name_text) ;
        totalPriceText = view.findViewById(R.id.total_price_text);
        profilePic = view.findViewById(R.id.profile_pic);
        confirmPurchaseBtn = view.findViewById(R.id.confirm_order_btn);
        confirmPurchaseBtn.setOnClickListener(v -> confirmPurchase());
        deleteCartBtn = view.findViewById(R.id.cancel_order_btn);
        deleteCartBtn.setOnClickListener(v -> clearCartItems());
        viewHistoryBtn = view.findViewById(R.id.view_history_btn);
        viewHistoryBtn.setOnClickListener(v -> loadShoppingHistory());

        //Crea las instancias necesarias
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUserFirebase = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = mFirestore.collection("users").document(idUserFirebase);

        //Muestra los datos del usuario en la parte superior
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userData = document.getData();
                    usernameText.setText(userData.get("name").toString());
                    userProvinceText.setText(userData.get("province").toString());
                } else {
                    Log.d("get user data", "No such document");
                }
            } else {
                Log.d("get user data", "get failed with ", task.getException());
            }
        });

        //Instancias de Firebase
        FirebaseStorage storage  = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Referencia y carga de la foto de perfil
        StorageReference fotoPerfil = storageRef.child("perfil"+currentUser.getUid().toString());
        final long ONE_MEGABYTE = 1024 * 1024;
        fotoPerfil.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Drawable image = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            profilePic.setImageDrawable(image);
        }).addOnFailureListener(exception -> {

        });

        //Deteccion de incremento y decremento de los productos
        mFirestore.collection("shoppingCart").document(idUserFirebase).collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            productList.add(document.toObject(CartItem.class));
                        }
                        //Ejecucion de las acciones para los eventos de decrecer y crecer cantidad de productos
                        adapter = new CartItemAdapter(getContext(), productList,
                                new CartItemAdapter.OnItemClickListener(){
                                    @Override public void minusBtnOnClick(int position) {
                                        CartItem item = productList.get(position);
                                        decreaseItemQuantity(item, position);
                                    }
                                    @Override public void plusBtnOnClick(int position) {
                                        CartItem item = productList.get(position);
                                        increaseItemQuantity(item, position);
                                    }
                                }
                        );

                        //Cambio de precio
                        productRV.setLayoutManager(new LinearLayoutManager(getContext()));
                        productRV.setAdapter(adapter);
                        for(int i = 0; i < productList.size(); i++){
                            totalPrice += productList.get(i).getPrice() * productList.get(i).getQuantity();
                        }
                        totalPriceText.setText(getString(R.string.cart_total) + totalPrice);

                    } else {
                        Log.d("get history details", "Error getting documents: ", task.getException());
                        Toast.makeText(getActivity(), "Fallo al pedir la información", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /* Do: Valida si existe inventario suficiente para incrementar la cantidad de algun producto
     * que el usuario desee comprar. Si es posible, incrementa la cantidad y el precio.
     * Param: item: producto al que se quiere incrementar la cantidad
     * Return: Nada
     */
    private void increaseItemQuantity(CartItem item, int pos) {
        DocumentReference productRef = mFirestore.collection("products").document(item.getId());
        productRef.get().addOnCompleteListener(productTask -> {
            if (productTask.isSuccessful()) {
                DocumentSnapshot productDoc = productTask.getResult();
                if (productDoc.exists()) {
                    int currentStock = Integer.parseInt(productDoc.get("stock").toString());

                    //Cambios en la base de datos en la nube
                    if (currentStock > 0) {
                        DocumentReference cartItemRef = mFirestore.collection("shoppingCart")
                                .document(idUserFirebase).collection("products").document(item.getId());
                        cartItemRef.get().addOnCompleteListener(cartTask -> {
                            if (cartTask.isSuccessful()) {
                                DocumentSnapshot cartItemDoc = cartTask.getResult();
                                if (cartItemDoc.exists()) {
                                    if(item.getQuantity() == maxPurchaseAmount){
                                        Toast.makeText(getActivity(), "El límite de cantidad es 10", Toast.LENGTH_LONG).show();
                                    } else {
                                        //Actualizaciones
                                        cartItemRef.update("quantity", FieldValue.increment(1));
                                        productList.get(pos).setTotalPrice(productList.get(pos).getTotalPrice()
                                                + productList.get(pos).getPrice());
                                        productList.get(pos).setQuantity(productList.get(pos).getQuantity() + 1);
                                        productRef.update("stock", FieldValue.increment(-1));
                                        totalPrice += item.getPrice();
                                        totalPriceText.setText(getString(R.string.cart_total) + totalPrice);
                                        adapter.notifyItemChanged(pos);

                                        //Verifaciones de else
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "No existe el producto en el carrito", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Fallo al pedir la información del carrito", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), R.string.msg_no_inventory, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "No existe el producto", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "Fallo al pedir la información del producto", Toast.LENGTH_LONG).show();
            }
        });
    }

    /* Do: Reduce la cantidad de un producto del carrito y se restaura la cantidad en el inventario.
     * Si llega a cero, no formará parte de la orden
     * Param: item: producto al que se quiere decrementar la cantidad
     * Return: Nada
     */
    private void decreaseItemQuantity(CartItem item, int pos){
        if(item.getQuantity() > 0) {
            DocumentReference productRef = mFirestore.collection("products").document(item.getId());
            productRef.update("stock", FieldValue.increment(1));
            DocumentReference cartItemRef = mFirestore.collection("shoppingCart")
                    .document(idUserFirebase).collection("products").document(item.getId());
            cartItemRef.update("quantity", FieldValue.increment(-1));
            productList.get(pos).setTotalPrice(productList.get(pos).getTotalPrice()
                    - productList.get(pos).getPrice());
            productList.get(pos).setQuantity(productList.get(pos).getQuantity() - 1);
            totalPrice -= item.getPrice();
            totalPriceText.setText(getString(R.string.cart_total) + totalPrice);
            adapter.notifyItemChanged(pos);
        }
    }

    /* Do: Confirma la compra de todos los articulos del carrito. Envía un email de confirmación con
     * la orden de un usuario. Guarda la compra en el historial y limpia el carrito.
     * Param: Nada
     * Return: Nada
     */
    private void confirmPurchase() {
        callPushNotification();
        EmailSender emailer = new EmailSender();
        emailer.sendPurchaseConfirmation(userData.get("email").toString(), productList);
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String currentDate = dateFormat.format(currentTime);

        // Create purchaseHistory document
        Map<String, Object> data = new HashMap<>();
        data.put("created", true);
        mFirestore.collection("purchaseHistory").document(idUserFirebase).set(data, SetOptions.merge());

        //Ingresa los datos de la compra a la base de datos en linea
        Map<String, Object> productData = new HashMap<>();
        for(int i = 0; i < productList.size(); i++) {
            CartItem currentItem = productList.get(i);
            productData.put("name", currentItem.getName());
            productData.put("quantity", currentItem.getQuantity());
            productData.put("totalPrice", currentItem.getPrice() * currentItem.getQuantity());
            productData.put("thumbnail", currentItem.getThumbnail());
            Toast.makeText(getActivity(), "Compra exitosa", Toast.LENGTH_LONG).show();

            //Verificaciones de coleccion
            mFirestore.collection("purchaseHistory").document(idUserFirebase)
                .collection(currentDate).document(currentItem.getName())
                    .set(productData)
                        .addOnSuccessListener(unused -> Log.d("add to history", "added product successfully"))
                        .addOnFailureListener(e -> Log.d("add to history", "failed to add", e));
        }
        mFirestore.collection("purchaseHistory").document(idUserFirebase)
                .update("timestamps", FieldValue.arrayUnion(currentDate));

        //Limpia y restablece el carro
        productList.clear();
        adapter.notifyDataSetChanged();
        clearFirestoreCart();
        totalPriceText.setText(getString(R.string.cart_total) + 0);
    }

    /* Do: Metodo encargado de las notificaciones push, mediante el uso de un token presente
        en el perfil del telefono del usuario en la base de datos en linea
     * Param: Nada
     * Return: Nada
     */
    private void callPushNotification() {

        //Instancias de la base de datos en linea
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                RequestQueue myrequest = Volley.newRequestQueue(getActivity());
                JSONObject json = new JSONObject();

                try {
                    //Se busca el token del usuario y se carga la notifiacion
                    String token = documentSnapshot.getString("deviceToken");
                    json.put("to",token);
                    JSONObject notification = new JSONObject();
                    notification.put("titulo","¡Su compra ha sido realizada con exito!");
                    notification.put("detalle","Gracias por comprar con nosotros. En breve le llegará un correo de confirmación con los detalles de su compra");
                    json.put("data", notification);
                    String URL = "https://fcm.googleapis.com/fcm/send";
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,json,null,null) {
                        @Override
                        public Map<String, String> getHeaders(){
                            //Se ejecuta la notificacion
                            Map<String, String> header = new HashMap<>();
                            header.put("content-type","application/json");
                            header.put("authorization", "key=AAAALAVFWrI:APA91bFhECdyuYghroUP9YPk_XqQDSGPX5uQuGn_avd4uP7uv6SEN5UrO1h1lhqoM2yTXxd5AhF0p2X8Cl7gzNQ8PJovSsWndNn5mpXRQUDfC45bHbvPe_eSDxC6wr6QgQqrxNdafnkR");
                            return header;
                        }
                    };
                    myrequest.add(request);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    /* Do:Metodo encargado de borrar el carrito de comrpas del usuario activo
     * Param: Nada
     * Return: Nada
     */
    private void clearFirestoreCart() {
        mFirestore.collection("shoppingCart").document(idUserFirebase).collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    } else {
                        Log.d("GET cart", "Error getting documents: ", task.getException());
                    }
                });
    }

    /* Do: Elimina todos los productos del carrito de un usuario.
     * Param: none
     * Return: none
     */
    private void clearCartItems() {
        for(int i = 0; i < productList.size(); i++) {
            DocumentReference productRef = mFirestore.collection("products")
                    .document(productList.get(i).getId());
            productRef.update("stock", FieldValue.increment(-productList.get(i).getQuantity()));
        }
        clearFirestoreCart();
        productList.clear();
        adapter.notifyDataSetChanged();
        totalPriceText.setText(getString(R.string.cart_total) + 0);
        Toast.makeText(getActivity(), R.string.msg_clear_cart, Toast.LENGTH_LONG).show();
    }

    /* Do:Metodo encargado de cargar el activity del carrito de compras
     * Param: Nada
     * Return: Nada
     */
    public void loadShoppingHistory() {
        Intent intent = new Intent(getContext(), ShoppingHistoryActivity.class);
        startActivity(intent);
    }

    /* Do:Metodo encargado de terminar el binding
     * Param: Nada
     * Return: Nada
     */
    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}