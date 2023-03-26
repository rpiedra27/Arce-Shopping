package ucr.example.arce.ControllersTests;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import ucr.example.arce.controllers.ClosestLocation;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ClosestLocationTests {

    /*
     * Do: Este metodo verifica que la respuesta del metodo getProvinciaMasCercana
     * coincida con datos reales de maps
     * Param: None
     * Return: None
     * */

    @Test
    public void checkGetLocation() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ClosestLocation location = new ClosestLocation();
        // Esta latitud y longitud fueron sacados de maps
        String answer = location.getProvinciaMasCercana(9.868032,-83.923565);
        String expectedAnswer = "Cartago";
        assertEquals(expectedAnswer, answer);
    }
}
