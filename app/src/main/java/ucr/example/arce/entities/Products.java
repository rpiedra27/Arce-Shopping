package ucr.example.arce.entities;

import java.util.ArrayList;

public class Products {

    private int id;
    private String title;
    private String description;
    private int price;
    private int rating;
    private int stock;
    private String brand;
    private String category;
    private String thumbnail;

    /*
     * Do: Constructor vacio entidad
     * Param: none
     * Return: none
     * */
    public Products() {
    }

    /*
     * Do: Constructor que inicializa los atributos de la entidad
     * Param: title, description, price, rating, stock, category, thumbnail, images
     * Return: none
     * */
    public Products(int id, String title, String description, int price, int rating, int stock, String brand, String category, String thumbnail/*, ArrayList images*/) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.stock = stock;
        this.brand = brand;
        this.category = category;
        this.thumbnail = thumbnail;
    }

    /*
     * Do: Devuelve el identificador del producto
     * Param: none
     * Return: identificador del producto como entero
     * */
    public int getId() {
        return id;
    }

    /*
     * Do: Asigna el identificador del producto
     * Param: id del producto
     * Return: none
     * */
    public void setId(int id) {
        this.id = id;
    }

    /*
     * Do: Devuelve el titulo para nombrar el producto
     * Param: none
     * Return: tittle del producto
     * */
    public String gettitle() {
        return title;
    }

    /*
     * Do: Asigna el titulo para nombrar el producto
     * Param: tittle del producto
     * Return: none
     * */
    public void settitle(String title) {
        this.title = title;
    }

    /*
     * Do: Devuelve la descripcion del producto
     * Param: none
     * Return: descripcion del producto
     * */
    public String getDescription() {
        return description;
    }

    /*
     * Do: Asigna la descripcion del producto
     * Param: description del producto
     * Return: none
     * */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * Do: Devuelve el precio del producto
     * Param: none
     * Return: precio del producto
     * */
    public int getPrice() {
        return price;
    }

    /*
     * Do: Asigna el precio de la unidad del producto
     * Param: precio del producto
     * Return: none
     * */
    public void setPrice(int price) {
        this.price = price;
    }

    /*
     * Do: Obtiene la valoración del producto
     * Param: none
     * Return: rating del producto
     * */
    public int getRating() {
        return rating;
    }

    /*
     * Do: Asigna la valoracion del producto
     * Param: valoracion del producto
     * Return: none
     * */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /*
     * Do: Obtiene el stock del producto
     * Param: none
     * Return: stock del producto
     * */
    public int getStock() {
        return stock;
    }

    /*
     * Do: Asigna el stock del producto
     * Param: stock del producto
     * Return: none
     * */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /*
     * Do: Obtiene la marca del producto
     * Param: none
     * Return: brand del producto
     * */
    public String getBrand() {
        return brand;
    }

    /*
     * Do: Asigna la marca del producto
     * Param: brand del producto
     * Return: none
     * */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /*
     * Do: Obtiene la categoria del producto
     * Param: none
     * Return: category del producto
     * */
    public String getCategory() {
        return category;
    }

    /*
     * Do: Asigna la marca del producto
     * Param: brand del producto
     * Return: none
     * */
    public void setCategory(String category) {
        this.category = category;
    }

    /*
     * Do: Obtiene la imagen del producto
     * Param: none
     * Return: thumbnail del producto
     * */
    public String getThumbnail() {
        return thumbnail;
    }

    /*
     * Do: Asigna la imagen del producto
     * Param: thumbnail del producto
     * Return: none
     * */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

}
