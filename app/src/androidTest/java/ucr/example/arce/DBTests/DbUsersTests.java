package ucr.example.arce.DBTests;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import ucr.example.arce.db.DbUsers;
import ucr.example.arce.entities.Users;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DbUsersTests {

    /*
     * Do: Este metodo verifica se este editando de manera correcta la contraseña de inicio
     * de sesión
     * Param: None
     * Return: None
     * */

    @Test
    public void checkEditPass() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DbUsers dbUsers = new DbUsers(appContext);
        Users user = dbUsers.displayUser();
        String passBeforeEdit = user.getPass();
        if (passBeforeEdit.equals("prueba")) {
            dbUsers.editarUserPassword(user.getEmailUser(),"false", "NewPass");
        } else {
            dbUsers.editarUserPassword(user.getEmailUser(),"false", "prueba");
        }
        user = dbUsers.displayUser();
        String passAfterEdit = user.getPass();
        assertNotEquals(passAfterEdit,passBeforeEdit);
    }

    /*
     * Do: Este metodo verifica se este verificando correctamente el estado del usuario
     * (si es un usuario nuevo o no)
     * de sesión
     * Param: None
     * Return: None
     * */

    @Test
    public void checkUserState() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DbUsers dbUsers = new DbUsers(appContext);
        Users user = dbUsers.displayUser();
        String actualState = dbUsers.getNewState(user.getEmailUser());
        assertEquals("false", actualState);
    }
}
