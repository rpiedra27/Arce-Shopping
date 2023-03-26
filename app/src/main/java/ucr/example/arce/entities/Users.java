package ucr.example.arce.entities;

public class Users {

    private String emailUser;
    private String pass;
    private String name;
    private String id;
    private int age;
    private String newPass;
    private String province;
    private String photo;
    private String notificationToken;

    /*
     * Do: constructor vacio usuario
     * Param: none
     * Return: none
     * */
    public Users() {
    }

    /*
     * Do: consutructor lleno usuario
     * Param: String emailUser, String pass, String name, String id, int age, String newPass, String province, String photo
     * Return: none
     * */
    public Users(String emailUser, String pass, String name, String id, int age, String newPass, String province, String photo, String notificationToken) {
        this.emailUser = emailUser;
        this.pass = pass;
        this.name = name;
        this.id = id;
        this.age = age;
        this.newPass = newPass;
        this.province = province;
        this.notificationToken = notificationToken;
        this.photo = photo;
    }


    /*
     * Do: Get para la variable respectiva
     * Param: none
     * Return: atributo respectivo
     * */
    public String getEmailUser() {
        return emailUser;
    }

    /*
     * Do: Set para la variable respectiva
     * Param: Valor para la variable respectiva
     * Return: none
     * */
    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    /*
     * Do: Get para la variable respectiva
     * Param: none
     * Return: atributo respectivo
     * */
    public String getPass() {
        return pass;
    }

    /*
     * Do: Set para la variable respectiva
     * Param: Valor para la variable respectiva
     * Return: none
     * */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /*
     * Do: Get para la variable respectiva
     * Param: none
     * Return: atributo respectivo
     * */
    public String getName() {
        return name;
    }

    /*
     * Do: Set para la variable respectiva
     * Param: Valor para la variable respectiva
     * Return: none
     * */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * Do: Get para la variable respectiva
     * Param: none
     * Return: atributo respectivo
     * */
    public String getId() {
        return id;
    }

    /*
     * Do: Set para la variable respectiva
     * Param: Valor para la variable respectiva
     * Return: none
     * */
    public void setId(String id) {
        this.id = id;
    }

    /*
     * Do: Get para la variable respectiva
     * Param: none
     * Return: atributo respectivo
     * */
    public int getAge() {
        return age;
    }

    /*
     * Do: Set para la variable respectiva
     * Param: Valor para la variable respectiva
     * Return: none
     * */
    public void setAge(int age) {
        this.age = age;
    }

    /*
     * Do: Get para la variable respectiva
     * Param: none
     * Return: atributo respectivo
     * */
    public String getNewPass() {
        return newPass;
    }

    /*
     * Do: Set para la variable respectiva
     * Param: Valor para la variable respectiva
     * Return: none
     * */
    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    /*
     * Do: Get para la variable respectiva
     * Param: none
     * Return: atributo respectivo
     * */
    public String getProvince() {
        return province;
    }

    /*
     * Do: Set para la variable respectiva
     * Param: Valor para la variable respectiva
     * Return: none
     * */
    public void setProvince(String province) {
        this.province = province;
    }

    /*
     * Do: Get para la variable respectiva
     * Param: none
     * Return: atributo respectivo
     * */
    public String getPhoto() {
        return photo;
    }

    /*
     * Do: Set para la variable respectiva
     * Param: Valor para la variable respectiva
     * Return: none
     * */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /*
     * Do: Get para la variable respectiva
     * Param: none
     * Return: atributo respectivo
     * */
    public String getNotificationToken() {
        return notificationToken;
    }
}
