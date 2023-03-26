package ucr.example.arce.models;

public class UserModelClass {

    String id, name,  email,  age,  location, password;

    public UserModelClass() {
    }

    /*
     * Do: Ingresa los datos de la clase modelo
     * Param:  Informacion del usuaario como el id, nombre, correo, edad, provincia donde vive y la password
     * Return:None
     * */
    public UserModelClass(String id, String nombre, String correo, String edad, String provincia, String contrasenia) {
        this.id = id;
        this.name = nombre;
        this.email = correo;
        this.age = edad;
        this.location = provincia;
        this.password = contrasenia;
    }

    /*
     * Do: Metodos set y get de los usuarios
     * */
    public String getId() {
        return id;
    }

    public String getNombre() {
        return name;
    }

    public String getCorreo() {
        return email;
    }

    public String getEdad() {
        return age;
    }

    public String getProvincia() {
        return location;
    }

    public String getContrasenia() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.name = nombre;
    }

    public void setCorreo(String correo) {
        this.email = correo;
    }

    public void setEdad(String edad) {
        this.age = edad;
    }

    public void setProvincia(String provincia) {
        this.location = provincia;
    }

    public void setContrasenia(String contrasenia) {
        this.password = contrasenia;
    }
}