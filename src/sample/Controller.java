package sample;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.BorderPane;
import org.w3c.dom.ls.LSOutput;


import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML public BorderPane loginBorderPane;
    @FXML public Button idScanner;
    static int activeIdNo = 0;
    static double minY;
    static double maxY;
    static double maxX;
    static double minX;

    static ArrayList<IDCard> idCardArrayList = new ArrayList<IDCard>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            try {
                Bounds bounds = idScanner.localToScreen(idScanner.getBoundsInLocal());
                minX = bounds.getMinX();
                minY = bounds.getMinY();
                maxX = bounds.getMaxX();
                maxY = bounds.getMaxY();

                loginBorderPane.getScene().getWindow().xProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                        Bounds bounds = idScanner.localToScreen(idScanner.getBoundsInLocal());
                        minX = bounds.getMinX();
                        minY = bounds.getMinY();
                        maxX = bounds.getMaxX();
                        maxY = bounds.getMaxY();
                        System.out.println(minX);
                    }
                });


                IDCard idCard1 = new IDCard("frankID.fxml", 2222, loginBorderPane);
                Controller.idCardArrayList.add(idCard1);
                IDCard idCard2 = new IDCard("karstenID.fxml", 1111, loginBorderPane);
                Controller.idCardArrayList.add(idCard2);
                IDCard idCard3 = new IDCard("robertID.fxml", 9865, loginBorderPane);
                Controller.idCardArrayList.add(idCard3);
                IDCard idCard4 = new IDCard("kasperID.fxml", 4951, loginBorderPane);
                Controller.idCardArrayList.add(idCard4);
                IDCard idCard5 = new IDCard("jacobID.fxml", 3546, loginBorderPane);
                Controller.idCardArrayList.add(idCard5);

            }
            catch (NullPointerException | IOException ex) {
                /*
                 we expect to get nullpointer as we try to use FXML which hasnt been initialized yet
                 we get around this by using the Platform.runlater, and accept that we get nullpointers until it works
                 */
            }
        });
    }

    public void showLoginScreen() {
        loginBorderPane.toFront();
        showIDs();
    }

    public void showIDs() {
        for (IDCard id : idCardArrayList) {
            id.Stage.show();
        }
    }

}
