import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextInputDialog;

import java.util.List;

public class Controller {

    //buttons
    @FXML
    private Button execAll;

    @FXML
    private Button execOne;

    @FXML
    private Button restart;

    //Labels from the "Algemeen" component
    @FXML
    private Label timerLabel;

    @FXML
    public Label aantalWritesLabel;

    @FXML
    public Label aantalReadsLabel;

    //Labels for just performed instruction
    @FXML
    public Label netinstructieLabel;

    @FXML
    public Label netvirtadrLabel;

    @FXML
    public Label netreadrLabel;

    @FXML
    public Label netframeLabel;

    @FXML
    public Label netoffsetlabel;

    //Next instruction label
    @FXML
    public Label volginstructieLabel;

    @FXML
    public Label volgvirtadrLabel;

    @FXML
    public Label volgframeLabel;

    @FXML
    public Label volgoffsetLabel;

    @FXML
    public Label volgreadrLabel;

    //Table RAM
    @FXML
    public TableColumn RAMFrameCol;

    @FXML
    public TableColumn RAMpidCol;

    @FXML
    public TableColumn RAMpagenrCol;

    //Page table of current running process
    @FXML
    public TableColumn PTPageCol;

    @FXML
    public TableColumn PTPBCol;

    @FXML
    public TableColumn PTMBCol;

    @FXML
    public TableColumn PTLATCol;

    @FXML
    public TableColumn PTFrameCol;


    //List 1 of instructions
    List<Instruction> instructions1;

    //List 2 of instructions
    List<Instruction> instructions2;

    //List 3 of instructions
    List<Instruction> instructions3;

    //timer starts at -1
    private int timer = -1;

    //writes to RAM
    private int writesToRAM=0;

    //reads from RAM
    private int readsFromRAM=0;

    //When starting the application all the fill all the Lists
    public void initializeList1(List<Instruction> instructions) {
        instructions1 = instructions;
    }

    public void initializeList2(List<Instruction> instructions) {
        instructions2 = instructions;
    }

    public void initializeList3(List<Instruction> instructions) {
        instructions3 = instructions;
    }


    @FXML //Execute one instruction and update the view
    void executeOneInstruction(ActionEvent event) {

        //Timer + 1
        timer = timer + 1;

        //














        //show timer value
        timerLabel.setText(String.valueOf(timer));

        //show number of writes to RAM
        aantalWritesLabel.setText(String.valueOf(writesToRAM));

        //show number of reads from RAM
        aantalReadsLabel.setText(String.valueOf(readsFromRAM));

    }

    @FXML //Execute one instruction and update the view
    void executeAll(ActionEvent event) {}

    @FXML //Execute one instruction and update the view
    void restart(ActionEvent event) {}

    /*
    public void update(Observable arg0, Object arg1) { // Called from the Model
        tekstAriaAfdelingen.setText(al.toString());
        tekstAriaJuryLeden.setText(jl.toString());
        tekenGrafiek();
    }*/

}
