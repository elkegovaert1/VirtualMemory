import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public TableView<Page> RAMTable;

    @FXML
    public TableColumn RAMFrameCol;

    @FXML
    public TableColumn RAMpidCol;

    @FXML
    public TableColumn RAMpagenrCol;

    //Page table of current running process
    @FXML
    public TableView<TableEntry> pageTable;

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

    //RAM
    RAM ram = new RAM(0);

    //address of next instruction to be selected out of virtual memory
    //Start at instruction 0;
    private int virtualAddressNextInstruction = 0;

    //When starting the application all the fill all the Lists
    public void initializeList1(List<Instruction> instructions) {
        instructions1 = instructions;

        // Set up the RAM table
        RAMFrameCol.setCellValueFactory(
                new PropertyValueFactory<Page,Integer>("frameNumber")
        );
        RAMpagenrCol.setCellValueFactory(
                new PropertyValueFactory<Page,Integer>("pageNumber")
        );
        RAMpidCol.setCellValueFactory(
                new PropertyValueFactory<Page,Integer>("processId")
        );

        // Set up the PageTable Of a process
        PTFrameCol.setCellValueFactory(
                new PropertyValueFactory<Page,Integer>("frameNumber")
        );
        PTLATCol.setCellValueFactory(
                new PropertyValueFactory<Page,String>("lastAccessTime")
        );
        PTMBCol.setCellValueFactory(
                new PropertyValueFactory<Page,Boolean>("modifyBit")
        );
        PTPageCol.setCellValueFactory(
                new PropertyValueFactory<Page,Integer>("pageNumber")
        );
        PTPBCol.setCellValueFactory(
                new PropertyValueFactory<Page,Boolean>("presentBit")
        );
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

        //execute next instruction
        int virtualAddressInstruction = virtualAddressNextInstruction;
        Instruction instruction = instructions1.get(virtualAddressInstruction);

        String operation = instruction.getOperation();
        int processID = instruction.getProcessID();
        int virtualAddress = instruction.getAdress();

        //Different operations are possible
        if(operation.equals("Start")){
            startProcess(processID);
        }
        else if(operation.equals("Read")){

        }
        else if(operation.equals("Write")){

        }
        else if(operation.equals("finished")){

        }


        //show timer value
        timerLabel.setText(String.valueOf(timer));

        //show number of writes to RAM
        aantalWritesLabel.setText(String.valueOf(writesToRAM));

        //show number of reads from RAM
        aantalReadsLabel.setText(String.valueOf(readsFromRAM));

        //show labels current instruction
        netinstructieLabel.setText(operation);
        netvirtadrLabel.setText(String.valueOf(virtualAddressInstruction));

        //show RAM table
        ObservableList<Page> ramTable = FXCollections.observableArrayList();
        Page[] tablePage = ram.getFrameArray();
        for(int i=0;i<tablePage.length;i++){
            System.out.println(tablePage[i]);
        }
        System.out.println(tablePage);
        ramTable.addAll(tablePage);
        RAMTable.setItems(ramTable);

        //show Page Table of current running process
        ObservableList<TableEntry> pageTableOfProcess = FXCollections.observableArrayList();
        List<TableEntry> table = ram.getPageTables().get(processID);
        System.out.println(table);
        pageTableOfProcess.addAll(table);
        pageTable.setItems(pageTableOfProcess);

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

    public void startProcess(int processId) {

        int numberOfProcessesInRAM = ram.getProcessList().size();

        //Max 4 processes in RAM
        if(numberOfProcessesInRAM<4){

            ram.setProcessesInRAM(numberOfProcessesInRAM+1);
            //number of frames each process must have after startup process
            int framesPerProcess = 12/(numberOfProcessesInRAM+1);
            int pagesToRemoveFromRAMPerProcess = Math.abs(12/(numberOfProcessesInRAM - framesPerProcess));

            //Via LRU remove pages from all processes in RAM

            //create new page table and put each page in RAM
            List<TableEntry> pageTable = new ArrayList<TableEntry>(12);

            //move pages to free places in RAM
            //for every moved page place entry in page table
            for(int i=0; i<16; i++){

                if(i<framesPerProcess){
                    //look for free frame
                    int freeFrameNumber = -1;
                    int frameNumber = 0;
                    while(freeFrameNumber==-1){
                        if(ram.getFrameArray()[frameNumber]==null){
                            freeFrameNumber=frameNumber;
                        }
                        else{
                            frameNumber++;
                        }
                    }

                    ram.getFrameArray()[freeFrameNumber]=new Page(freeFrameNumber+1,processId,i+1);
                    pageTable.add(new TableEntry(i+1, true, false, -1, freeFrameNumber+1));

                }
                else{
                    pageTable.add(new TableEntry(i+1,false, false, -1, 0));
                }


            }

            for(int j=0;j<pageTable.size();j++){
                pageTable.get(j);
            }
            ram.addPageTable(processId,pageTable);

        }
    }

}
