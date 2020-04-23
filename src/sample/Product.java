package sample;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Product {
    private int productID;
    private int supplierID;
    private int reorderLimit;
    private int stock;
    private double price;
    private String name;
    private String imgPath;
    public int yPlacement;
    public double ID_xOffset = 0;
    public double ID_yOffset = 0;
    public Stage stage = new Stage();
    public Scene ID_SCENE;

    public Product(int productID, int supplierID, int reorderLimit, int stock, double price, String name, String imgPath) {
        this.productID = productID;
        this.supplierID = supplierID;
        this.reorderLimit = reorderLimit;
        this.stock = stock;
        this.price = price;
        this.name = name;
        this.imgPath = imgPath;
    }

    public Product(double price, String name, String imgPath, int productID) {
        this.price = price;
        this.name = name;
        this.imgPath = imgPath;
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
}
