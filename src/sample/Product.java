package sample;

import javafx.scene.image.ImageView;

public class Product {
    private int productID;
    private int supplierID;
    private int reorderLimit;
    private int stock;
    private double price;
    private String name;
    private String imgPath;

    public Product(int productID, int supplierID, int reorderLimit, int stock, double price, String name, String imgPath) {
        this.productID = productID;
        this.supplierID = supplierID;
        this.reorderLimit = reorderLimit;
        this.stock = stock;
        this.price = price;
        this.name = name;
        this.imgPath = imgPath;
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
