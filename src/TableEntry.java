public class TableEntry {
    private boolean presentBit;
    private boolean modifyBit;
    private int lastAccessTime;
    private int frameNumber;

    public TableEntry() {
        this.presentBit = false;
        this.modifyBit = false;
        this.lastAccessTime = -1;
        this.frameNumber = 0;
    }

    public boolean isPresentBit() {
        return presentBit;
    }

    public void setPresentBit(boolean presentBit) {
        this.presentBit = presentBit;
    }

    public boolean isModifyBit() {
        return modifyBit;
    }

    public void setModifyBit(boolean modifyBit) {
        this.modifyBit = modifyBit;
    }

    public int getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(int lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }
}
