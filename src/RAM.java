import java.util.ArrayList;
import java.util.List;

public class RAM {
    private List<Process> processList;
    private Page[] frameArray;
    private int frameCount;

    public RAM(int fc) {
        this.frameCount = fc;
        processList = new ArrayList<>();
        frameArray = new Page[12];
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
}
