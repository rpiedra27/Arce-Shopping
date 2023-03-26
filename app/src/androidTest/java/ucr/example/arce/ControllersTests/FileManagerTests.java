package ucr.example.arce.ControllersTests;


import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import ucr.example.arce.controllers.FileManager;

import java.io.IOException;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FileManagerTests {

    /*
     * Do: Este metodo verifica que el metodo que guarda un JSON en un archivo del sistema
     * funcione correctamente
     * Param: None
     * Return: None
     * */

    @Test
    public void checkWriteFile() throws IOException, JSONException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FileManager fileManager = new FileManager();

        JSONObject jso = new JSONObject();

        jso.put("Id", "123");
        jso.put("Nombre", "carlos");
        jso.put("Email", "carlos@gmail.com");
        jso.put("Age", "23");
        jso.put("New", "true");
        jso.put("Province", "San Jose");
        jso.put("Password", "admin123");

        fileManager.writeData(appContext,jso);

        JSONObject answer = fileManager.readData(appContext);
        assertEquals(jso.getString("Nombre"), answer.getString("Nombre"));
    }
}
