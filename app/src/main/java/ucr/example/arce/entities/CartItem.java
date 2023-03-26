package ucr.example.arce.entities;

public class CartItem {
    private String id;
    private String name;
    private int price;
    private int quantity;
    private String thumbnail;
    private int totalPrice;
    private String date;

    /*
     * Do: Constructor vacio entidad
     * Param: none
     * Return: none
     * */
    public CartItem() {
    }

    /*
     * Do: Constructor que inicializa los atributos de la entidad
     * Param: name, description, price, rating, stock, category, thumbnail, images
     * Return: none
     * */
    public CartItem(String id, String name, int price, int quantity, String thumbnail, String date) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.thumbnail = thumbnail;
        this.date = date;
    }

    /*
     * Do: Devuelve el identificador del item
     * Param: none
     * Return: identificador del item como cadena de caracteres
     * */
    public String getId() {return id;}

    /*
     * Do: Asigna el identificador del item
     * Param: identificador del item
     * Return: none
     * */
    public void setId(String id) {this.id = id;}

    /*
     * Do: Devuelve el nombre del item
     * Param: none
     * Return: nombre del item como cadena de caracteres
     * */
    public String getName() {
        return name;
    }

    /*
     * Do: Asigna el nombre del item
     * Param: nombre del item
     * Return: none
     * */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * Do: Devuelve el precio del item
     * Param: none
     * Return: el precio del item como un entero
     * */
    public int getPrice() {
        return price;
    }

    /*
     * Do: Asigna el precio del item
     * Param: precio del item
     * Return: none
     * */
    public void setPrice(int price) {
        this.price = price;
    }

    /*
     * Do: Devuelve la cantidad en carrito del item
     * Param: none
     * Return: cantidad en carrito del item como un entero
     * */
    public int getQuantity() {
        return quantity;
    }

    /*
     * Do: Asigna la cantidad en carrito del item
     * Param: cantidad en carrito del item
     * Return: none
     * */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /*
     * Do: Devuelve la imagen del item
     * Param: none
     * Return: imagen del item como string
     * */
    public String getThumbnail() {
        return thumbnail;
    }

    /*
     * Do: Asigna la imagen del item
     * Param: imagen del item
     * Return: none
     * */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /*
     * Do: Devuelve el producto del precio del item con la cantidad en el carrito
     * Param: none
     * Return: el total según el precio del item y la cantidad en el carrito
     * */
    public int getTotalPrice() {
        return totalPrice;
    }

    /*
     * Do: Guarda el producto del precio del item con la cantidad en el carrito
     * Param: precio total de items en el carrito
     * Return: none
     * */
    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    /*
     * Do: Devuelve la fecha de la compra del item con la cantidad en el carrito
     * Param: none
     * Return: fecha de compra del item
     * */
    public String getDate() {
        return date;
    }

    /*
     * Do: Establece la fecha de la compra del item en el carrito
     * Param: none
     * Return: la fecha de la compra del item en el carrito
     * */
    public void setDate(String date) {
        this.date = date;
    }
};
