package ucr.example.arce.controllers;

import android.location.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
    Clase utilizada para obtener el nombre de la provincia en la que se encuentra un usuario.
 */
public class ClosestLocation {

    private double coordenadasCartago[] = new double[]{9.86297793342555, -
            83.9233447908051};
    private double coordenadasSanJose[] = new double[]{9.9328603358678, -
            84.07950526683237};
    private double coordenadasHeredia[] = new double[]{9.998088767045822, -
            84.11981971949604};
    private double coordenadasAlajuela[] = new double[]{10.016491249923705, -
            84.21389019362014};
    private double coordenadasGuanacaste[] = new double[]{10.438566474348564, -
            85.40068664547498};
    private double coordenadasLimon[] = new double[]{10.030615543601439, -
            83.08815687284975};
    private double coordenadasPuntarenas[] = new double[]{9.967174211900028, -
            84.83344028835543};

    /*
     * Do: Calcula la provincia más cercana que se usará como recomendación al usuario
     * Param: latitud y logitud actual del usuario
     * Return: Provincia más cercana
     * */
    public String getProvinciaMasCercana(double currentLatitude, double currentLongitude) {
        String provincia = "";
        float results[] = new float[1];
        //Obtiene la distancia entre un punto y otro
        Map<String, Float> maps = new HashMap<String, Float>();
        android.location.Location.distanceBetween(currentLatitude, currentLongitude,
                coordenadasCartago[0], coordenadasCartago[1], results);
        maps.put("Cartago", results[0]);
        android.location.Location.distanceBetween(currentLatitude, currentLongitude,
                coordenadasSanJose[0], coordenadasSanJose[1], results);
        maps.put("San José", results[0]);
        android.location.Location.distanceBetween(currentLatitude, currentLongitude,
                coordenadasHeredia[0], coordenadasHeredia[1], results);
        maps.put("Heredia", results[0]);
        android.location.Location.distanceBetween(currentLatitude, currentLongitude,
                coordenadasAlajuela[0], coordenadasAlajuela[1], results);
        maps.put("Alajuela", results[0]);
        android.location.Location.distanceBetween(currentLatitude, currentLongitude,
                coordenadasGuanacaste[0], coordenadasGuanacaste[1], results);
        maps.put("Guanacaste", results[0]);
        android.location.Location.distanceBetween(currentLatitude, currentLongitude,
                coordenadasLimon[0], coordenadasLimon[1], results);
        maps.put("Limon", results[0]);
        android.location.Location.distanceBetween(currentLatitude, currentLongitude,
                coordenadasPuntarenas[0], coordenadasPuntarenas[1], results);
        maps.put("Puntarenas", results[0]);
        provincia = Collections.min(maps.entrySet(),Map.Entry.comparingByValue()).getKey();
        return provincia;
    }

}
