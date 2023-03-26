package ucr.example.arce.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ucr.example.arce.R;
import ucr.example.arce.adapters.FirebaseProductAdapter;
import ucr.example.arce.adapters.ProductListAdapter;
import ucr.example.arce.controllers.NetworkChangeReceiver;
import ucr.example.arce.databinding.FragmentStoreBinding;
import ucr.example.arce.entities.Products;

public class StoreFragment extends Fragment {

    //String JSON donde se encuentran los productos
    private static String JSON_URL = "https://dummyjson.com/products";

    //List<ProductModelClass> productList; //Lista donde se guardaran los objetos productos

    RecyclerView mRecycler; //Llamado objeto recyclerView
    FirebaseProductAdapter mAdapter;
    FirebaseFirestore mFirestore;

    private FragmentStoreBinding binding;
    ProductListAdapter adapter;
    Query query;

    ArrayList<Products> arrayListProducts;
    SearchView searchViewProduct;
    Button filterButton;
    public StoreFragment () {
    }
    
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    /*
     * Do: Llamado al recyclerView y adapter respectivo para el llamado de los productos y sus funciones
     * Param: view. BundeInstance
     * Return: none
     * */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        //Categorias
        final String[] res = new String[1];
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                getString(R.string.category_request),
                response -> {
                    res[0] = response;
                },
                error -> {
                    error.printStackTrace();
                });
        queue.add(stringRequest);
        //Variables para las vistas de los botones y demas objetos graficos
        Button button = (Button) view.findViewById(R.id.filterButton);
        button.setOnClickListener(v -> showNoticeDialog(res[0]));
        mFirestore = FirebaseFirestore.getInstance();
        searchViewProduct = view.findViewById(R.id.searchViewProduct);
        mRecycler = view.findViewById(R.id.RecyclerProductView);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        query = mFirestore.collection("products");

        //LLamado a la base de datos en linea para cargar los productos
        FirestoreRecyclerOptions<Products> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Products>().setQuery(query, Products.class).build();
        //Adapter que carga los productos en el recycle view
        mAdapter = new FirebaseProductAdapter(firestoreRecyclerOptions,getActivity());
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);

        searchProduct();

    }

    /*
     * Do: Metodo encargado de buscar productos bassado en la palabra que el cliente escribio
     * Param: none
     * Return: none
     * */
    private void searchProduct() {
        searchViewProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //Metodo interno que se ejecuta cuando busca
            @Override
            public boolean onQueryTextSubmit(String query) {
                textSearch(query);
                return false;
            }
            //Metodo interno que se ejecuta cuando escribe
            @Override
            public boolean onQueryTextChange(String query) {
                textSearch(query);
                return false;
            }
        });
    }

    /*
     * Do: Metodo encargado de filtrar las palabras para la busqueda, este consulta y retorna desde la base de datos en linea
     * Param: none
     * Return: none
     * */
    public void textSearch(String s){
        FirestoreRecyclerOptions<Products> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Products>()
                        .setQuery(query.orderBy("title")
                                .startAt(s).endAt(s+"~"), Products.class).build();
        mAdapter = new FirebaseProductAdapter(firestoreRecyclerOptions, getActivity());
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);
    }

    /*
     * Do: Metodo encargado de encender el listening
     * Param: none
     * Return: none
     * */
    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    /*
     * Do: Metodo encargado de apagar el listening
     * Param: none
     * Return: none
     * */
    @Override
    public void onStop() {

        super.onStop();
        mAdapter.startListening();
    }



    /*
     * Do: Clase encargado de realizar las validaciones y excepciones para conectar con el JSON y extraer la informacion
     * Param: none
     * Return: none
     * */
    public class GetData extends AsyncTask<String, String, String> {

        /*
         * Do: Carga de la conexion en background
         * Param: Generico del metodo
         * Return: String con la conexion reciente
         * */
        @Override
        protected String doInBackground(String... strings) {

            String current = "";

            try {
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(strings[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    int data = isr.read();
                    while(data != -1){
                        current += (char) data;
                        data = isr.read();
                    }
                    return current;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(urlConnection != null){
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return current;
        }

    }

    /*
     * Do: Binding de finalizacion
     * Param: none
     * Return: none
     * */
    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*
     * Do: Instancia el dialogo para escoger la categoria y lo despliega.
     * Param: res: Lista de categorias proveniente del API
     * Return: Nada
     * */
    public void showNoticeDialog(String res) {
        DialogFragment categoryDialog = new CategoryDialogFragment();
        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.category_arg), res);
        categoryDialog.setArguments(bundle);
        categoryDialog.show(fragManager, "catDialogFragment");
    }

    /*
     * Do: Actualiza la vista de productos a partir de la categoria seleccionada por el usuario.
     * Param: choice: Categoria elegida por el usuario
     * Return: Nada
     * */
    public void getDialogSelection(String choice){
        StoreFragment.GetData getData = new StoreFragment.GetData();
        if(choice == getString(R.string.category_option_all)) {

            //Carga de todos los productos
            FirestoreRecyclerOptions<Products> firestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Products>()
                            .setQuery(query.orderBy("title"), Products.class).build();
            mAdapter = new FirebaseProductAdapter(firestoreRecyclerOptions, getActivity());
            mAdapter.startListening();
            mRecycler.setAdapter(mAdapter);
        } else {

            //Carga de una categoria seleccionada
            FirestoreRecyclerOptions<Products> firestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Products>()
                            .setQuery(query.orderBy("category")
                                    .whereEqualTo("category",choice), Products.class).build();
            mAdapter = new FirebaseProductAdapter(firestoreRecyclerOptions, getActivity());
            mAdapter.startListening();
            mRecycler.setAdapter(mAdapter);
        }
    }
}