public class OnCPU {
    private int PID;
    private int startTime;
    private int finishTime;
    private int burstIndex;
//put the process burst in array list to draw the gantt chart
    public OnCPU(int PID, int startTime, int finishTime,int burstIndex) {
        this.PID = PID;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.burstIndex=burstIndex;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getPID() {
        return PID;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public int getBurstIndex() {
        return burstIndex;
    }
}
