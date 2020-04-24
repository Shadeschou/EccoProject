package sample;

import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

import java.util.ArrayList;

public class ProductPane extends TilePane {
    private ArrayList<ProductButton> listOfbuttons = new ArrayList<>();
    private ProductButton addButton;
    private int numberOfButtons;
    private static ProductPane instance;
    private final String placeHolderImage = "Resources/Images/noImageIcon.png";

    private ProductPane() {
        this.setStyle("-fx-background-color: white");
        this.setVisible(true);
        ProductButton productButton;
        String name;
        ImageView imageView;
        int id;
        DB.selectSQL("SELECT COUNT(fldProductId) FROM tblProduct");
        this.numberOfButtons = Integer.parseInt(DB.getData());
        DB.selectSQL("SELECT fldName, fldImagePath, fldProductId FROM tblProduct;");

        for (int i = 0; i < numberOfButtons; i++) {

            name = DB.getData();
            imageView = new ImageView("File:" + DB.getData());

            if (imageView.getImage().getException() != null) {
                imageView = new ImageView("Resources/Images/noImageIcon.png");
                System.out.println("WARNING: NO IMG URL");
            }
            id = Integer.parseInt(DB.getData());
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setStyle("");
            productButton = new ProductButton(name, imageView, id);
            productButton.setPrefWidth(150);
            listOfbuttons.add(productButton);
            this.getChildren().add(productButton);
        }

        imageView = new ImageView("Resources/Images/plusIcon.png");
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        productButton = new ProductButton("New Product", imageView, 0);
        productButton.setPrefWidth(150);
        addButton = productButton;
        this.getChildren().add(productButton);
        DB.getData();
    }

    /***
     * Singleton getinstance
     * @return
     */
    public static ProductPane getInstance() {
        if (instance != null) {
            instance.getChildren().clear();
        }
        instance = new ProductPane();

        return instance;
    }

    public ArrayList<ProductButton> getListOfbuttons() {
        return listOfbuttons;
    }

    public ProductButton getAddButton() {
        return addButton;
    }
}
