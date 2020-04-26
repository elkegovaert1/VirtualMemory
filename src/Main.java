//Glenn Groothuis
//ELke Govaert
//Arne Reyniers

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("gui.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Virtual Memory");
        primaryStage.setScene(new Scene(root, 1100, 628));
        primaryStage.show();

        Controller controller = loader.getController();

        //xmlfiles inlezen
        ReadXMLFile readXMLFile = new ReadXMLFile();
        List<Instruction> instructions1 = readXMLFile.leesInstructions("Instructions_30_3.xml");
        List<Instruction> instructions2 = readXMLFile.leesInstructions("Instructions_20000_4.xml");
        List<Instruction> instructions3 = readXMLFile.leesInstructions("Instructions_20000_20.xml");

        controller.initializeList1(instructions1);
        controller.initializeList2(instructions2);
        controller.initializeList3(instructions3);
        controller.initializeTables();
        ;
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        launch(args);
    }

}
