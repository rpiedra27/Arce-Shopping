package ucr.example.arce.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.regex.Pattern;

import ucr.example.arce.R;
import ucr.example.arce.activities.LoadImageActivity;
import ucr.example.arce.activities.LogInActivity;
import ucr.example.arce.activities.MainActivity;
import ucr.example.arce.activities.NavBarActivity;

public class UserFragment extends Fragment {

    //Variable usadas
    DatePickerDialog picker;
    EditText eText;
    int yearOfBirth;
    int monthOfBirth;
    int dayOfBirth;
    TextView EdittextMail, textViewUserAge;
    EditText EdittextViewNombre, EdittextViewId;
    Button buttonActualizar,changePassBtn;
    ImageButton imageViewUserIcon;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    Spinner spinner;


    public UserFragment() {
    }

    /*
     * Do: Metodo encargado de cargar la vista y las acciones iniciales del fragmento
     * Param: None
     * Return:none
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //vista
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        Button btnLanzarActivity = (Button) view.findViewById(R.id.logOut);
        ImageButton simpleImageButton = (ImageButton) view.findViewById(R.id.imageViewUserIcon);

        //En cargo de querer cambiar la foto de perfil, envia al activity que es
        simpleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoadImageActivity.class);
                startActivity(intent);
            }
        });
        btnLanzarActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
            }
        });


        return view;
    }

    /*
     * Do: Metodo encargado de desplegar el datepicker una vez que se toque para usar
     * Param: None
     * Return:none
     * */

    public  void showDatePicker(){
        //Variable que almacenara el valor
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //variables a obtener
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                dayOfBirth = dayOfMonth;
                                monthOfBirth =  monthOfYear+1;
                                yearOfBirth = year;
                            }
                        }, year, month, day);
                picker.show();
            }
        });

    }

    /*
     * Do: Se encarga de convertir la fecha de nacimiento, en la edad numerica normal mostrada
     * Param: None
     * Return: Enterio con el valor de la edad
     * */

    public int getAge(){
        //Usa dia mes y anho para el calculo
        LocalDate today = LocalDate.now();
        int daysSubs = today.getDayOfMonth() - dayOfBirth;
        int monthSubs = today.getMonthValue() - monthOfBirth;
        int yearSubs = today.getYear() - yearOfBirth;

        if (monthSubs < 0) {
            yearSubs  = yearSubs  - 1;
        } else
        if (monthSubs == 0) {
            if (daysSubs < 0) {
                yearSubs = yearSubs - 1;
            }
        }
        return yearSubs;
    }

    /*
     * Do: Funcion encargada de obtener la provincia, para la actualizacion del perfil
     * Param: None
     * Return: None
     * */

    private void getCProvincia() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.ProvinciasNames, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    /*
     * Do: Ejecuta el despliegue de la galeria
     * Param: None
     * Return: None
     * */
    public void takePictureFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    /*
     * Do: Ejecuta el despliegue de la camara
     * Param: None
     * Return: None
     * */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, 1);

    }
    /*
     * Do: Llamado a la DB para el llamado de los daos de usuario y Layout respectivo
     * Param: view. BundeInstance
     * Return: none
     * */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //carga objetos de vista
        EdittextViewNombre = getView().findViewById(R.id.EdittextViewNombre);
        EdittextViewId = getView().findViewById(R.id.EdittextViewId);
        EdittextMail = getView().findViewById(R.id.EdittextMail);
        textViewUserAge = getView().findViewById(R.id.textViewUserAge);
        changePassBtn = (Button) view.findViewById(R.id.buttonChangePassword);
        buttonActualizar = getView().findViewById(R.id.buttonActualizar);
        imageViewUserIcon = (ImageButton)view.findViewById(R.id.imageViewUserIcon);
        eText= (EditText) view.findViewById(R.id.editText1);
        showDatePicker();
        spinner = (Spinner) view.findViewById(R.id.textViewProvince);
        // Crea el ArrayAdapter usando el string array por defecto en el spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.ProvinciasNames, android.R.layout.simple_spinner_item);
        // Especifica los valores del spinner en vista
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Aplica el adapter
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        //Valores de firestore
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Muestra al usuario actual en el perfil, usando los datos traidos desde la nube
        if(currentUser != null){
            DocumentReference documentReference=  mFirestore.collection("users").document(mAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        //Carga todos los objetos de vista
                        DocumentSnapshot document = task.getResult();
                        EdittextViewNombre.setText(document.getString("name"));
                        EdittextViewId.setText( document.getString("userIdentification"));
                        EdittextMail.setText(document.getString("email"));
                        int spinnerPosition = adapter.getPosition(document.getString("province"));
                        spinner.setSelection(spinnerPosition);
                        textViewUserAge.setText("Edad:" + document.getLong("age").toString());

                    }
                }
            });

            //Se carga las instacias para traer la foto
            FirebaseStorage storage  = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            //Se carga la foto de perfil como binaria, luego se procede a transformar a drawable para poder mostrarla
            StorageReference fotoPerfil = storageRef.child("perfil"+currentUser.getUid().toString());
            final long ONE_MEGABYTE = 1024 * 1024;
            fotoPerfil.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes){
                    //carga la imagen en el objeto que muestra la imagen al usuario
                    Drawable image = new BitmapDrawable(getResources(),BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    imageViewUserIcon.setImageDrawable(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        }
        buttonActualizar.setOnClickListener(v -> updateInfo());
        changePassBtn.setOnClickListener(v -> beginPassChange());
    }


    /*
     * Do: Metodo encargado del cambio de la informacion del perfil, en este caso todas la casillas u objetos editables sin incluir la foto que pueden ser editables
     * Param: none
     * Return: none
     * */
    private  void updateInfo(){
        //Se obtienen las referencias de firestore
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //se ejecuta el showDatepicker y se cargan las referencias
            showDatePicker();
            DocumentReference documentReference=  mFirestore.collection("users").document(mAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        mFirestore = FirebaseFirestore.getInstance();
                        DocumentSnapshot document = task.getResult();

                        //If que cambia nombre del usuario, tambien valida que sea correcto
                        if(!EdittextViewNombre.getText().toString().equals(document.getString("name"))  ){
                           if(nameIsValid(EdittextViewNombre.getText().toString())){
                               mFirestore.collection("users").document(mAuth.getCurrentUser().getUid()).update("name",EdittextViewNombre.getText().toString());
                               Toast.makeText(getContext(), "Actualizacion de nombre exitosa",Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(getContext(), NavBarActivity.class);
                               startActivity(intent);
                           }else{
                               Toast.makeText(getContext(), "Nombre no valido",Toast.LENGTH_SHORT).show();
                           }
                        }

                        //If que cambia la identificacion personal del usuario, tambien valida que sea correcto
                        if(!EdittextViewId.getText().toString().equals(document.getString("userIdentification"))){
                            mFirestore.collection("users").document(mAuth.getCurrentUser().getUid()).update("userIdentification",EdittextViewId.getText().toString());
                            Toast.makeText(getContext(), "Actualizacion de ID exitosa",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), NavBarActivity.class);
                            startActivity(intent);
                        }

                        //If que cambia la provincia donde vive el usuario, tambien valida que sea correcto
                        if(!spinner.getSelectedItem().equals(document.getString("province"))){
                            mFirestore.collection("users").document(mAuth.getCurrentUser().getUid()).update("province",spinner.getSelectedItem());
                            Toast.makeText(getContext(), "Actualizacion de provincia exitosa",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), NavBarActivity.class);
                            startActivity(intent);
                        }
                        //If que cambia la edad del usuario usando datepicker, tambien valida que sea correcto
                       if(getAge() != document.getLong("age")){
                           if(ageIsValid(getAge())){
                               mFirestore.collection("users").document(mAuth.getCurrentUser().getUid()).update("age",getAge());
                               Toast.makeText(getContext(), "Actualizacion de edad exitosa",Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(getContext(), NavBarActivity.class);
                               startActivity(intent);
                           }else{
                               Toast.makeText(getContext(), "Edad no valida",Toast.LENGTH_SHORT).show();
                           }
                        }
                    }
                }
            });
        }
    }

    /*
     * Do: Metodo encargado del cambio de contrasena interno mediante firestore, este cierra la sesion y envia un link para cambiar la contrasena
     * Param: none
     * Return: none
     * */
    private void beginPassChange() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Se envio un correo para restablecimiento de la contraseña y cerraremos su sesion", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getContext(), LogInActivity.class);
                        startActivity(intent);
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
}
    /*
     * Do: Verifica que la edad sea un valor valido y sea mayor a 13 y menor a 120
     * Param: edad que ingreso el usuario
     * Return: Retorna booleano indicando si el valor es valido
     * */
    private boolean ageIsValid(int age) {
        if(age>13 && age<120){
            return true;
        }
        return false;
    }

    /*
     * Do: Verifica que el nombre sea un valor valido y no sobrepase los 30 digitos
     * Param: Nombre que ingreso el usuario
     * Return: Retorna booleano indicando si el valor es valido
     * */
    private boolean nameIsValid(String name) {
        Pattern patron = Pattern.compile("^[a-zA-Z\\s]*$");
        if (name.equals("") || !patron.matcher(name).matches() || name.length() > 30) {
            return false;
        }
        return true;
    }
}