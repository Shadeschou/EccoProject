package sample;

import DB.DB;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML private BorderPane loginBorderPane;
    @FXML private Button idScanner;
    @FXML private Button itemScanner;
    @FXML private Button shoppingCart;
    @FXML private Button checkoutButton;
    @FXML private AnchorPane logInOptionPane;
    @FXML private AnchorPane initialCostumerPane;
    @FXML private ListView<String> itemPriceList;
    @FXML private ListView<String> itemNameList;
    @FXML private ListView<String> totalList;
    @FXML private ListView<String> totalPriceList;

    private int total = 0;
    private int activeIdNo = 0;
    private double scannerMinY;
    private double scannerMaxY;
    private double scannerMinX;
    private double scannerMaxX;
    private double itemScannerMinY;
    private double itemScannerMaxY;
    private double itemScannerMinX;
    private double itemScannerMaxX;
    private IDCard idCard1;
    private IDCard idCard2;
    private IDCard idCard3;
    private IDCard idCard4;
    private IDCard idCard5;
    private boolean viewingBasket = false;


    static ArrayList<IDCard> idCardArrayList = new ArrayList<IDCard>();
    static ArrayList<Product> productsArrayList = new ArrayList<Product>();
    static ObservableList<String> products = FXCollections.observableArrayList();
    static ObservableList<String> prices = FXCollections.observableArrayList();
    static ObservableList<String> totalString = FXCollections.observableArrayList();
    static ObservableList<String> totalPrice = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            initializeScanner(); // calls other initialize methods, specific call chain for it to work.
        });
    }

    private void initializeScanner() {
        try {
            Bounds bounds = idScanner.localToScreen(idScanner.getBoundsInLocal());
            scannerMinX = bounds.getMinX();
            scannerMinY = bounds.getMinY();
            scannerMaxX = bounds.getMaxX();
            scannerMaxY = bounds.getMaxY();

            loginBorderPane.getScene().getWindow().xProperty().addListener((observableValue, number, t1) -> {
                Bounds bounds1 = idScanner.localToScreen(idScanner.getBoundsInLocal());
                scannerMinX = bounds1.getMinX();
                scannerMinY = bounds1.getMinY();
                scannerMaxX = bounds1.getMaxX();
                scannerMaxY = bounds1.getMaxY();
            });
            Bounds boundsItemScanner = itemScanner.localToScreen(itemScanner.getBoundsInLocal());
            itemScannerMinX = boundsItemScanner.getMinX();
            itemScannerMinY = boundsItemScanner.getMinY();
            itemScannerMaxX = boundsItemScanner.getMaxX();
            itemScannerMaxY = boundsItemScanner.getMaxY();

            loginBorderPane.getScene().getWindow().xProperty().addListener((observableValue, number, t1) -> {
                Bounds bounds1 = itemScanner.localToScreen(itemScanner.getBoundsInLocal());
                itemScannerMinX = bounds1.getMinX();
                itemScannerMinY = bounds1.getMinY();
                itemScannerMaxX = bounds1.getMaxX();
                itemScannerMaxY = bounds1.getMaxY();
            });
            totalString.add("Total Price");
            totalList.setItems(totalString);
            prices.addListener((ListChangeListener<? super String>) change -> {
                total = 0;
                for (String price : prices) {
                    total += Integer.parseInt(price);
                }
                totalPrice.clear();
                totalPrice.add(0, String.valueOf(total));
                totalPriceList.setItems(totalPrice);
            });

            initializeIdCards();
        }
        catch (NullPointerException ex) {
                /*
                 we expect to get nullpointer as we try to use FXML which hasnt been initialized yet
                 we get around this by using the Platform.runlater, and accept that we get nullpointers until it works
                 */
        }
    }

    private void getProducts() {
        DB.selectSQL("SELECT fldPrice,fldName,fldImagePath,fldProductId FROM tblProduct");
        int noOfColumns = DB.getNumberOfColumns();
        DB.selectSQL("SELECT COUNT(*) FROM tblProduct");
        int noOfRows = Integer.valueOf(DB.getData());
        DB.selectSQL("SELECT fldPrice,fldName,fldImagePath,fldProductId,fldStock FROM tblProduct");
        double price = 0;
        String name = null;
        String imgPath = null;
        int productId = 0;
        int amountInStock = 0;
        for (int i = 0; i < noOfRows; i++) {
            price = Integer.valueOf(DB.getData());
            name = DB.getData();
            imgPath = DB.getData();
            productId = Integer.valueOf(DB.getData());
            amountInStock = Integer.valueOf(DB.getData());

            if (amountInStock != 0) {
                Product product = new Product(price, name, imgPath, productId);
                product.setStock(amountInStock);
                productsArrayList.add(product);
            }

        }
        addListenersToProducts();
    }

    private void addListenersToProducts() {
        for (Product product : productsArrayList) {

            product.ID_SCENE.setOnMousePressed(event -> {
                product.ID_xOffset = event.getSceneX();
                product.ID_yOffset = event.getSceneY();
            });
            product.ID_SCENE.setOnMouseDragged(event -> {
                product.stage.setX(event.getScreenX() - product.ID_xOffset);
                product.stage.setY(event.getScreenY() - product.ID_yOffset);
                if (product.stage.getY() > itemScannerMinY && product.stage.getY() < itemScannerMaxY && product.stage.getX() > itemScannerMinX && product.stage.getX() < itemScannerMaxX) {
                    product.yPlacement = 0;
                    DB.pendingData = false;
                    DB.updateSQL(
                            "UPDATE tblProduct SET fldStock = fldStock-1 WHERE fldName ='" + product.getName() + "' AND fldStock>0");
                    for (Product product1 : productsArrayList) {
                        product1.stage.close();
                    }
                    addToBasket(product.getProductID());
                    productsArrayList.clear();
                    getProducts();
                }
            });
        }
        showProducts();
    }

    public void addToBasket(int productID) {
        DB.selectSQL("SELECT fldName,fldPrice FROM tblProduct WHERE fldProductId =" + productID);
        products.add(DB.getData());
        prices.add(DB.getData());
        itemNameList.setItems(products);
        itemPriceList.setItems(prices);
    }

    public void checkout() {
        for (String product : products) {
            DB.pendingData = false;
            DB.updateSQL("UPDATE tblProduct SET fldStock = fldStock-1 WHERE fldName ='" + product + "' AND fldStock>0");
        }
        prices.clear();
        products.clear();
        showLoginScreen();
    }

    private void initializeIdCards() {
        try {
            idCard1 = new IDCard("frankID.fxml", 2222);
            idCardArrayList.add(idCard1);
            idCard2 = new IDCard("karstenID.fxml", 1111);
            idCardArrayList.add(idCard2);
            idCard3 = new IDCard("robertID.fxml", 9865);
            idCardArrayList.add(idCard3);
            idCard4 = new IDCard("kasperID.fxml", 4951);
            idCardArrayList.add(idCard4);
            idCard5 = new IDCard("jacobID.fxml", 3546);
            idCardArrayList.add(idCard5);
            showIDs();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        addListenersToID();
    }

    private void addListenersToID() {
        for (IDCard id : idCardArrayList) {

            id.ID_FXML.setOnMousePressed(event -> {
                id.ID_xOffset = event.getSceneX();
                id.ID_yOffset = event.getSceneY();
            });
            id.ID_FXML.setOnMouseDragged(event -> {
                id.stage.setX(event.getScreenX() - id.ID_xOffset);
                id.stage.setY(event.getScreenY() - id.ID_yOffset);
                if (id.stage.getY() > scannerMinY && id.stage.getY() < scannerMaxY && id.stage.getX() > scannerMinX && id.stage.getX() < scannerMaxX) {
                    id.yPlacement = 0;
                    for (IDCard id2 : idCardArrayList) {
                        id2.yPlacement += 210;
                        for (IDCard ids : idCardArrayList) {
                            ids.stage.close();

                            ids.stage.setY(-280 + ids.yPlacement);
                            ids.stage.setX(0);
                        }
                    }
                    activeIdNo = id.idNo;
                    if (id.role.equalsIgnoreCase("Employee")) {
                        showEmployeeLogIn();
                    }
                    else {
                        showCostumerInitialLogin();
                    }
                }
            });
        }
    }

    public void showLoginScreen() {
        activeIdNo = 0;
        loginBorderPane.toFront();
        if (viewingBasket) {
            toggleBasket();
        }
        for (Product product : productsArrayList) {
            product.stage.close();
        }
        productsArrayList.clear();
        products.clear();
        prices.clear();
        showIDs();
    }

    private void showEmployeeLogIn() {
        logInOptionPane.toFront();
    }

    public void showCostumerInitialLogin() {
        initialCostumerPane.toFront();
        getProducts();
    }

    public void toggleBasket() {
        if (!viewingBasket) {
            itemNameList.setVisible(true);
            itemPriceList.setVisible(true);
            totalList.setVisible(true);
            totalPriceList.setVisible(true);
            checkoutButton.setVisible(true);
            viewingBasket = true;
        }
        else {
            itemNameList.setVisible(false);
            itemPriceList.setVisible(false);
            totalList.setVisible(false);
            totalPriceList.setVisible(false);
            checkoutButton.setVisible(false);
            viewingBasket = false;
        }
    }

    private void showProducts() {
        int yPosition = 0;
        int iteration = 0;
        int xPosition = -100;
        for (Product product : productsArrayList) {

            switch (iteration) {
                case 3:
                case 6:
                case 9:
                case 12:
                    yPosition += 200;
                    xPosition = 0;
                    break;
                default:
                    xPosition += 100;
            }
            product.stage.setY(yPosition);
            product.stage.setX(xPosition);
            iteration++;

            product.stage.show();
        }
        iteration = 0;
        xPosition = 0;
        yPosition = 0;
    }

    private void showIDs() {
        int yPosition = -80;
        for (IDCard id : idCardArrayList) {
            id.stage.setY(yPosition);
            id.stage.setX(0);
            yPosition += 200;
            id.stage.show();
        }
    }


}
