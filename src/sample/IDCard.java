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

/**
 * IDCard is actually a new stage that is draggable, so you can scan it in the simulator.
 * ive done high coupling between the IDCard class and the controller, because i ended up
 * needing to initialize the ID card within the FXML of controller, and rather than splashing everything in the controller
 * i still maintained some kind of "low" coupling.
 */
class IDCard {

    int idNo;
    int yPlacement;
    String role;
    double balance;
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
        DB.selectSQL("SELECT fldBalance FROM tblIdCard WHERE fldIdCardID ="+this.idNo);
        this.balance = Double.parseDouble(DB.getData());

        ID_SCENE.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(ID_SCENE);
    }
}
