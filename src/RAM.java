import java.util.*;

public class RAM {
    //Page Tables
    //Mapping of processId on List<TableEntry> pagetable
    private Map<Integer, List<TableEntry>> pageTables;
    //processes that have been in ram => keeps amount of writes and reads from and to persistent memory
    private List<Process> processList;
    private Page[] frameArray;
    private int frameCount;
    private int processesInRAM;

    public RAM(int fc) {
        this.frameCount = fc;
        pageTables = new HashMap<Integer, List<TableEntry>>();
        processList = new ArrayList<>();
        frameArray = new Page[12];
        processesInRAM = 0;
    }

    public void addPageTable(int processId, List<TableEntry> table){
        pageTables.put(processId,table);
    }

    public Map<Integer, List<TableEntry>> getPageTables() {
        return pageTables;
    }

    public void setPageTables(Map<Integer, List<TableEntry>> pageTables) {
        this.pageTables = pageTables;
    }

    public List<Process> getProcessList() {
        return processList;
    }

    public void setProcessList(List<Process> processList) {
        this.processList = processList;
    }

    public Page[] getFrameArray() {
        return frameArray;
    }

    public void setFrameArray(Page[] frameArray) {
        this.frameArray = frameArray;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public int getProcessesInRAM() {
        return processesInRAM;
    }

    public void setProcessesInRAM(int processesInRAM) {
        this.processesInRAM = processesInRAM;
    }

    public void setPageTablePageAccessTime(int processID, int pageNumber, int timer){
        pageTables.get(processID).get(pageNumber).setLastAccessTime(timer);
    }
}
