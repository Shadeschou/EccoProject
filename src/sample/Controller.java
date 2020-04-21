package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private StackPane stackPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        statisticsHandler sh = statisticsHandler.getInstance();
        stackPane.getChildren().add(sh);

        DB.selectSQL("SELECT fldName FROM tblProduct");



    }
}
