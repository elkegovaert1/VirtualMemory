//Glenn Groothuis
//ELke Govaert
//Arne Reyniers
public class Instruction {
    private int processID;
    private String operation;
    private int adress;

    public Instruction() {}

    public Instruction(int processID, String operation, int adress) {
        this.processID = processID;
        this.operation = operation;
        this.adress = adress;
    }

    //getters
    public int getProcessID() {
        return processID;
    }

    public String getOperation() {
        return operation;
    }

    public int getAdress() {
        return adress;
    }

    //setters
    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setAdress(int adress) {
        this.adress = adress;
    }

    //tostring
    public String toString() {
        return "Instructie: ID " + processID + " for " + operation + " at " + adress + "\n";
    }
}
