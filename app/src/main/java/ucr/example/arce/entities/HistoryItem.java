package ucr.example.arce.entities;

public class HistoryItem {
    private String date;
    private int id;

    /*
     * Do: Constructor que inicializa los atributos de la entidad del historial de compras
     * Param: date
     * Return: none
     * */
    public HistoryItem(String date) {
        this.date = date;
        this.id = id;
    }

    /*
     * Do: Constructor vacio entidad
     * Param: none
     * Return: none
     * */
    public HistoryItem() {}

    /*
     * Do: Devuelve la fecha cuando se hizo la compra
     * Param: none
     * Return: fecha cuando se hizo la compra
     * */
    public String getDate() {
        return date;
    }

    /*
     * Do: Asigna la fecha cuando se hizo la compra
     * Param: date
     * Return: none
     * */
    public void setDate(String date) {
        this.date = date;
    }

    /*
     * Do: Devuelve el identificador de la compra
     * Param: none
     * Return: id de la compra
     * */
    public int getId() {
        return id;
    }

    /*
     * Do: Establece el identificador de la compra
     * Param: id de la compra
     * Return: none
     * */
    public void setId(int id) {
        this.id = id;
    }
}
