package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * @Author Robert Skaar
 * @Project EccoProject  -  https://github.com/robskaar
 * @Date 20-04-2020
 **/

public class IDCard {

    private int idNo;
    private double ID_xOffset = 0;
    private double ID_yOffset = 0;
    private double activeScannerMinY;
    private double activeScannerMaxY;
    private double activeScannerMinX;
    private double activeScannerMaxX;
    private Stage Stage = new Stage();


    public IDCard(String fxmlName, int idNo) throws IOException {
        this.idNo = idNo;
        Parent ID_FXML = FXMLLoader.load(getClass().getResource(fxmlName));
        Scene ID_SCENE = new Scene(ID_FXML, 400, 350);
        ID_SCENE.setFill(Color.TRANSPARENT);
        Stage.setY(0);
        Stage.setX(0);
        Stage.initStyle(StageStyle.TRANSPARENT);
        Stage.setScene(ID_SCENE);
        Stage.show();

        ID_FXML.setOnMousePressed(event -> {
            activeScannerMinY = Controller.activeScanner.Stage.getY();
            activeScannerMaxY = Controller.activeScanner.Stage.getY() + Controller.activeScanner.Stage.getHeight();
            activeScannerMinX = Controller.activeScanner.Stage.getX();
            activeScannerMaxX = Controller.activeScanner.Stage.getX() + Controller.activeScanner.Stage.getWidth();
            ID_xOffset = event.getSceneX();
            ID_yOffset = event.getSceneY();
        });
        ID_FXML.setOnMouseDragged(event -> {
            Stage.setX(event.getScreenX() - ID_xOffset);
            Stage.setY(event.getScreenY() - ID_yOffset);
            if (Stage.getY() > activeScannerMinY && Stage.getY() < activeScannerMaxY && Stage.getX() > activeScannerMinX && Stage.getX() < activeScannerMaxX) {
                for (IDCard id : Controller.idCardArrayList) {
                    id.Stage.close();
                }
                Controller.activeScanner.Stage.close();
                Controller.activeIdNo = this.idNo;
            }
        });
    }


}
