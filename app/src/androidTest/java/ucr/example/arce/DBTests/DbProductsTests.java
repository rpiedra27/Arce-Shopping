package ucr.example.arce.DBTests;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import ucr.example.arce.db.DbProducts;
import ucr.example.arce.entities.Products;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DbProductsTests {

    /*
     * Do: Este metodo verifica que la respuesta del metodo displayProduct este funcionando
     * correctamente
     * Param: None
     * Return: None
     * */

    @Test
    public void checkDisplayProduct() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DbProducts dbProducts = new DbProducts(appContext);
        //El dispositivo con el id 1 siempre es iPhone 9 ya que los datos se cargan de un
        //API y actualmente no se borran, ni se reasignan ids.
        Products product = dbProducts.displayProduct(1);
        String expectedProductName = "iPhone 9";
        assertEquals(expectedProductName, product.gettitle());
    }

    /*
     * Do: Este metodo verifica que la respuesta del metodo que devuelve el stock de un producto
     * este funcionando correctamente
     * Param: None
     * Return: None
     * */

    @Test
    public void checkProductStock() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DbProducts dbProducts = new DbProducts(appContext);
        int stock = dbProducts.checkStock("iPhone 9");
        Products product = dbProducts.displayProduct(1);
        assertEquals(product.getStock(), stock);
    }

    /*
     * Do: Este metodo verifica se esten decrementado los productos en stock de manera correcta
     * Param: None
     * Return: None
     * */

//    //TODO arreglar metodo decreaseStock no pasa la prueba
//    @Test
//    public void checkDecreaseStock() {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        DbProducts dbProducts = new DbProducts(appContext);
//        int stockBeforeDecrese = dbProducts.checkStock("iPhone 9");
//        dbProducts.decreaseStock("iPhone 9");
//        int stockAfterDecrese = dbProducts.checkStock("iPhone 9");
//        assertEquals(stockAfterDecrese , stockBeforeDecrese - 1);
//    }
}
