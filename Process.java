
public class Process implements Comparable<Process>{

    private int PID;
    private int ArrivalTime;
    private int burst_no;
    private int bursts[];
    private String [] status;
    private int wattingTime;
    private int turnArroundTime;
    private  int CPU_index=0;
    private int IO_index=1;
    private int counter_in=0;
    private int exit_no=0;
    private  int estimatedValue=0;
    private int orginBurst[];




    public Process() {
        super();
    }






    public Process(int pID, int arrivalTime, int burst_no, int[] bursts,int[] orginBurst, String[] status) {
        super();
        PID = pID;
        ArrivalTime = arrivalTime;
        this.burst_no = burst_no;
        this.bursts = bursts;
        this.orginBurst=orginBurst;
        this.status= status;
        this.wattingTime=0;
        this.turnArroundTime=0;




    }






    public int getPID() {
        return PID;
    }






    public void setPID(int pID) {
        PID = pID;
    }






    public int getArrivalTime() {
        return ArrivalTime;
    }






    public void setArrivalTime(int arrivalTime) {
        ArrivalTime = arrivalTime;
    }






    public int getBurst_no() {
        return burst_no;
    }






    public void setBurst_no(int burst_no) {

        this.burst_no = burst_no;
    }






    public int getBursts(int i) {

        return bursts[i];


    }

    public void setBursts(int i,int newBurst) {

        this.bursts[i] = newBurst;
    }




    public int getOrginBurst(int i) {

        return orginBurst[i];


    }

    public void setOrginBurst(int i,int newBurst) {

        this.orginBurst[i] = newBurst;
    }

    public String getStatus(int i) {
        return status[i];
    }






    public void setStatus(int i,String status) {
        this.status[i] = status;
    }











    public int getWattingTime() {
        return wattingTime;
    }






    public void setWattingTime(int wattingTime) {
        this.wattingTime = wattingTime;
    }


    public int getTurnArroundTime() {
        return turnArroundTime;
    }


    public void setTurnArroundTime(int turnArroundTime) {
        this.turnArroundTime = turnArroundTime;
    }


    public int getCPU_index() {
        return CPU_index;
    }

    public void setCPU_index(int CPU_index) {
        this.CPU_index = CPU_index;
    }

    public int getIO_index() {
        return IO_index;
    }

    public void setIO_index(int IO_index) {
        this.IO_index = IO_index;
    }

    public int getCounter_in() {
        return counter_in;
    }

    public void setCounter_in(int counter_in) {
        this.counter_in = counter_in;
    }

    public void setExit_no(int exit_no) {
        this.exit_no = exit_no;
    }

    public int getExit_no() {
        return exit_no;
    }

    public int getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(int estimatedValue) {
        this.estimatedValue = estimatedValue;
    }



    @Override
    public int compareTo(Process o) {
        int compareArrivel=((Process)o).getArrivalTime();
        return this.ArrivalTime - compareArrivel;
    }





}
