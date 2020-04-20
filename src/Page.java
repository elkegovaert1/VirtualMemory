public class Page {
    private int frameNumber;
    private int processId;
    private int pageNumber;

    public Page(int frameNumber, int processId, int pageNumber) {
        this.frameNumber = frameNumber;
        this.processId = processId;
        this.pageNumber = pageNumber;
    }

    public Page(Page page) {
        this.frameNumber = page.frameNumber;
        this.processId = page.processId;
        this.pageNumber = page.pageNumber;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
