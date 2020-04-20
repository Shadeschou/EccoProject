package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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
    private int yPlacement;
    private double ID_xOffset = 0;
    private double ID_yOffset = 0;
    public Stage Stage = new Stage();


    public IDCard(String fxmlName, int idNo, BorderPane loginBorderPane) throws IOException {
        this.idNo = idNo;
        Parent ID_FXML = FXMLLoader.load(getClass().getResource(fxmlName));
        Scene ID_SCENE = new Scene(ID_FXML, 400, 350);
        ID_SCENE.setFill(Color.TRANSPARENT);
        for (IDCard id : Controller.idCardArrayList) {
            yPlacement += 210;
        }
        Stage.setY(-80 + yPlacement);
        Stage.setX(0);
        Stage.initStyle(StageStyle.TRANSPARENT);
        Stage.setScene(ID_SCENE);
        Stage.show();

        ID_FXML.setOnMousePressed(event -> {

            ID_xOffset = event.getSceneX();
            ID_yOffset = event.getSceneY();
        });
        ID_FXML.setOnMouseDragged(event -> {
            Stage.setX(event.getScreenX() - ID_xOffset);
            Stage.setY(event.getScreenY() - ID_yOffset);
            if (Stage.getY() > Controller.minY && Stage.getY() < Controller.maxY && Stage.getX() > Controller.minX && Stage.getX() < Controller.maxX) {
                yPlacement = 0;
                for (IDCard id : Controller.idCardArrayList) {
                    yPlacement += 210;
                    for (IDCard ids : Controller.idCardArrayList) {
                        ids.Stage.close();
                        id.Stage.setY(-280 + yPlacement);
                        id.Stage.setX(0);
                    }
                }
                Controller.activeIdNo = this.idNo;
                loginBorderPane.toBack();
            }
        });
    }
}
