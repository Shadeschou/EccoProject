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

public class Controller {


    @FXML
    private AnchorPane addProductPaneID;

    @FXML
    private TextField nameID;

    @FXML
    private TextField priceID;

    @FXML
    private TextField stockID;

    @FXML
    private TextField supplierID;

    @FXML
    private TextField orderLimitID;

    @FXML
    private TextField ProductID;

    @FXML
    private StackPane managementPane;

    @FXML
    private AnchorPane addProductPaneID1;

    @FXML
    private Label nameField;

    @FXML
    private Label quantityField;

    @FXML
    private Label supplierField;

    @FXML
    private Label priceField;

    @FXML
    private AnchorPane productInfo;

    @FXML
    private ImageView productPicture;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private ImageView addProductImage;


    private Desktop desktop = Desktop.getDesktop();
    public static ProductPane productPane;
    private Product currentProduct;
    String image = "";
    private final String placeHolderImage = "Resources/Images/noImageIcon.png";
    private Path to;
    private Path from;

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
            to = Paths.get("C:/Users/cappe/IdeaProjects/EccoProject/src/Resources/Images" + selectedFile.getName());
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

