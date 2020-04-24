package Testing;

import DB.DB;
import org.junit.Test;
import sample.Controller;

import static org.junit.Assert.*;

public class ControllerTest {

    String supplierName = "Bilka";

    @Test
    public void convertSupplierName() {
        System.out.println("Testing if we get the correct supplier ID from supplierName in DB");
        System.out.println();
        int supplierID;
        DB.selectSQL("SELECT fldSupplierId FROM TblSupplier WHERE fldName = '" + supplierName + "';");
        supplierID = Integer.parseInt(DB.getData());
        assertSame(1,Integer.valueOf(supplierID));
        System.out.println();
        System.out.println("SUCCESS");
    }
}