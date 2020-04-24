package sample;

import DB.DB;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class Product {
    private int productID;
    private int supplierID;
    private int reorderLimit;
    private int stock;
    private double price;
    private boolean hasValidImage = true;
    private String name;
    private String imgPath;
    private String supplierName;
    public int yPlacement;
    public double ID_xOffset = 0;
    public double ID_yOffset = 0;
    public Stage stage = new Stage();
    public Scene ID_SCENE;
    /***
     * constructor
     * @param productID
     * @param supplierID
     * @param reorderLimit
     * @param stock
     * @param price
     * @param name
     * @param imgPath
     */
    public Product(int productID, int supplierID, int reorderLimit, int stock, double price, String name, String imgPath) {
        this.productID = productID;
        this.supplierID = supplierID;
        this.reorderLimit = reorderLimit;
        this.stock = stock;
        this.price = price;
        this.name = name;
        this.imgPath = imgPath;
    }

    /**
     * constructor for a product that acts like the ID card, is essentially used as a draggable javafx stage
     * that can be scanned with a custom scan area
     *
     * @param price     - price of product
     * @param name      - name of product
     * @param imgPath   - image path of product
     * @param productID - ID of the product
     */
    Product(double price, String name, String imgPath, int productID) {
        this.price = price;
        this.name = name;
        if(imgPath!=null){
            this.imgPath = imgPath.substring(4);
        }
        this.productID = productID;



        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-image: url(" + this.imgPath + ") !important;" +
                            "-fx-background-size: contain !important;" +
                            "-fx-background-repeat: no-repeat !important;" +
                            "-fx-background-color: transparent !important;" +
                            "-fx-background-position: center !important;");

        ID_SCENE = new Scene(anchorPane, 400, 350);

        ID_SCENE.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(ID_SCENE);

    }


    /***
     * overloaded constructor using only the ProductID to fetch all the data
     * @param productID
     */
    public Product(int productID) {
        DB.selectSQL("SELECT * FROM tblProduct WHERE fldProductID = " + productID + ";");
        this.productID = Integer.parseInt(DB.getData());
        this.price = Double.parseDouble(DB.getData());
        this.name = DB.getData();
        this.stock = Integer.parseInt(DB.getData());
        this.supplierID = Integer.parseInt(DB.getData());
        this.imgPath = DB.getData();
        this.reorderLimit = Integer.parseInt(DB.getData());
        DB.selectSQL("SELECT fldName from tblSupplier WHERE fldSupplierId =" + this.supplierID);
        this.supplierName = DB.getData();
        DB.getData();
    }


    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public int getReorderLimit() {
        return reorderLimit;
    }

    public void setReorderLimit(int reorderLimit) {
        this.reorderLimit = reorderLimit;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    /***
     * stringbuilder that adds the product to DB
     */
    public void addToDB() {
        StringBuilder dbString = new StringBuilder();
        dbString.append("INSERT INTO tblProduct (fldProductId, fldStock, fldName, fldSupplierId, fldReorderLimit, fldPrice,fldImagePath) VALUES (");
        dbString.append(this.productID);
        dbString.append(",");
        dbString.append(this.stock);
        dbString.append(",'");
        dbString.append(this.name);
        dbString.append("',");
        dbString.append(this.supplierID);
        dbString.append(",");
        dbString.append(this.reorderLimit);
        dbString.append(",");
        dbString.append(this.price);
        dbString.append(",'");
        dbString.append(this.imgPath);
        dbString.append("');");
        DB.insertSQL(dbString.toString());
    }

    public String getSupplierName() {
        return supplierName;
    }

    /***
     * updates the amount and adds it to DB
     * @param amount
     */
    public void updateStock(int amount) {
        amount += this.stock;
        DB.updateSQL("UPDATE tblProduct SET fldStock =" + amount + "  WHERE fldProductId = " + this.productID + ";");
    }

    /***
     * deletes product from DB
     */
    public void deleteProduct() {
        DB.pendingData = false;
        DB.deleteSQL("DELETE FROM tblProduct WHERE fldProductID = " + this.productID + ";");
    }

    /***
     * checks if any other products in the DB uses that image
     * @return
     */
    public boolean checkIfImageIsUsed() {
        ArrayList<String> productIDs = new ArrayList<>();
        DB.selectSQL("SELECT fldProductId FROM tblProduct WHERE fldImagePath ='" + this.imgPath + "';");
        while (DB.pendingData) {
            productIDs.add(DB.getData());
        }
        if (productIDs.size() > 1) {
            return true;
        } else {
            return false;
        }
    }

    public void setHasValidImage(boolean hasValidImage) {
        this.hasValidImage = hasValidImage;
    }

    public boolean hasValidImage() {
        return hasValidImage;
    }
}
