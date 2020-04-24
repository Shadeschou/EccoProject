package sample;



import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static javafx.collections.FXCollections.observableArrayList;

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
import java.sql.Types;
import java.util.ArrayList;
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
    @FXML private ListView<Double> productPriceList;
    @FXML private ListView<String> productNameList;
    @FXML private ListView<String> totalList;
    @FXML private ListView<Double> totalPriceList;
    @FXML private ListView<Integer> amountOfEachProduct;
    @FXML private Label confirmText;
    @FXML private AnchorPane addProductPaneID;
    @FXML private TextField nameID;
    @FXML private TextField priceID;
    @FXML private TextField stockID;
    @FXML private TextField supplierID;
    @FXML private TextField orderLimitID;
    @FXML private TextField ProductID;
    @FXML private StackPane managementPane;
    @FXML private AnchorPane addProductPaneID1;
    @FXML private Label nameField;
    @FXML private Label quantityField;
    @FXML private Label supplierField;
    @FXML private Label priceField;
    @FXML private AnchorPane productInfo;
    @FXML private ImageView productPicture;
    @FXML private ComboBox<String> comboBox;
    @FXML private ImageView addProductImage;
    @FXML private HBox inventoryPane;

    private double total = 0;
    private double scannerMinY;
    private double scannerMaxY;
    private double scannerMinX;
    private double scannerMaxX;
    private double itemScannerMinY;
    private double itemScannerMaxY;
    private double itemScannerMinX;
    private double itemScannerMaxX;
    private boolean viewingBasket = false;
    private int activeId;
    private Desktop desktop = Desktop.getDesktop();
    public static ProductPane productPane;
    private Product currentProduct;
    String image = "";
    private final String placeHolderImage = "Resources/Images/noImageIcon.png";
    private Path to;
    private Path from;
    private static ArrayList<IDCard> idCardArrayList = new ArrayList<>();
    private static ArrayList<Product> productsArrayList = new ArrayList<>();
    private static ObservableList<String> productsInShoppingCart = FXCollections.observableArrayList();
    private static ObservableList<Double> prices = FXCollections.observableArrayList();
    private static ObservableList<String> totalStringText = FXCollections.observableArrayList();
    private static ObservableList<Double> totalPrice = FXCollections.observableArrayList();
    private static ObservableList<Integer> quantity = FXCollections.observableArrayList();
    private static ArrayList<String> currentShoppingCart = new ArrayList<>();

    /**
     * Initialize the Class with following methods / fields
     * specific initializing chain for the program to work with the custom Scan functions implemented.
     *
     * @param url            - generic
     * @param resourceBundle - generic
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(this::initializeScanner);
    }

    /**
     * this is the method used in initialize to perform the initializing call chain.
     */
    private void initializeScanner() {

        /*
        will make bounds for the id Scanner and Item Scanner,
         also put a listener on them to update location of scan area to hit
         so you can scan an item or ID even when moving the window around
         */
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

            /*
            here the total price text is added in cart, also change listener for the total price
             */
            totalStringText.add("Total Price");
            totalList.setItems(totalStringText);
            prices.addListener((ListChangeListener<? super Double>) change -> {
                total = 0;
                for (double price : prices) {
                    total += price;
                }
                totalPrice.clear();
                totalPrice.add(0, total);
                totalPriceList.setItems(totalPrice);
            });

            /*
            initialize the ID cards
             */
            initializeIdCards();
        }
        catch (NullPointerException ex) {
                /*
                 we expect to get null pointer as we try to use FXML which hasn't been initialized yet
                 we get around this by using the Platform.run later, and accept that we get null pointers until it works
                 */
        }
    }

    /**
     * initializes id cards, and shows them, then adds listeners to the ID cards.
     */
    private void initializeIdCards() {
        try {
            IDCard idCard1 = new IDCard("frankID.fxml", 2222);
            idCardArrayList.add(idCard1);
            IDCard idCard2 = new IDCard("karstenID.fxml", 1111);
            idCardArrayList.add(idCard2);
            IDCard idCard3 = new IDCard("robertID.fxml", 9865);
            idCardArrayList.add(idCard3);
            IDCard idCard4 = new IDCard("kasperID.fxml", 4951);
            idCardArrayList.add(idCard4);
            IDCard idCard5 = new IDCard("jacobID.fxml", 3546);
            idCardArrayList.add(idCard5);
            showIDs();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        addListenersToID();
    }


    /**
     * this method will get a product from the DB
     * then create a product with a draggable stage that can be scanned
     * ultimately shows product
     */
    private void getProductFromDB() {

        // query the DB for informations and declare variables to be used
        DB.selectSQL("SELECT COUNT(*) FROM tblProduct");
        int noOfRows = Integer.parseInt(DB.getData());
        DB.selectSQL("SELECT fldPrice,fldName,fldImagePath,fldProductId,fldStock FROM tblProduct");
        double price;
        String name;
        String imgPath;
        int productId;
        int amountInStock;

        //for each row (product in db) create a draggable stage that can be scanned as an item
        for (int i = 0; i < noOfRows; i++) {
            price = Integer.parseInt(DB.getData());
            name = DB.getData();
            imgPath = DB.getData();
            productId = Integer.parseInt(DB.getData());
            amountInStock = Integer.parseInt(DB.getData());
            if (amountInStock > 0) {
                createProductWithStage(price, name, imgPath, productId, amountInStock);
            }
        }
        // will display products
        showProducts();
    }

    /**
     * this creates a product with a draggable stage so it can be scanned
     *
     * @param price         - price of product
     * @param name          - name of product
     * @param imgPath       - image path
     * @param productId     - id of product
     * @param amountInStock - amount in stock of the product
     */
    private void createProductWithStage(double price, String name, String imgPath, int productId, int amountInStock) {
        Product product = new Product(price, name, imgPath, productId);
        product.setStock(amountInStock);
        productsArrayList.add(product);
        addListenersToProducts();
    }

    /**
     * add listeners to the product created
     * these listeners will detect collision with a scan area
     */
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
                    addToShoppingCart(product); //if scanned area hit, add to basked
                    showProducts(); //reshow products
                }
            });
        }

    }

    /**
     * adds a product to the shopping cart
     *
     * @param productToAdd - the product you wish to add
     */
    private void addToShoppingCart(Product productToAdd) {
        productToAdd.setStock(
                productToAdd.getStock() - 1); // decrease the stock - this is done later in DB, isn't purchased yet
        String productName = productToAdd.getName();
        double productPrice = productToAdd.getPrice();

        // if shopping cart dont contain item, add it, else increment the price and amount of the product in cart
        if (!currentShoppingCart.contains(productName)) {
            quantity.add(1);
            productsInShoppingCart.add(productName);
            prices.add(productPrice);
            currentShoppingCart.add(productName);
        }
        else {
            for (String product : currentShoppingCart) {
                if (productName.equalsIgnoreCase(product)) {
                    int indexOfProduct = productsInShoppingCart.indexOf(productName);
                    quantity.set(indexOfProduct, quantity.get(indexOfProduct) + 1);
                    prices.set(indexOfProduct, prices.get(indexOfProduct) + productPrice);
                    break;
                }
            }
        }
        amountOfEachProduct.setItems(quantity);
        productNameList.setItems(productsInShoppingCart);
        productPriceList.setItems(prices);
    }


    /**
     *just used to show the checkoutPane, and to view the IDcard used to log in, which can be used to confirm purchase
     */
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

    /**
     * clears a cart if costumer dont want items/want to redo their orders
     */
    public void clearCart() {
        for (Product product : productsArrayList) {
            product.stage.close();
        }
        clearSession();
        getProductFromDB();
    }


    /**
     * adds listeners to ID cards, these will check for collision with scan areas.
     * also, if confirmtext is visible - you are at the confirm purchase site.
     * therefor i use the same listener to store a sale / finish it with transfers to DB
     * if you dont confirm the purchase it will just log off
     */
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
                            showEmployeeOption();
                        }
                        else {
                            showCostumerInitialLogin();
                        }
                    }

                }
            });
        }
    }

    /**
     * a lot of crap happens here essentially
     * 1. receive the costumer ID
     * 2. execute callableStatement to store a receipt and retrieve the receipt ID
     * 3. for each product on the shopping cart add callableStatement to batch
     * 4. update stock of items in the DB
     * 5. execute batch that stores the sale in joint table between user and receipt n-m relationship
     * 6. close connections
     * 7. go back to start and show login screen, ready for next costumer
     * */
    private void storeTheSale() {
        DB.pendingData = false;
        CallableStatement callableStatement;
        Connection con = DB.getConnection();
        int iteratorCount = 0;
        try {
            DB.selectSQL("SELECT fldUserId FROM tblUser WHERE fldIdCardId =" + activeId);
            int costumerId = Integer.parseInt(DB.getData());
            callableStatement = con.prepareCall("{call Ecco.dbo.storeReceipt(?,?,?,?)}");
            callableStatement.setInt(1, costumerId);
            callableStatement.setDouble(2, totalPrice.get(0));
            callableStatement.setDate(3, java.sql.Date.valueOf(java.time.LocalDate.now()));
            callableStatement.registerOutParameter(4, Types.INTEGER);
            callableStatement.execute();
            int receiptId = callableStatement.getInt(4);
            DB.pendingData = false;
            callableStatement = con.prepareCall("{call Ecco.dbo.storeSale(?,?,?)}");
            for (String inCart : productsInShoppingCart) {
                Product product = productsArrayList.get(iteratorCount);
                callableStatement.setInt(1, receiptId);
                callableStatement.setInt(2, product.getProductID());
                int indexOfProduct = productsInShoppingCart.indexOf(inCart);
                int quantityOfProduct = quantity.get(indexOfProduct);
                callableStatement.setInt(3, quantityOfProduct);

                DB.updateSQL(
                        "UPDATE tblProduct SET fldStock = " + product.getStock() + " WHERE fldName ='" + product.getName() + "' AND fldStock>0");
                callableStatement.addBatch();
                iteratorCount++;
            }
            int[] results = callableStatement.executeBatch();
            callableStatement.close();
            con.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        showLoginScreen();

    }

    /**
     * returns to log in screen and resets session + show the ID's you can scan
     */
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

    /**
     * clear the session, often used when going to a new pane
     */
    private void clearSession() {
        confirmText.setVisible(false);
        cancelButton.setVisible(false);
        currentShoppingCart.clear();
        productsArrayList.clear();
        quantity.clear();
        productsInShoppingCart.clear();
        prices.clear();
    }

    /**
     * if employee login chosen shows the employee pane
     */
    private void showEmployeeOption() {
       logInOptionPane.toFront();
    }
    @FXML
    private void showEmployeeLogIn() {
        inventoryPane.toFront();
    }

    /**
     * if costumer login chosen/ a costumer logs in, bring you to the menu
     * and gets the products from DB
     */
    public void showCostumerInitialLogin() {
        initialCostumerPane.toFront();
        itemScanner.toFront();
        getProductFromDB();
    }

    /**
     * if basked shown, sets UI visible, of toggled to hide, sets UI invisible
     */
    public void toggleBasket() {
        if (!viewingBasket) {
            amountOfEachProduct.setVisible(true);
            productNameList.setVisible(true);
            productPriceList.setVisible(true);
            totalList.setVisible(true);
            clearCartButton.setVisible(true);
            totalPriceList.setVisible(true);
            checkoutButton.setVisible(true);
            viewingBasket = true;

        }
        else {
            amountOfEachProduct.setVisible(false);
            productNameList.setVisible(false);
            productPriceList.setVisible(false);
            totalList.setVisible(false);
            totalPriceList.setVisible(false);
            checkoutButton.setVisible(false);
            clearCartButton.setVisible(false);
            viewingBasket = false;
        }
    }

    /**
     * shows products, the y/x positions is not really based on anything else than making it look neat
     */
    private void showProducts() {

        int yPosition = 0;
        int iteration = 0;
        int xPosition = -100;
        for (Product product : productsArrayList) {
            if (product.getStock() > 0) {
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

    /**
     * shows the id's, the y/x positions is not really based on anything else than making it look neat
     */
    private void showIDs() {
        int yPosition = -80;
        for (IDCard id : idCardArrayList) {
            id.stage.setY(yPosition);
            id.stage.setX(0);
            yPosition += 200;
            id.stage.show();
        }
    }



    /***
     * creates a new product object and adds it to the DB
     */
    public void addNewProduct() {
        try {
            if (to != null)
                copyProduct();
            Product product = new Product(Integer.parseInt(ProductID.getText()), convertSupplierName(),
                                          Integer.parseInt(orderLimitID.getText()), Integer.parseInt(stockID.getText()), Double.parseDouble(priceID.getText())
                    , nameID.getText(), image);
            product.addToDB();
        } catch (NullPointerException e) {
            System.out.println("NO infomation to Product to be created");
        }

        createProductPane();
        clearTextfield();
    }

    /***
     * creates a filechooser which selects it's URL path
     */

    public void selectImage() {
        final FileChooser fileChooser = new FileChooser();
        final File selectedFile = fileChooser.showOpenDialog(addProductPaneID.getScene().getWindow());
        if (selectedFile != null) {

            from = Paths.get(selectedFile.toURI());
            to = Paths.get("src/Resources/Images/" + selectedFile.getName());
            addProductImage.setImage(new Image("File:" + from));
        }

    }

    /***
     * creates a product pane, by getting the instance of the product pane
     * and assigns a event to all it's buttons
     */
    public void createProductPane() {
        productPane = ProductPane.getInstance();
        productPane.setStyle("-fx-background-color: white");
        showProductView();

        try {
            managementPane.getChildren().add(productPane);
        } catch (IllegalArgumentException e) {
            System.out.println("Children: duplicate children added: parent = HBox[id=managementPane] ");
        }
        for (ProductButton button : productPane.getListOfbuttons()) {
            button.setOnMouseClicked(e -> {
                showProductInfo(button.getProductId());
            });
        }

        productPane.getAddButton().setOnMouseClicked(e -> {
            showAddProductPane();
        });

    }

    private void showProductInfo(int id) {
        currentProduct = new Product(id);
        productInfo.toFront();
        nameField.setText(currentProduct.getName());
        quantityField.setText(String.valueOf(currentProduct.getStock()));
        supplierField.setText(currentProduct.getSupplierName());
        priceField.setText(String.valueOf(currentProduct.getPrice()));
        productPicture.setImage(new Image("File:" + currentProduct.getImgPath()));

        if (productPicture.getImage().getException() != null) {
            productPicture.setImage(new Image(placeHolderImage));
            currentProduct.setHasValidImage(false);
        }

        addProductPaneID.setVisible(false);
        productPane.setVisible(false);
        productInfo.setVisible(true);
    }

    /***
     * deletes the product from the database, and if there is a local copy of the file it deletes it too as long it's not used by other products
     * @throws IOException
     */
    public void deleteProduct() throws IOException {
        currentProduct.deleteProduct();
        if (!currentProduct.checkIfImageIsUsed() && currentProduct.hasValidImage()) {
            Files.delete(Path.of(currentProduct.getImgPath()));
        }
        createProductPane();
    }


    private void showAddProductPane() {
        addProductImage.setImage(new Image(placeHolderImage));
        ObservableList<String> options = observableArrayList();
        DB.selectSQL("SELECT fldName FROM tblSupplier");
        while (DB.pendingData) {
            options.add(DB.getData());
        }
        options.remove(options.size() - 1);
        comboBox.setItems(options);
        productPane.setVisible(false);
        productInfo.setVisible(false);
        addProductPaneID.setVisible(true);
        addProductPaneID.toFront();
    }

    private void showProductView() {
        productPane.setVisible(true);
        productPane.toFront();
    }

    private int convertSupplierName() {
        int supplierID;
        DB.selectSQL("SELECT fldSupplierId FROM TblSupplier WHERE fldName = '" + comboBox.getValue() + "';");
        supplierID = Integer.parseInt(DB.getData());
        DB.getData();
        return supplierID;
    }

    /***
     * increases the quantity of a product
     */
    public void orderProduct() {
        Popup popup = new Popup();
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Label labelIntro = new Label("Please Insert the amount you wanna order?");
        Label label = new Label("Order Amount:");
        TextField textField = new TextField();
        textField.setStyle("-fx-background-color: linear-Gradient(grey,LightGrey)");
        hBox.getChildren().add(label);
        hBox.getChildren().add(textField);
        vBox.setStyle("-fx-background-color: lightgrey");
        vBox.setAlignment(Pos.CENTER);
        Button button = new Button("Comfirm");
        button.getStyleClass().add("myButton");
        ;
        button.setOnMouseClicked(Event -> {
            currentProduct.updateStock(Integer.parseInt(textField.getText()));
            showProductInfo(currentProduct.getProductID());
            popup.hide();
        });
        vBox.getChildren().add(labelIntro);
        vBox.getChildren().add(hBox);
        vBox.getChildren().add(button);
        popup.getContent().add(vBox);
        popup.show(productInfo.getScene().getWindow());
    }

    /***
     * shows the Statics pane and if there is no pane it  creates a new one
     */
    public void showStatics() {
        if (!managementPane.getChildren().contains(Statistics.getInstance())) {
            managementPane.getChildren().add(Statistics.getInstance());
        }
        Statistics.getInstance().toFront();
    }

    /***
     * shows the Transactioons pane and if there is no pane it  creates a new one
     */
    public void showTransactions() {
        if (!managementPane.getChildren().contains(Transactions.getInstance())) {
            managementPane.getChildren().add(Transactions.getInstance());
        }
        Transactions.getInstance().toFront();
    }

    private void copyProduct() {
        try {
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            image = to.toString();
            to=null;
        } catch (IOException e) {
            System.out.println("Failed to Copy f ile");
        }
    }

    private void clearTextfield() {
        ProductID.clear();
        orderLimitID.clear();
        stockID.clear();
        priceID.clear();
        nameID.clear();
        image = placeHolderImage;
        addProductImage.setImage(new Image(placeHolderImage));
    }

}