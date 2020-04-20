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

public class ScanHead {
    private double width = 400;
    private double height = 350;
    private double xOffset = (width / 2);
    private double yOffset = (height / 2);
    public Stage Stage = new Stage();


    public ScanHead() {
        try {
            Parent ID_FXML = FXMLLoader.load(getClass().getResource("idReader.fxml"));
            Scene ID_SCENE = new Scene(ID_FXML, width, height);
            ID_SCENE.setFill(Color.TRANSPARENT);
            Stage.setY(0);
            Stage.setX(0);
            Stage.initStyle(StageStyle.TRANSPARENT);
            Stage.setScene(ID_SCENE);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ScanHead(String fxmlName, double minX, double minY, double maxX, double maxY) throws IOException {
        Parent ID_FXML = FXMLLoader.load(getClass().getResource(fxmlName));
        Scene ID_SCENE = new Scene(ID_FXML, width, height);
        ID_SCENE.setFill(Color.TRANSPARENT);
        Stage.setY((minY + maxY) / 2 - yOffset);
        Stage.setX((minX + maxX) / 2 - xOffset);
        Stage.initStyle(StageStyle.TRANSPARENT);
        Stage.setScene(ID_SCENE);
        Stage.show();
    }


}
