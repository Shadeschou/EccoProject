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

    private int idNo;
    private int yPlacement;
    private String role;
    private double balance;
    private double ID_xOffset = 0;
    private double ID_yOffset = 0;
    private Stage stage = new Stage();
    private Parent ID_FXML;
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

    public int getIdNo() {
        return idNo;
    }

    public void setIdNo(int idNo) {
        this.idNo = idNo;
    }

    public int getyPlacement() {
        return yPlacement;
    }

    public void setyPlacement(int yPlacement) {
        this.yPlacement = yPlacement;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getID_xOffset() {
        return ID_xOffset;
    }

    public void setID_xOffset(double ID_xOffset) {
        this.ID_xOffset = ID_xOffset;
    }

    public double getID_yOffset() {
        return ID_yOffset;
    }

    public void setID_yOffset(double ID_yOffset) {
        this.ID_yOffset = ID_yOffset;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Parent getID_FXML() {
        return ID_FXML;
    }

    public void setID_FXML(Parent ID_FXML) {
        this.ID_FXML = ID_FXML;
    }

    public Scene getID_SCENE() {
        return ID_SCENE;
    }

    public void setID_SCENE(Scene ID_SCENE) {
        this.ID_SCENE = ID_SCENE;
    }
}
