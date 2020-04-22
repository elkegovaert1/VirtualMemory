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
    private int indexNextInstruction = 0;

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
        if(timer+1<instructions1.size()){

        //Timer + 1
        timer = timer + 1;

        //execute next instruction
        int virtualAddressInstruction = indexNextInstruction;
        Instruction instruction = instructions1.get(virtualAddressInstruction);

        String operation = instruction.getOperation();
        int processID = instruction.getProcessID();
        int virtualAdress = instruction.getAdress();

        //Different operations are possible
        if(operation.equals("Start")){
            startProcess(processID);

            //show labels current instruction
            netinstructieLabel.setText(operation);
            netvirtadrLabel.setText("Geen");
            netframeLabel.setText("Geen");
            netoffsetlabel.setText("Geen");
            netreadrLabel.setText("Geen");
        }
        else if(operation.equals("Read")){

            int pageNumber = givePageNumberOfVirtualAdress(virtualAdress);
            int offsetInPage = virtualAdress-pageNumber*4096;

            //See if page is in RAM
            List<TableEntry> pageTable = ram.getPageTables().get(processID);

            boolean pageIsInRAM = pageTable.get(pageNumber).isPresentBit();

            int frameNumber = -1;
            int realAdress = -1;

            if(pageIsInRAM){
                frameNumber = pageTable.get(pageNumber).getFrameNumber();
                realAdress = 4096*frameNumber + offsetInPage;

                //change access time
                System.out.println(timer);
                ram.setPageTablePageAccessTime(processID, pageNumber, timer);

            }
            else{
                //via RLU load page in RAM and remove one
                //first remove LRU Page
                replacementViaLRU(processID);

                //move page to free place in RAM
                //Adjust page Table
                for(int i=0;i<ram.getFrameArray().length;i++){
                    if(ram.getFrameArray()[i]==null){
                        ram.getFrameArray()[i]=new Page(i,processID,pageNumber);
                        pageTable.get(pageNumber).setPresentBit(true);
                        pageTable.get(pageNumber).setFrameNumber(i);
                        pageTable.get(pageNumber).setLastAccessTime(timer);


                        frameNumber = pageTable.get(pageNumber).getFrameNumber();
                        realAdress = 4096*frameNumber + offsetInPage;
                    }
                }
            }

            //show labels current instruction
            netinstructieLabel.setText(operation);
            netvirtadrLabel.setText(String.valueOf(virtualAdress));
            netframeLabel.setText(String.valueOf(frameNumber));
            netoffsetlabel.setText(String.valueOf(offsetInPage));
            netreadrLabel.setText(String.valueOf(realAdress));
        }
        else if(operation.equals("Write")){

            int pageNumber = givePageNumberOfVirtualAdress(virtualAdress);
            int offsetInPage = virtualAdress-pageNumber*4096;

            //See if page is in RAM
            List<TableEntry> pageTable = ram.getPageTables().get(processID);

            boolean pageIsInRAM = pageTable.get(pageNumber).isPresentBit();

            int frameNumber = -1;
            int realAdress = -1;

            if(pageIsInRAM){
                frameNumber = pageTable.get(pageNumber).getFrameNumber();
                realAdress = 4096*frameNumber + offsetInPage;

                //change access time
                System.out.println(timer);
                ram.setPageTablePageAccessTime(processID, pageNumber, timer);
                ram.getPageTables().get(processID).get(pageNumber).setModifyBit(true);

            }
            else{
                //via RLU load page in RAM and remove one
                //first remove LRU Page
                replacementViaLRU(processID);

                //move page to free place in RAM
                //Adjust page Table
                for(int i=0;i<ram.getFrameArray().length;i++){
                    if(ram.getFrameArray()[i]==null){
                        ram.getFrameArray()[i]=new Page(i,processID,pageNumber);
                        pageTable.get(pageNumber).setPresentBit(true);
                        pageTable.get(pageNumber).setFrameNumber(i);
                        pageTable.get(pageNumber).setLastAccessTime(timer);
                        pageTable.get(pageNumber).setModifyBit(true);

                        frameNumber = pageTable.get(pageNumber).getFrameNumber();
                        realAdress = 4096*frameNumber + offsetInPage;
                    }
                }
            }

            //show labels current instruction
            netinstructieLabel.setText(operation);
            netvirtadrLabel.setText(String.valueOf(virtualAdress));
            netframeLabel.setText(String.valueOf(frameNumber));
            netoffsetlabel.setText(String.valueOf(offsetInPage));
            netreadrLabel.setText(String.valueOf(realAdress));
        }
        else if(operation.equals("Terminate")){

            finishProcess(processID);

            //show labels current instruction
            netinstructieLabel.setText(operation);
            netvirtadrLabel.setText("Geen");
            netframeLabel.setText("Geen");
            netoffsetlabel.setText("Geen");
            netreadrLabel.setText("Geen");
        }


        //show timer value
        timerLabel.setText(String.valueOf(timer));

        //show number of writes to RAM
        aantalWritesLabel.setText(String.valueOf(writesToRAM));

        //show number of reads from RAM
        aantalReadsLabel.setText(String.valueOf(readsFromRAM));

        //show RAM table
        ObservableList<Page> ramTable = FXCollections.observableArrayList();
        Page[] tablePage = ram.getFrameArray();

        ramTable.addAll(tablePage);
        RAMTable.refresh();
        RAMTable.setItems(ramTable);

        //show Page Table of current running process
        ObservableList<TableEntry> pageTableOfProcess = FXCollections.observableArrayList();
        List<TableEntry> table = ram.getPageTables().get(processID);
        /**
        for(int i=0;i<table.size();i++){
            System.out.println(table.get(i));
        }
        **/
        if(table!=null){
            pageTableOfProcess.addAll(table);
        }
        pageTable.refresh();
        pageTable.setItems(pageTableOfProcess);

        indexNextInstruction++;

        }
    }

    @FXML //Execute one instruction and update the view
    void executeAll(ActionEvent event) {}

    @FXML //Execute one instruction and update the view
    void restart(ActionEvent event) {
        //reset everything
        timer = -1;
        ram = new RAM(12);
        indexNextInstruction = 0;
        readsFromRAM = 0;
        writesToRAM = 0;

        //show timer value
        timerLabel.setText(String.valueOf(timer));

        //show number of writes to RAM
        aantalWritesLabel.setText(String.valueOf(writesToRAM));

        //show number of reads from RAM
        aantalReadsLabel.setText(String.valueOf(readsFromRAM));

        //reset labels
        netreadrLabel.setText("---");
        netoffsetlabel.setText("---");
        netframeLabel.setText("---");
        netvirtadrLabel.setText("---");
        netinstructieLabel.setText("---");

        //show RAM table
        //ObservableList<Page> ramTable = FXCollections.observableArrayList();
        //Page[] tablePage = ram.getFrameArray();

        //ramTable.addAll(tablePage);
        RAMTable.refresh();
        //RAMTable.setItems(ramTable);

        //show Page Table of current running process
        //ObservableList<TableEntry> pageTableOfProcess = FXCollections.observableArrayList();
        //List<TableEntry> table = ram.getPageTables().get(processID);

        //if(table!=null){
         //   pageTableOfProcess.addAll(table);
        //}
        pageTable.refresh();
        //pageTable.setItems(pageTableOfProcess);

    }

    public void startProcess(int processId) {

        int numberOfProcessesInRAM = ram.getProcessesInRAM();
        System.out.println(numberOfProcessesInRAM);
        //Max 4 processes in RAM
        if(numberOfProcessesInRAM<4){

            ram.setProcessesInRAM(numberOfProcessesInRAM+1);
            //number of frames each process must have after startup process
            int framesPerProcess = 12/(numberOfProcessesInRAM+1);
            System.out.println(framesPerProcess);
            int pagesToRemoveFromRAMPerProcess = 0;
            if(numberOfProcessesInRAM!=0){
                pagesToRemoveFromRAMPerProcess = Math.abs(12/numberOfProcessesInRAM - framesPerProcess);
            }

            //Via LRU remove pages from all processes in RAM
            //Replace from all processes the same amount of pages to make free place for new process
            internalReplacementViaLRU(pagesToRemoveFromRAMPerProcess);


            //create new page table and put each page in RAM
            List<TableEntry> pageTable = new ArrayList<TableEntry>(12);

            //move pages to empty free places in RAM
            //for every moved page place entry in page table
            for(int k=0;k<ram.getFrameArray().length;k++){
                System.out.println(ram.getFrameArray()[k]);
            }

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

                    ram.getFrameArray()[freeFrameNumber]=new Page(freeFrameNumber,processId,i);
                    pageTable.add(new TableEntry(i, true, false, -1, freeFrameNumber));

                }
                else{
                    pageTable.add(new TableEntry(i,false, false, -1, -1));
                }


            }

            for(int j=0;j<pageTable.size();j++){
                pageTable.get(j);
            }
            ram.addPageTable(processId,pageTable);

        }
    }

    public void finishProcess(int processId){
        //When finishing a process the free coming frames need to be distributed among the free processes
        int beforeTermNumbOfProcessesInRAM = ram.getProcessesInRAM();

        int extraFramesPerProcess = 0;
        if(beforeTermNumbOfProcessesInRAM!=1){
            extraFramesPerProcess = 12/(beforeTermNumbOfProcessesInRAM-1)-12/beforeTermNumbOfProcessesInRAM;
        }




        //Remove frames from RAM
        Page[] frameArray = ram.getFrameArray();

        for(int i=0;i<frameArray.length;i++){
            if(frameArray[i].getProcessId()==processId){
                frameArray[i] = null;
            }
        }

        //Remove Page Table
        ram.getPageTables().remove(processId);

        //number of processes in RAM - 1
        ram.setProcessesInRAM(ram.getProcessesInRAM()-1);

        //give each remaing process extra frames
        Map<Integer, List<TableEntry>> pageTables = ram.getPageTables();

        for (Map.Entry<Integer, List<TableEntry>> pageTablesEntry : pageTables.entrySet()){

            int processID = pageTablesEntry.getKey();
            List<TableEntry> pageTable = pageTablesEntry.getValue();

            for(int j=0; j<extraFramesPerProcess; j++){

                int nextNotPresentPageNumber = findNextNotPresentPageNumber(pageTable);

                //Find next empty frame
                //Put page in next empty frame
                int nextEmptyFrame = -1;
                int f = 0;

                while(nextEmptyFrame==-1){
                    if(frameArray[f]==null){
                        nextEmptyFrame=f;
                    }
                    else {
                        f++;
                    }
                }

                frameArray[nextEmptyFrame] = new Page(nextEmptyFrame,processID,nextNotPresentPageNumber);

                //Update pageTeble
                pageTable.get(nextNotPresentPageNumber).setPresentBit(true);
                pageTable.get(nextNotPresentPageNumber).setFrameNumber(nextEmptyFrame);

            }

        }

    }

    //when new process comes in from all the processes a same amount has to be removed of every process
    public void internalReplacementViaLRU(int numberOfFramesToRemovePerProcess){

        System.out.println(numberOfFramesToRemovePerProcess);

        Map<Integer, List<TableEntry>> pageTables = ram.getPageTables();

        for (Map.Entry<Integer, List<TableEntry>> pageTableEntry : pageTables.entrySet()){

            int processID = pageTableEntry.getKey();
            List<TableEntry> pageTable = pageTableEntry.getValue();

            //LRU per Process
            for(int i = 0; i<numberOfFramesToRemovePerProcess; i++){
                replacementViaLRU(processID);
            }


        }

    }

    //Replacement via Least Recently Used, only remove
    public void replacementViaLRU(int processID){

        List<TableEntry> pageTable = ram.getPageTables().get(processID);

        TableEntry leastRecentlyUsed = pageTable.get(0);
        boolean startFound = false;
        int start = 0;
        while(!startFound) {
            if(pageTable.get(start).isPresentBit()){
                leastRecentlyUsed = pageTable.get(start);
                startFound = true;
            }
            else{
                start++;
            }
        }

        for(int i=start; i<pageTable.size(); i++){
            if(leastRecentlyUsed.getLastAccessTime()>pageTable.get(i).getLastAccessTime()&&pageTable.get(i).isPresentBit()){
                leastRecentlyUsed = pageTable.get(i);
            }
        }

        //remove frame from RAM
        int frameNumberToRemove = leastRecentlyUsed.getFrameNumber();
        ram.getFrameArray()[frameNumberToRemove] = null;

        //set present bit to false
        leastRecentlyUsed.setPresentBit(false);
        leastRecentlyUsed.setFrameNumber(-1);

    }

    public int givePageNumberOfVirtualAdress(int virtualAdress) {
        return (int) Math.floor(virtualAdress/4096);
    }

    public int findNextNotPresentPageNumber(List<TableEntry> pageTable){

        int nextNotPresentPage = -1;
        int i = 0;

        while(nextNotPresentPage==-1){
            if(!pageTable.get(i).isPresentBit()){
                nextNotPresentPage = i;
            }
            else {
                i++;
            }
        }

        return nextNotPresentPage;
    }
}
