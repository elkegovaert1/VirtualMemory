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
    public Label netProcessIDLabel;

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
    public Label volgprocessidLabel;

    @FXML
    public Label volginstructieLabel;

    @FXML
    public Label volgreadrLabel;

    @FXML
    public Label pagetableLabel;

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

    @FXML
    private MenuButton panelChoice;

    //List global of instructions;
    List<Instruction> instructionsGlob;

    //Integer for the amount of instruction
    private int amountOfInstruction = 0;


    //List 1 of instructions
    List<Instruction> instructions1;

    //List 2 of instructions
    List<Instruction> instructions2;

    //List 3 of instructions
    List<Instruction> instructions3;

    //timer starts at -1
    private int timer = -1;

    //amount of pages from persistent memory to RAM: Global
    private int persistentToRAM =0;

    //amount of pages from RAM to persistent memory: Global
    private int RAMToPersistent =0;

    //RAM
    RAM ram = new RAM(0);

    //address of next instruction to be selected out of virtual memory
    //Start at instruction 0;
    private int indexNextInstruction = 0;

    //When starting the application populate all the Lists
    public void initializeList1(List<Instruction> instructions) {
        instructions1 = instructions;

        //Default for startup
        instructionsGlob = instructions1;
        persistentToRAM = 0;
        RAMToPersistent = 0;

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
        if(timer+1<instructionsGlob.size()){

        //Timer + 1
        timer = timer + 1;

        //execute next instruction
        int virtualAddressInstruction = indexNextInstruction;
        Instruction instruction = instructionsGlob.get(virtualAddressInstruction);

        String operation = instruction.getOperation();
        int processID = instruction.getProcessID();
        int virtualAdress = instruction.getAdress();

        //Different operations are possible
        if(operation.equals("Start")){
            startProcess(processID);

            //show labels current instruction
            netProcessIDLabel.setText(String.valueOf(processID));
            netinstructieLabel.setText(operation);
            netvirtadrLabel.setText("Geen");
            netframeLabel.setText("Geen");
            netoffsetlabel.setText("Geen");
            netreadrLabel.setText("Geen");

            volgprocessidLabel.setText(String.valueOf(instructionsGlob.get(virtualAddressInstruction + 1).getProcessID()));
            volginstructieLabel.setText(instructionsGlob.get(virtualAddressInstruction+1).getOperation());
            if (instructionsGlob.get(virtualAddressInstruction+1).getAdress() == 0) {
                volgreadrLabel.setText("Geen");
            } else {
                volgreadrLabel.setText(String.valueOf(instructionsGlob.get(virtualAddressInstruction + 1).getAdress()));
            }
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
                //System.out.println(timer);
                ram.setPageTablePageAccessTime(processID, pageNumber, timer);

            }
            else{
                //via RLU load page in RAM and remove one
                //first remove LRU Page
                replacementViaLRU(processID);
                RAMToPersistent++;

                //move page to free place in RAM
                persistentToRAM++;
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
            netProcessIDLabel.setText(String.valueOf(processID));
            netinstructieLabel.setText(operation);
            netvirtadrLabel.setText(String.valueOf(virtualAdress));
            netframeLabel.setText(String.valueOf(frameNumber));
            netoffsetlabel.setText(String.valueOf(offsetInPage));
            netreadrLabel.setText(String.valueOf(realAdress));

            volgprocessidLabel.setText(String.valueOf(instructionsGlob.get(virtualAddressInstruction + 1).getProcessID()));
            volginstructieLabel.setText(instructionsGlob.get(virtualAddressInstruction+1).getOperation());
            if (instructionsGlob.get(virtualAddressInstruction+1).getAdress() == 0) {
                volgreadrLabel.setText("Geen");
            } else {
                volgreadrLabel.setText(String.valueOf(instructionsGlob.get(virtualAddressInstruction + 1).getAdress()));
            }
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
                //System.out.println(timer);
                ram.setPageTablePageAccessTime(processID, pageNumber, timer);
                ram.getPageTables().get(processID).get(pageNumber).setModifyBit(true);

            }
            else{
                //via RLU load page in RAM and remove one
                //first remove LRU Page
                replacementViaLRU(processID);
                RAMToPersistent++;

                //move page to free place in RAM
                persistentToRAM++;
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
            netProcessIDLabel.setText(String.valueOf(processID));
            netinstructieLabel.setText(operation);
            netvirtadrLabel.setText(String.valueOf(virtualAdress));
            netframeLabel.setText(String.valueOf(frameNumber));
            netoffsetlabel.setText(String.valueOf(offsetInPage));
            netreadrLabel.setText(String.valueOf(realAdress));

            volgprocessidLabel.setText(String.valueOf(instructionsGlob.get(virtualAddressInstruction + 1).getProcessID()));
            volginstructieLabel.setText(instructionsGlob.get(virtualAddressInstruction+1).getOperation());
            if (instructionsGlob.get(virtualAddressInstruction+1).getAdress() == 0) {
                volgreadrLabel.setText("Geen");
            } else {
                volgreadrLabel.setText(String.valueOf(instructionsGlob.get(virtualAddressInstruction + 1).getAdress()));
            }
        }
        else if(operation.equals("Terminate")){

            finishProcess(processID);

            //show labels current instruction
            netProcessIDLabel.setText(String.valueOf(processID));
            netinstructieLabel.setText(operation);
            netvirtadrLabel.setText("Geen");
            netframeLabel.setText("Geen");
            netoffsetlabel.setText("Geen");
            netreadrLabel.setText("Geen");

            if (indexNextInstruction != instructionsGlob.size() - 1) {
                volgprocessidLabel.setText(String.valueOf(instructionsGlob.get(virtualAddressInstruction + 1).getProcessID()));
                volginstructieLabel.setText(instructionsGlob.get(virtualAddressInstruction+1).getOperation());
                if (instructionsGlob.get(virtualAddressInstruction+1).getAdress() == 0) {
                    volgreadrLabel.setText("Geen");
                } else {
                    volgreadrLabel.setText(String.valueOf(instructionsGlob.get(virtualAddressInstruction + 1).getAdress()));
                }
            } else {
                volgprocessidLabel.setText("Geen");
                volgreadrLabel.setText("Geen");
                volginstructieLabel.setText("Geen");
            }
        }

        //show timer value
        timerLabel.setText(String.valueOf(timer));

        //show number of writes to RAM
        aantalWritesLabel.setText(String.valueOf(persistentToRAM));

        //show number of reads from RAM
        aantalReadsLabel.setText(String.valueOf(RAMToPersistent));

        //show RAM table
        ObservableList<Page> ramTable = FXCollections.observableArrayList();
        Page[] tablePage = ram.getFrameArray();

        ramTable.addAll(tablePage);
        RAMTable.refresh();
        RAMTable.setItems(ramTable);

        String [] colourArray1 = {
                "-fx-background-color : #f25f16",
                "-fx-background-color : #f2169a",
                "-fx-background-color : #16f2e7",
                "-fx-background-color : #f2c216",
                "-fx-background-color : #7916f2",
                "-fx-background-color : #acf216",
                "-fx-background-color : #67f216",
                "-fx-background-color : #16f242",
                "-fx-background-color : #16f2b4",
                "-fx-background-color : #f29e16",
                "-fx-background-color : #16cdf2",
                "-fx-background-color : #1696f2",
                "-fx-background-color : #1628f2",
                "-fx-background-color : #4d16f2",
                "-fx-background-color : #e7f216",
                "-fx-background-color : #ac16f2",
                "-fx-background-color : #f216ee",
                "-fx-background-color : #637364",
                "-fx-background-color : #eb2e21",
                "-fx-background-color : #ffffff"
        };
        RAMTable.setRowFactory(row -> new TableRow<Page>(){
            @Override
            public void updateItem(Page item, boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    for(int i=0; i< row.getColumns().size();i++) {
                        row.getColumns().get(i).setStyle("");
                    }
                } else {
                    int kleur = item.getProcessId();

                    //We apply now the changes in all the cells of the row
                    for(int i=0; i< row.getColumns().size();i++) {
                        row.getColumns().get(i).setStyle(colourArray1[kleur]);
                    }
                }
            }
        });


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

        if (instructionsGlob.get(indexNextInstruction).getOperation().equals("Terminate")) {
            pagetableLabel.setText("Page table leeg door terminate operatie");
        } else {
            pagetableLabel.setText("Page table van process: " + instructionsGlob.get(indexNextInstruction).getProcessID());
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //GROEN OF ROOD
            //Nog een comment
            pageTable.setRowFactory(row -> new TableRow<TableEntry>(){
                @Override
                public void updateItem(TableEntry item, boolean empty){
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        for(int i=0; i< row.getColumns().size();i++) {
                            row.getColumns().get(i).setStyle("");
                        }
                    } else {
                        if (item.isPresentBit() == true) {
                            for(int i=0; i< row.getColumns().size();i++) {
                                row.getColumns().get(i).setStyle("-fx-background-color : green");
                            }
                        } else {
                            for(int i=0; i< row.getColumns().size();i++) {
                                row.getColumns().get(i).setStyle("-fx-background-color : red");
                            }
                        }
                    }
                }
            });
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        indexNextInstruction++;

        }
    }

    @FXML //Execute all instructions and update the view
    void executeAll(ActionEvent event) {
        while(timer+1<instructionsGlob.size()){
            //Timer + 1
            timer = timer + 1;

            //execute next instruction
            int virtualAddressInstruction = indexNextInstruction;
            Instruction instruction = instructionsGlob.get(virtualAddressInstruction);

            String operation = instruction.getOperation();
            int processID = instruction.getProcessID();
            int virtualAdress = instruction.getAdress();

            //Different operations are possible
            if(operation.equals("Start")){
                startProcess(processID);
            }
            else if(operation.equals("Read")){

                int pageNumber = givePageNumberOfVirtualAdress(virtualAdress);

                //See if page is in RAM
                List<TableEntry> pageTable = ram.getPageTables().get(processID);

                boolean pageIsInRAM = pageTable.get(pageNumber).isPresentBit();

                if(pageIsInRAM){
                    //change access time
                    ram.setPageTablePageAccessTime(processID, pageNumber, timer);
                }
                else{
                    //via RLU load page in RAM and remove one
                    //first remove LRU Page
                    replacementViaLRU(processID);
                    RAMToPersistent++;

                    //move page to free place in RAM
                    persistentToRAM++;
                    //Adjust page Table
                    for(int i=0;i<ram.getFrameArray().length;i++){
                        if(ram.getFrameArray()[i]==null){
                            ram.getFrameArray()[i]=new Page(i,processID,pageNumber);
                            pageTable.get(pageNumber).setPresentBit(true);
                            pageTable.get(pageNumber).setFrameNumber(i);
                            pageTable.get(pageNumber).setLastAccessTime(timer);
                        }
                    }
                }
            }
            else if(operation.equals("Write")){

                int pageNumber = givePageNumberOfVirtualAdress(virtualAdress);

                //See if page is in RAM
                List<TableEntry> pageTable = ram.getPageTables().get(processID);
                boolean pageIsInRAM = pageTable.get(pageNumber).isPresentBit();

                if(pageIsInRAM){

                    //change access time
                    ram.setPageTablePageAccessTime(processID, pageNumber, timer);
                    ram.getPageTables().get(processID).get(pageNumber).setModifyBit(true);

                }
                else{
                    //via RLU load page in RAM and remove one
                    //first remove LRU Page
                    replacementViaLRU(processID);
                    RAMToPersistent++;

                    //move page to free place in RAM
                    persistentToRAM++;
                    //Adjust page Table
                    for(int i=0;i<ram.getFrameArray().length;i++){
                        if(ram.getFrameArray()[i]==null){
                            ram.getFrameArray()[i]=new Page(i,processID,pageNumber);
                            pageTable.get(pageNumber).setPresentBit(true);
                            pageTable.get(pageNumber).setFrameNumber(i);
                            pageTable.get(pageNumber).setLastAccessTime(timer);
                            pageTable.get(pageNumber).setModifyBit(true);
                        }
                    }
                }
            }
            else if(operation.equals("Terminate")){

                finishProcess(processID);

                //show labels current instruction
                if(timer == instructionsGlob.size()-1) {
                    //vorige
                    netProcessIDLabel.setText(String.valueOf(processID));
                    netinstructieLabel.setText(operation);
                    netvirtadrLabel.setText("Geen");
                    netframeLabel.setText("Geen");
                    netoffsetlabel.setText("Geen");
                    netreadrLabel.setText("Geen");
                    //volgende
                    volginstructieLabel.setText("null");
                    volgreadrLabel.setText("null");
                }
            }

            if(timer == instructionsGlob.size()-1) {
                //show timer value
                timerLabel.setText(String.valueOf(timer));

                //show number of writes to RAM
                aantalWritesLabel.setText(String.valueOf(persistentToRAM));

                //show number of reads from RAM
                aantalReadsLabel.setText(String.valueOf(RAMToPersistent));
            }

            //show RAM table
            ObservableList<Page> ramTable = FXCollections.observableArrayList();
            Page[] tablePage = ram.getFrameArray();

            ramTable.addAll(tablePage);

            //show Page Table of current running process
            ObservableList<TableEntry> pageTableOfProcess = FXCollections.observableArrayList();
            List<TableEntry> table = ram.getPageTables().get(processID);

            if(table!=null){
                pageTableOfProcess.addAll(table);
            }


            indexNextInstruction++;
        }
    }

    @FXML //Execute one instruction and update the view
    void restart(ActionEvent event) {
        //reset everything
        timer = -1;
        ram = new RAM(12);
        indexNextInstruction = 0;
        RAMToPersistent = 0;
        persistentToRAM = 0;

        //show timer value
        timerLabel.setText(String.valueOf(timer));

        //show number of writes to RAM
        aantalWritesLabel.setText(String.valueOf(persistentToRAM));

        //show number of reads from RAM
        aantalReadsLabel.setText(String.valueOf(RAMToPersistent));

        //reset labels
        netreadrLabel.setText("---");
        netoffsetlabel.setText("---");
        netframeLabel.setText("---");
        netvirtadrLabel.setText("---");
        netinstructieLabel.setText("---");

        //reset RAM Table
        RAMTable.setItems(null);

        //reset Page Table
        pageTable.setItems(null);

    }

    @FXML
    void choice2000020(ActionEvent event) {
        panelChoice.setText("20000_20");
        amountOfInstruction = 20000;
        instructionsGlob = instructions3;
        restart(new ActionEvent());
    }

    @FXML
    void choice200004(ActionEvent event) {
        panelChoice.setText("20000_4");
        amountOfInstruction = 20000;
        instructionsGlob = instructions2;
        restart(new ActionEvent());
    }

    @FXML
    void choice303(ActionEvent event) {
        panelChoice.setText("30_3");
        amountOfInstruction = 20;
        instructionsGlob = instructions1;
        restart(new ActionEvent());
    }

    public void startProcess(int processId) {

        int numberOfProcessesInRAM = ram.getProcessesInRAM();
        //System.out.println(numberOfProcessesInRAM);
        //Max 4 processes in RAM
        if(numberOfProcessesInRAM<4){

            ram.setProcessesInRAM(numberOfProcessesInRAM+1);
            //number of frames each process must have after startup process
            int framesPerProcess = 12/(numberOfProcessesInRAM+1);

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
            //for every moved page, place entry in page table
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
                    persistentToRAM++;
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
                RAMToPersistent++;
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
                persistentToRAM++;

                //Update pageTeble
                pageTable.get(nextNotPresentPageNumber).setPresentBit(true);
                pageTable.get(nextNotPresentPageNumber).setFrameNumber(nextEmptyFrame);

            }

        }

    }

    //when new process comes in from all the processes a same amount has to be removed of every process
    public void internalReplacementViaLRU(int numberOfFramesToRemovePerProcess){

        //System.out.println(numberOfFramesToRemovePerProcess);

        Map<Integer, List<TableEntry>> pageTables = ram.getPageTables();

        for (Map.Entry<Integer, List<TableEntry>> pageTableEntry : pageTables.entrySet()){

            int processID = pageTableEntry.getKey();
            List<TableEntry> pageTable = pageTableEntry.getValue();

            //LRU per Process
            for(int i = 0; i<numberOfFramesToRemovePerProcess; i++){
                replacementViaLRU(processID);
                RAMToPersistent++;
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
