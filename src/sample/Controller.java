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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML private BorderPane loginBorderPane;
    @FXML private Button idScanner;
    @FXML private Button itemScanner;
    @FXML private Button checkoutButton;
    @FXML private Button clearCartButton;
    @FXML private Button cancelButton;
    @FXML private AnchorPane logInOptionPane;
    @FXML private AnchorPane initialCostumerPane;
    @FXML private ListView<Double> itemPriceList;
    @FXML private ListView<String> itemNameList;
    @FXML private ListView<String> totalList;
    @FXML private ListView<String> totalPriceList;
    @FXML private ListView<Integer> itemAmount;

    @FXML private Label confirmText;

    private int total = 0;
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
    private int activeId;


    static ArrayList<IDCard> idCardArrayList = new ArrayList<IDCard>();
    static ArrayList<Product> productsArrayList = new ArrayList<Product>();
    static ObservableList<String> products = FXCollections.observableArrayList();
    static ObservableList<Double> prices = FXCollections.observableArrayList();
    static ObservableList<String> totalString = FXCollections.observableArrayList();
    static ObservableList<String> totalPrice = FXCollections.observableArrayList();
    static ObservableList<Integer> amounts = FXCollections.observableArrayList();
    static ArrayList<String> currentBasket = new ArrayList<String>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // calls other initialize methods, specific call chain for it to work.
        Platform.runLater(this::initializeScanner);
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
            prices.addListener((ListChangeListener<? super Double>) change -> {
                total = 0;
                for (double price : prices) {
                    total += price;
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

    private void getProductFromDB() {

        DB.selectSQL("SELECT COUNT(*) FROM tblProduct");
        int noOfRows = Integer.parseInt(DB.getData());
        DB.selectSQL("SELECT fldPrice,fldName,fldImagePath,fldProductId,fldStock FROM tblProduct");
        double price;
        String name;
        String imgPath;
        int productId;
        int amountInStock;

        for (int i = 0; i < noOfRows; i++) {
            price = Integer.parseInt(DB.getData());
            name = DB.getData();
            imgPath = DB.getData();
            productId = Integer.parseInt(DB.getData());
            amountInStock = Integer.parseInt(DB.getData());
            if (amountInStock > 0) {
                createScannableProduct(price, name, imgPath, productId, amountInStock);
            }
        }
        showProducts();
    }

    public void createScannableProduct(double price, String name, String imgPath, int productId, int amountInStock) {
        Product product = new Product(price, name, imgPath, productId);
        product.setStock(amountInStock);
        productsArrayList.add(product);
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
                    for (Product product1 : productsArrayList) {
                        product1.stage.close();
                    }
                    addToBasket(product);
                    showProducts();
                }
            });
        }

    }

    private void addToBasket(Product productToAdd) {
        productToAdd.setStock(productToAdd.getStock() - 1);
        String productName = productToAdd.getName();
        double productPrice = productToAdd.getPrice();

        if (!currentBasket.contains(productName)) {
            amounts.add(1);
            products.add(productName);
            prices.add(productPrice);
            currentBasket.add(productName);
        }
        else {
            for (String product : currentBasket) {
                if (productName.equalsIgnoreCase(product)) {
                    int indexOfProduct = products.indexOf(productName);
                    amounts.set(indexOfProduct, amounts.get(indexOfProduct) + 1);
                    prices.set(indexOfProduct, prices.get(indexOfProduct) + productPrice);
                    break;
                }
            }
        }
        itemAmount.setItems(amounts);
        itemNameList.setItems(products);
        itemPriceList.setItems(prices);
    }

    public void showCheckoutPane() {

        productsArrayList.forEach(product -> product.stage.close());
        loginBorderPane.toFront();
        confirmText.setVisible(true);
        cancelButton.setVisible(true);
        for (IDCard idcard : idCardArrayList) {
            if (idcard.idNo == activeId) {
                idcard.stage.setX(0);
                idcard.stage.setY(0);
                idcard.stage.show();
            }
        }


    }

    public void clearCart() {


        for (Product product : productsArrayList) {
            product.stage.close();
        }
        clearSession();
        getProductFromDB();


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
                    if (confirmText.isVisible()) {
                        storeTheSale();
                    }
                    else {
                        activeId = id.idNo;
                        if (id.role.equalsIgnoreCase("Employee")) {
                            showEmployeeLogIn();
                        }
                        else {
                            showCostumerInitialLogin();
                        }
                    }

                }
            });
        }
    }

    private void storeTheSale() {
        DB.pendingData = false;
        CallableStatement cstmt;
        Connection con = DB.getConnection();
        int iteratorCount = 0;
        try {
            cstmt = con.prepareCall("{call Ecco.dbo.storeSale(?,?,?,?,?)}");
            for (String pr : products) {
                Product product = productsArrayList.get(iteratorCount);
                cstmt.setString(1, String.valueOf(activeId));
                cstmt.setString(2, totalPrice.get(0));
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                cstmt.setString(3, dateFormat.format(date));
                cstmt.setString(4, String.valueOf(product.getProductID()));
                int indexOfProduct = products.indexOf(pr);
                int quantityOfProduct = amounts.get(indexOfProduct);
                cstmt.setString(5, String.valueOf(quantityOfProduct));

                boolean results = cstmt.execute();

                DB.updateSQL(
                        "UPDATE tblProduct SET fldStock = " + product.getStock() + " WHERE fldName ='" + product.getName() + "' AND fldStock>0");


            }
            cstmt.close();
            con.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        showLoginScreen();

    }

    public void showLoginScreen() {
        loginBorderPane.toFront();
        if (viewingBasket) {
            toggleBasket();
        }
        for (Product product : productsArrayList) {
            product.stage.close();
        }
        clearSession();
        showIDs();
    }

    private void clearSession() {
        confirmText.setVisible(false);
        cancelButton.setVisible(false);
        currentBasket.clear();
        productsArrayList.clear();
        amounts.clear();
        products.clear();
        prices.clear();
    }

    private void showEmployeeLogIn() {
        logInOptionPane.toFront();
    }

    public void showCostumerInitialLogin() {
        initialCostumerPane.toFront();
        itemScanner.toFront();
        getProductFromDB();
    }

    public void toggleBasket() {
        if (!viewingBasket) {
            itemAmount.setVisible(true);
            itemNameList.setVisible(true);
            itemPriceList.setVisible(true);
            totalList.setVisible(true);
            clearCartButton.setVisible(true);
            totalPriceList.setVisible(true);
            checkoutButton.setVisible(true);
            viewingBasket = true;

        }
        else {
            itemAmount.setVisible(false);
            itemNameList.setVisible(false);
            itemPriceList.setVisible(false);
            totalList.setVisible(false);
            totalPriceList.setVisible(false);
            checkoutButton.setVisible(false);
            clearCartButton.setVisible(false);
            viewingBasket = false;
        }
    }

    private void showProducts() {

        int yPosition = 0;
        int iteration = 0;
        int xPosition = -100;
        for (Product product : productsArrayList) {
            if (product.getStock() == 0) {
                // do nothing
            }
            else {
                product.stage.close();
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
        }
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
