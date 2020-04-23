package sample;

import DB.DB;
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

class IDCard {

    public int idNo;
    int yPlacement;
    String role;
    double ID_xOffset = 0;
    double ID_yOffset = 0;
    Stage stage = new Stage();
    Parent ID_FXML;
    private Scene ID_SCENE;

    IDCard(String fxmlName, int idNo) throws IOException {
        this.idNo = idNo;
        DB.selectSQL(
                "SELECT fldRoleName FROM tblRole WHERE fldRoleId = (SELECT fldRoleId FROM tblIdCard WHERE fldIdCardID =" + this.idNo + ")");
        this.role = DB.getData();
        this.ID_FXML = FXMLLoader.load(getClass().getResource(fxmlName));
        this.ID_SCENE = new Scene(ID_FXML, 400, 350);

        ID_SCENE.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(ID_SCENE);
    }
}
