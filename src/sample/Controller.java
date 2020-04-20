package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML public BorderPane loginBorderPane;
    static ScanHead activeScanner = new ScanHead();
    static int activeIdNo;
    public static ArrayList<IDCard> idCardArrayList = new ArrayList<IDCard>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    public void generateIdCards() {
        try {
            IDCard idCard1 = new IDCard("frankID.fxml", 2222);
            IDCard idCard2 = new IDCard("karstenID.fxml", 1111);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateIDScanner() {
        Bounds bounds = loginBorderPane.localToScreen(loginBorderPane.getBoundsInLocal());
        double minX = bounds.getMinX();
        double minY = bounds.getMinY();
        double maxX = bounds.getMaxX();
        double maxY = bounds.getMaxY();
        try {
            ScanHead iDScanner = new ScanHead("idReader.fxml", minX, minY, maxX, maxY);
            activeScanner = iDScanner;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
