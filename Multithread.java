public class Multithread implements Runnable{

    private  Process process;
    private  int i;

    public Multithread(Process process, int i) {
        this.process = process;
        this.i =i;
    }

    @Override
    public void run() {

        if(process.getBurst_no()*2-1>i&&process.getBursts(i)>500&&process.getBursts(i)<1000){
            process.setBursts(i,0);
            process.setStatus(i,"Finished");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("IO Burst : " +i+ " for Process : "+process.getPID()+" is done!");
        } else if (process.getBurst_no()*2>i&&process.getBursts(i)<200) {
            process.setBursts(i,0);
            process.setStatus(i,"Finished");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("IO Burst : " +i+ " for Process : "+process.getPID()+" is done!");
        }
        else if(process.getBurst_no()*2>i){
            process.setBursts(i,0);
            process.setStatus(i,"Finished");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("IO Burst : " +i+ " for Process : "+process.getPID()+" is done!");
        }
    }


}
