public class TableEntry {

    private int pageNumber;
    private boolean presentBit;
    private boolean modifyBit;
    private int lastAccessTime;
    private int frameNumber;

    public TableEntry() {
        this.presentBit = false;
        this.modifyBit = false;
        this.lastAccessTime = -1;
        this.frameNumber = 0;
        this.pageNumber = 0;
    }

    public TableEntry(int pageNumber, boolean presentBit, boolean modifyBit, int lastAccessTime, int frameNumber) {
        this.pageNumber = pageNumber;
        this.presentBit = presentBit;
        this.modifyBit = modifyBit;
        this.lastAccessTime = lastAccessTime;
        this.frameNumber = frameNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
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
