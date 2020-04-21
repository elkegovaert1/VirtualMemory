import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;

public class Controller {

    @FXML
    private Button execAll;

    @FXML
    private Button execOne;

    @FXML
    private Button restart;

    @FXML //Execute one instruction and update the view
    void executeOneInstruction(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog();
        //dialog.initOwner(stage); //nodig om bij een gemaximaliseerde toepassing goed te verschijnen
        dialog.setTitle("Toevoegen");
        dialog.setHeaderText("AfdelingJurylid");
        dialog.setContentText("ID");
        dialog.setGraphic(null);
        int id = 0;
        try {
            id = Integer.parseInt(dialog.showAndWait().get());
        } catch (Exception e) {
        }
    }

    @FXML //Execute one instruction and update the view
    void executeAll(ActionEvent event) {}

    @FXML //Execute one instruction and update the view
    void restart(ActionEvent event) {}
}
