package sample;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class ProductButton extends Button {
    private int id;

    public ProductButton(String text, Node graphic, int id) {
        super(text, graphic);
        this.id = id;
        this.getStyleClass().add("ProductButton");
    }

    public int getProductId() {
        return id;
    }
}

