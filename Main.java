import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
    public static Scanner in = new Scanner(System.in);
    private static ArrayList<Process> processes = new ArrayList<>();// store the process generated randomly.
    // implements the 4 queues
    static boolean running = false;
    static int totalWaitingTime = 0;
    static int totalTurnaroundTime = 0;

    static ArrayList<OnCPU> onCPU = new ArrayList<>();
    static ArrayList<Process> queue1 = new ArrayList<>();
    static ArrayList<Process> queue2 = new ArrayList<>();
    static ArrayList<Process> queue3 = new ArrayList<>();
    static ArrayList<Process> queue4 = new ArrayList<>();
    static ArrayList<Process> finished = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {


        System.out.println("choose an option :");
        System.out.println("1) Enter new data :");
        System.out.println("2) Use the data stored in the file :");
        int option = in.nextInt();
        if (option == 1) {
            System.out.println("Enter the  number of process:\n");
            int processNO = in.nextInt();
            Workload_Generator(processNO);// call the function that generate the processes and print the simulated processes in a file
            readFile();
        } else {

            readFile();
        }


        // printing the generated processes
        for (int i = 0; i < processes.size(); i++) {
            System.out.print(processes.get(i).getPID() + "\t");
            System.out.print(processes.get(i).getArrivalTime() + "\t");

            for (int j = 0; j < (processes.get(i).getBurst_no() * 2) - 1; j++) {
                System.out.print(processes.get(i).getBursts(j) + "\t");

            }
            System.out.println("");

        }


        ////////////////////////////////////// MultiLevel Feedback Queue
        ////////////////////////////////////// //////////////////////////////////////
        ////////////////////////////////////// ////////////////////////////////
        System.out.print("Enter the value of q1: ");
        int q1 = in.nextInt();
        System.out.print("Enter the value of q2: ");
        int q2 = in.nextInt();
        System.out.print("Enter the value of alpha: ");
        double alpha = in.nextDouble();

        ArrayList<Process> sortedArival = new ArrayList<>();
        for (int i = 0; i < processes.size(); i++) {
            sortedArival.add(processes.get(i));

        }
        Collections.sort(sortedArival);

        final int[] currentTime = {0};
        int waitingTime = 0;
        int turnaroundTime = 0;
        final int[] flag = {0};

        final int[] allUsed = {0};

        final Process[] temp = {new Process()};
        final int[] i = {0};
        final int[] firstF = {1};
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("Enter 's' to start counting, 't' to see working queues, or 'e' to quit: ");
            String command = input.nextLine().trim();

            if (command.equals("s")) {
                running = true;
                new Thread(() -> {
                    while (running) {
                        try {
                            Thread.sleep(1500);


                            while (i[0] < sortedArival.size()) {

                                while (sortedArival.get(i[0]).getArrivalTime() > currentTime[0]) {
                                    currentTime[0]++;
                                }if(firstF[0]==1) {
                                    System.out.println("Timer added " + currentTime[0] +" to get the first process !");
                                    firstF[0]=0;
                                }
                                if (flag[0] == 0 && sortedArival.get(i[0]).getArrivalTime() <= currentTime[0]) {
                                    queue1.add(sortedArival.get(i[0]));
                                    flag[0] = 1;
                                }
                                while (!queue1.isEmpty() && (queue1.get(0).getBurst_no() * 2) - 1 > queue1.get(0).getCPU_index() &&
                                        !queue1.get(0).getStatus(queue1.get(0).getCPU_index()).equalsIgnoreCase("Finished")) {
                                    if (queue1.get(0).getArrivalTime() <= currentTime[0]) {

                                        if (i[0] == sortedArival.size() - 1) {
                                            allUsed[0] = 1;
                                        }
                                        Thread.sleep(100);
                                        if (queue1.get(0).getBursts(queue1.get(0).getCPU_index()) <= q1 && queue1.get(0).getCounter_in() < 10 * q1) {
                                            System.out.print("time start at  " + currentTime[0]);
                                            onCPU.add(new OnCPU(queue1.get(0).getPID(), currentTime[0], currentTime[0] + queue1.get(0).getBursts(queue1.get(0).getCPU_index()),queue1.get(0).getCPU_index()));
                                            queue1.get(0).setStatus(queue1.get(0).getCPU_index(), "Finished");
                                            currentTime[0] += queue1.get(0).getBursts(queue1.get(0).getCPU_index());
                                            queue1.get(0).setBursts(queue1.get(0).getCPU_index(), 0);

                                            queue1.get(0).setWattingTime(currentTime[0]-queue1.get(0).getOrginBurst(queue1.get(0).getCPU_index())-queue1.get(0).getArrivalTime());
                                            queue1.get(0).setTurnArroundTime(currentTime[0]-queue1.get(0).getArrivalTime());
                                            totalWaitingTime+=queue1.get(0).getWattingTime();
                                            totalTurnaroundTime+=queue1.get(0).getTurnArroundTime();
                                            temp[0] = queue1.get(0);
                                            finished.add(temp[0]);


                                            if (queue1.get(0).getIO_index() < temp[0].getBurst_no() * 2 - 1 && queue1.get(0).getBursts(temp[0].getIO_index()) != 0) { //check if we have IO
                                                Multithread mythread = new Multithread(temp[0], queue1.get(0).getIO_index());  //creat multithreading to deal with the IO
                                                Thread thread = new Thread(mythread);
                                                thread.start();
                                                if ((queue1.get(0).getBurst_no() * 2) - 1 > queue1.get(0).getIO_index() + 2) {
                                                    queue1.get(0).setIO_index(queue1.get(0).getIO_index() + 2);
                                                }
                                            }

                                            System.out.println(" and finnish at  :" + currentTime[0] + "  at q1 for process l " + queue1.get(0).getPID() + "  status is : " + queue1.get(0).getStatus(queue1.get(0).getCPU_index()) + " index of burst " + queue1.get(0).getCPU_index());
                                            if ((queue1.get(0).getBurst_no() * 2) - 1 >= queue1.get(0).getCPU_index() + 2) {
                                                queue1.get(0).setCPU_index(queue1.get(0).getCPU_index() + 2);
                                                queue1.get(0).setCounter_in(0);
                                            }
                                            queue1.remove(0);
                                            Thread.sleep(500);
                                            if (allUsed[0] != 1) {

                                                while (i[0] + 1 < sortedArival.size() && currentTime[0] >= sortedArival.get(i[0] + 1).getArrivalTime()) {
                                                    i[0]++;
                                                    queue1.add(sortedArival.get(i[0]));
                                                    System.out.println("NEW PROCESS ARRIVED TO QUEUE1 !");

                                                    if (i[0] == sortedArival.size() - 1) {
                                                        allUsed[0] = 1;
                                                    }
                                                }
                                            }

                                            if (temp[0].getBursts((temp[0].getBurst_no() * 2) - 2) != 0) {
                                                queue1.add(temp[0]);
                                            }


                                        } else {

                                            if (queue1.get(0).getCounter_in() >= 10 * q1) {
                                                queue1.get(0).setCounter_in(0);
                                                queue2.add(queue1.get(0));
                                                System.out.println("PROCESS "+ queue1.get(0).getPID() +" SENT TO QUEUE 2 !!! " );
                                                queue1.remove(0);

                                                if (queue1.isEmpty()) {
                                                    break;
                                                }
                                            }
                                            Thread.sleep(500); Thread.sleep(500);
                                            System.out.print("time start at  " + currentTime[0]);
                                            onCPU.add(new OnCPU(queue1.get(0).getPID(), currentTime[0], currentTime[0] + q1,queue1.get(0).getCPU_index()));
                                            queue1.get(0).setBursts(queue1.get(0).getCPU_index(), (queue1.get(0).getBursts(queue1.get(0).getCPU_index()) - q1));
                                            queue1.get(0).setStatus(queue1.get(0).getCPU_index(), "Working");
                                            currentTime[0] += q1;
                                            temp[0] = queue1.get(0);
                                            queue1.get(0).setCounter_in(queue1.get(0).getCounter_in() + q1);

                                            System.out.println("  and finnish at  :" + currentTime[0] + " at q1  for process ov "
                                                    + queue1.get(0).getPID() + "  status is : " + queue1.get(0).getStatus(queue1.get(0).getCPU_index()) + "  and remainning : " + queue1.get(0).getBursts(queue1.get(0).getCPU_index()));
                                            queue1.remove(0);

                                            if (allUsed[0] != 1) {


                                                while (i[0] + 1 < sortedArival.size() && currentTime[0] >= sortedArival.get(i[0] + 1).getArrivalTime()) {
                                                    i[0]++;
                                                    queue1.add(sortedArival.get(i[0]));
                                                    System.out.println("NEW PROCESS ARRIVED TO QUEUE1 !");

                                                    if (i[0] == sortedArival.size() - 1) {
                                                        allUsed[0] = 1;
                                                    }
                                                }
                                            }


                                            if (temp[0].getBursts((temp[0].getBurst_no() * 2) - 2) != 0) {
                                                queue1.add(temp[0]);
                                            }

                                        }

                                    }

                                }
                                if (queue3.isEmpty()&&queue2.isEmpty() && queue1.isEmpty() && allUsed[0] == 0 && i[0] + 1 < sortedArival.size()) {
                                    i[0]++;
                                    while (i[0] < sortedArival.size() && sortedArival.get(i[0]).getArrivalTime() > currentTime[0]) {
                                        currentTime[0]++;

                                    }
                                    queue1.add(sortedArival.get(i[0]));
                                    while (i[0] + 1 < sortedArival.size() && sortedArival.get(i[0] + 1).getArrivalTime() <= currentTime[0]) {
                                        i[0]++;
                                        queue1.add(sortedArival.get(i[0]));
                                        System.out.println("NEW PROCESS ARRIVED--> GOING BACK TO QUEUE1 !");

                                    }
                                }
                                Thread.sleep(500);
                                ///////////////////////////////Queue 2 start here //////////////////////////////////////
                                while (queue1.isEmpty()) {

                                    while (!queue2.isEmpty() && (queue2.get(0).getBurst_no() * 2) - 1 > queue2.get(0).getCPU_index() &&
                                            !queue2.get(0).getStatus(queue2.get(0).getCPU_index()).equalsIgnoreCase("Finished")) {

                                        if (queue2.get(0).getBursts(queue2.get(0).getCPU_index()) <= q2 && queue2.get(0).getCounter_in() < 10 * q2) {
                                            Thread.sleep(1000);
                                            System.out.print("time start at  " + currentTime[0]);
                                            onCPU.add(new OnCPU(queue2.get(0).getPID(), currentTime[0], currentTime[0] + q2,queue2.get(0).getCPU_index()));
                                            queue2.get(0).setStatus(queue2.get(0).getCPU_index(), "Finished");
                                            currentTime[0] += queue2.get(0).getBursts(queue2.get(0).getCPU_index());
                                            queue2.get(0).setBursts(queue2.get(0).getCPU_index(), 0);
                                            queue2.get(0).setWattingTime(currentTime[0]-queue2.get(0).getOrginBurst(queue2.get(0).getCPU_index())-queue2.get(0).getArrivalTime());
                                            queue2.get(0).setTurnArroundTime(currentTime[0]-queue2.get(0).getArrivalTime());
                                            totalWaitingTime+=queue2.get(0).getWattingTime();
                                            totalTurnaroundTime+=queue2.get(0).getTurnArroundTime();
                                            temp[0] = queue2.get(0);
                                            finished.add(temp[0]);

                                            if (queue2.get(0).getIO_index() < temp[0].getBurst_no() * 2 - 1 && queue2.get(0).getBursts(temp[0].getIO_index()) != 0) { //check if we have IO
                                                Multithread mythread = new Multithread(temp[0], queue2.get(0).getIO_index());  //creat multithreading to deal with the IO
                                                Thread thread = new Thread(mythread);
                                                thread.start();
                                                if ((queue2.get(0).getBurst_no() * 2) - 1 > queue2.get(0).getIO_index() + 2) {
                                                    queue2.get(0).setIO_index(queue2.get(0).getIO_index() + 2);
                                                }
                                            }
                                            System.out.println(" and finnish at  :" + currentTime[0] + " at q2 for process l " + queue2.get(0).getPID() + "  status is : " +
                                                    queue2.get(0).getStatus(queue2.get(0).getCPU_index()) + " index of burst " + queue2.get(0).getCPU_index());
                                            if ((queue2.get(0).getBurst_no() * 2) - 1 >= queue2.get(0).getCPU_index() + 2) {
                                                queue2.get(0).setCPU_index(queue2.get(0).getCPU_index() + 2);
                                                queue2.get(0).setCounter_in(0);
                                            }
                                            queue2.remove(0);
                                            Thread.sleep(500);
                                            if (temp[0].getBursts((temp[0].getBurst_no() * 2) - 2) != 0) {
                                                queue2.add(temp[0]);
                                            }


                                        } else {


                                            if (queue2.get(0).getCounter_in() >= 10 * q2) {
                                                queue2.get(0).setCounter_in(0);
                                                queue3.add(queue2.get(0));
                                                System.out.println("PROCESS "+ queue2.get(0).getPID()+" SENT TO QUEUE 3 !!! " );
                                                queue2.remove(0);

                                                if (queue2.isEmpty()) {
                                                    break;
                                                }
                                            }
                                            Thread.sleep(1000);
                                            System.out.print("time start at  " + currentTime[0]);
                                            onCPU.add(new OnCPU(queue2.get(0).getPID(), currentTime[0], currentTime[0] + q2,queue2.get(0).getCPU_index()));
                                            queue2.get(0).setBursts(queue2.get(0).getCPU_index(), (queue2.get(0).getBursts(queue2.get(0).getCPU_index()) - q2));
                                            queue2.get(0).setStatus(queue2.get(0).getCPU_index(), "Working");
                                            currentTime[0] += q2;
                                            temp[0] = queue2.get(0);
                                            queue2.get(0).setCounter_in(queue2.get(0).getCounter_in() + q2);

                                            System.out.println("  and finnish at  :" + currentTime[0] + " at q2 for process ov "
                                                    + queue2.get(0).getPID() + "  status is : " + queue2.get(0).getStatus(queue2.get(0).getCPU_index()) +
                                                    "  and remaining : " + queue2.get(0).getBursts(queue2.get(0).getCPU_index()));
                                            queue2.remove(0);


                                            if (temp[0].getBursts((temp[0].getBurst_no() * 2) - 2) != 0) {
                                                queue2.add(temp[0]);
                                            }
                                            Thread.sleep(500);
                                        }
                                        if (queue1.isEmpty() && allUsed[0] == 0 && i[0] + 1 < sortedArival.size()) {

                                            while (i[0] + 1 < sortedArival.size() && sortedArival.get(i[0] + 1).getArrivalTime() <= currentTime[0]) {
                                                i[0]++;
                                                queue1.add(sortedArival.get(i[0]));
                                                System.out.println("NEW PROCESS ARRIVED--> GOING BACK TO QUEUE1 !");
                                            }
                                        }
                                        if (!queue1.isEmpty()) {
                                            break;
                                        }
                                    }
                                    if (queue2.isEmpty()) {
                                        break;
                                    }
                                    if (!queue1.isEmpty()) {
                                        break;
                                    }
                                }
                                if (queue2.isEmpty()&&queue3.isEmpty() && queue1.isEmpty() && allUsed[0] == 0 && i[0] + 1 < sortedArival.size()) {
                                    i[0]++;
                                    while (i[0] < sortedArival.size() && sortedArival.get(i[0]).getArrivalTime() > currentTime[0]) {
                                        currentTime[0]++;


                                    }
                                    queue1.add(sortedArival.get(i[0]));
                                    while (i[0] + 1 < sortedArival.size() && sortedArival.get(i[0] + 1).getArrivalTime() <= currentTime[0]) {
                                        i[0]++;
                                        queue1.add(sortedArival.get(i[0]));
                                        System.out.println("NEW PROCESS ARRIVED--> GOING BACK TO QUEUE1 !");

                                    }
                                }

                                Thread.sleep(500);
                                ////////////////////Queue3 start here///////////////////
                                while (queue2.isEmpty() && queue1.isEmpty()) {
                                    while (!queue3.isEmpty() && (queue3.get(0).getBurst_no() * 2) - 1 > queue3.get(0).getCPU_index() &&
                                            !queue3.get(0).getStatus(queue3.get(0).getCPU_index()).equalsIgnoreCase("Finished")) {
                                        int T = 5;
                                        Process minimum = queue3.get(0);
                                        for (int k = 0; k < queue3.size(); k++) {
                                            for (int j = 0; j < queue3.size(); j++) {
                                                T = (int) (alpha * queue3.get(j).getBursts(queue3.get(j).getCPU_index()) + (1 - alpha) * T);
                                                queue3.get(j).setEstimatedValue(T);

                                            }
                                            if (minimum.getEstimatedValue() >= queue3.get(k).getEstimatedValue()) {
                                                minimum = queue3.get(k);

                                                if (minimum.getBursts(minimum.getCPU_index()) != 0) {
                                                    if (sortedArival.size() > i[0] + 1 && sortedArival.get(i[0] + 1).getArrivalTime() <= currentTime[0] + minimum.getBursts(minimum.getCPU_index())) {
                                                        minimum.setExit_no(minimum.getExit_no() + 1);
                                                        if (sortedArival.get(i[0] + 1).getArrivalTime() <= currentTime[0]) {
                                                            break;
                                                        }

                                                    }
                                                    if (minimum.getExit_no() == 3) {
                                                        minimum.setExit_no(0);
                                                        queue4.add(minimum);
                                                        System.out.println("PROCESS" + minimum.getPID()+" SENT TO QUEUE 4 !!!");
                                                        queue3.remove(k);

                                                        if (queue3.isEmpty()) {
                                                            break;
                                                        }
                                                    } else {Thread.sleep(1000);
                                                        System.out.print("time start at  " + currentTime[0]);
                                                        onCPU.add(new OnCPU(minimum.getPID(), currentTime[0], currentTime[0] + minimum.getBursts(minimum.getCPU_index()),minimum.getCPU_index()));
                                                        minimum.setStatus(minimum.getCPU_index(), "Finished");
                                                        currentTime[0] += minimum.getBursts(minimum.getCPU_index());
                                                        minimum.setBursts(minimum.getCPU_index(), 0);
                                                        minimum.setWattingTime(currentTime[0]-minimum.getOrginBurst(minimum.getCPU_index())-minimum.getArrivalTime());
                                                        minimum.setTurnArroundTime(currentTime[0]-minimum.getArrivalTime());
                                                        totalWaitingTime+=minimum.getWattingTime();
                                                        totalTurnaroundTime+=minimum.getTurnArroundTime();


                                                        temp[0] = minimum;
                                                        finished.add(temp[0]);
                                                        if (minimum.getIO_index() < minimum.getBurst_no() * 2 - 1 && minimum.getBursts(minimum.getIO_index()) != 0) { //check if we have IO
                                                            Multithread mythread = new Multithread(minimum, minimum.getIO_index());  //creat multithreading to deal with the IO
                                                            Thread thread = new Thread(mythread);
                                                            thread.start();
                                                            if ((minimum.getBurst_no() * 2) - 1 > minimum.getIO_index() + 2) {
                                                                minimum.setIO_index(minimum.getIO_index() + 2);
                                                            }
                                                        }
                                                        System.out.println(" and finnish at  :" + currentTime[0] + " at q3 for process l " + minimum.getPID() + "  status is : " +
                                                                minimum.getStatus(minimum.getCPU_index()) + " index of burst " + minimum.getCPU_index());
                                                        if ((minimum.getBurst_no() * 2) - 1 >= minimum.getCPU_index() + 2) {
                                                            minimum.setCPU_index(minimum.getCPU_index() + 2);
                                                            minimum.setCounter_in(0);
                                                        }
                                                        queue3.remove(k);
                                                        Thread.sleep(500);
                                                        if (temp[0].getBursts((temp[0].getBurst_no() * 2) - 2) != 0) {
                                                            queue3.add(temp[0]);
                                                        }

                                                    }

                                                }



                                            }
                                            if (queue1.isEmpty() && allUsed[0] == 0 && i[0] + 1 < sortedArival.size()) {
                                                while (i[0] + 1 < sortedArival.size() && sortedArival.get(i[0] + 1).getArrivalTime() <= currentTime[0]) {
                                                    i[0]++;
                                                    queue1.add(sortedArival.get(i[0]));
                                                    System.out.println("NEW PROCESS ARRIVED--> GOING BACK TO QUEUE1 !");

                                                }
                                            }
                                            if (!queue1.isEmpty()) {
                                                break;
                                            }
                                        }
                                        if (queue1.isEmpty() && allUsed[0] == 0 && i[0] + 1 < sortedArival.size()) {
                                            while (i[0] + 1 < sortedArival.size() && sortedArival.get(i[0] + 1).getArrivalTime() <= currentTime[0]) {
                                                i[0]++;
                                                queue1.add(sortedArival.get(i[0]));

                                            }
                                        }
                                        if (!queue1.isEmpty()) {
                                            break;
                                        }
                                    }
                                    if (queue3.isEmpty()) {
                                        break;
                                    }

                                }
                                Thread.sleep(500);
                                if (queue3.isEmpty()&&queue4.isEmpty() && queue1.isEmpty() && allUsed[0] == 0 && i[0] + 1 < sortedArival.size()) {
                                    i[0]++;
                                    while (i[0] < sortedArival.size() && sortedArival.get(i[0]).getArrivalTime() > currentTime[0]) {
                                        currentTime[0]++;


                                    }
                                    System.out.println("Timer added until "+currentTime[0]  +" to get the process !");
                                    System.out.println("");
                                    queue1.add(sortedArival.get(i[0]));
                                    while (i[0] + 1 < sortedArival.size() && sortedArival.get(i[0] + 1).getArrivalTime() <= currentTime[0]) {
                                        i[0]++;
                                        queue1.add(sortedArival.get(i[0]));

                                    }
                                }
///////////////////////////////////Queue 4 start/////////////////////////////
                                while (queue3.isEmpty() && queue1.isEmpty()) {
                                    while (!queue4.isEmpty() && (queue4.get(0).getBurst_no() * 2) - 1 > queue4.get(0).getCPU_index() &&
                                            !queue4.get(0).getStatus(queue4.get(0).getCPU_index()).equalsIgnoreCase("Finished")) {

                                        Thread.sleep(1000);
                                        System.out.print("time start at  " + currentTime[0]);
                                        onCPU.add(new OnCPU(queue4.get(0).getPID(), currentTime[0], currentTime[0] + queue4.get(0).getBursts(queue4.get(0).getCPU_index()),queue4.get(0).getCPU_index()));
                                        queue4.get(0).setStatus(queue4.get(0).getCPU_index(), "Finished");
                                        currentTime[0] += queue4.get(0).getBursts(queue4.get(0).getCPU_index());

                                        queue4.get(0).setBursts(queue4.get(0).getCPU_index(), 0);
                                        queue4.get(0).setWattingTime(currentTime[0]-queue4.get(0).getOrginBurst(queue4.get(0).getCPU_index())-queue4.get(0).getArrivalTime());
                                        queue4.get(0).setTurnArroundTime(currentTime[0]-queue4.get(0).getArrivalTime());
                                        totalWaitingTime+=queue4.get(0).getWattingTime();
                                        totalTurnaroundTime+=queue4.get(0).getTurnArroundTime();
                                        temp[0] = queue4.get(0);
                                        finished.add(temp[0]);

                                        if (queue4.get(0).getIO_index() < temp[0].getBurst_no() * 2 - 1 && queue4.get(0).getBursts(temp[0].getIO_index()) != 0) { //check if we have IO
                                            Multithread mythread = new Multithread(temp[0], queue4.get(0).getIO_index());  //creat multithreading to deal with the IO
                                            Thread thread = new Thread(mythread);
                                            thread.start();
                                            if ((queue4.get(0).getBurst_no() * 2) - 1 > queue4.get(0).getIO_index() + 2) {
                                                queue4.get(0).setIO_index(queue4.get(0).getIO_index() + 2);
                                            }
                                        }
                                        System.out.println(" and finish at  :" + currentTime[0] + " at q4 for process l " + queue4.get(0).getPID() + "  status is : " +
                                                queue4.get(0).getStatus(queue4.get(0).getCPU_index()) + " index of burst " + queue4.get(0).getCPU_index());
                                        if ((queue4.get(0).getBurst_no() * 2) - 1 >= queue4.get(0).getCPU_index() + 2) {
                                            queue4.get(0).setCPU_index(queue4.get(0).getCPU_index() + 2);
                                            queue4.get(0).setCounter_in(0);
                                        }
                                        if (queue4.get(0).getBursts(queue4.get(0).getCPU_index()) == 0) {
                                            queue4.remove(0);
                                        }Thread.sleep(1000);
                                        if (queue4.isEmpty()) {
                                            break;
                                        }
                                    }

                                    if (queue1.isEmpty() && allUsed[0] == 0 && i[0] + 1 < sortedArival.size()) {

                                        while (i[0] + 1 < sortedArival.size() && sortedArival.get(i[0] + 1).getArrivalTime() <= currentTime[0]) {
                                            i[0]++;
                                            queue1.add(sortedArival.get(i[0]));
                                            System.out.println("NEW PROCESS ARRIVED--> GOING BACK TO QUEUE1 !");

                                        }
                                    }
                                    if (queue1.isEmpty()) {
                                        break;
                                    }


                                }


                                if (queue4.isEmpty()&&queue3.isEmpty()&&queue2.isEmpty()&&queue1.isEmpty() && allUsed[0] == 0 && i[0] + 1 < sortedArival.size()) {
                                    i[0]++;
                                    while (i[0] < sortedArival.size() && sortedArival.get(i[0]).getArrivalTime() > currentTime[0]) {
                                        currentTime[0]++;


                                    }
                                    queue1.add(sortedArival.get(i[0]));
                                    while (i[0] + 1 < sortedArival.size() && sortedArival.get(i[0] + 1).getArrivalTime() <= currentTime[0]) {
                                        i[0]++;
                                        queue1.add(sortedArival.get(i[0]));
                                        System.out.println("NEW PROCESS ARRIVED--> GOING BACK TO QUEUE1 !");

                                    }
                                }



                            }






                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }).start();
            } else if (command.equals("t")) {
                running = false;

                if(!queue1.isEmpty()){
                    for (int x = 0; x < queue1.size(); x++) {
                        System.out.print(queue1.get(x).getPID() + "\t");
                        System.out.print(queue1.get(x).getArrivalTime() + "\t");

                        for (int j = 0; j < (queue1.get(x).getBurst_no() * 2) - 1; j++) {
                            System.out.print(queue1.get(x).getBursts(j) + "\t");

                        }
                        System.out.println("");

                    }

                }
                else
                    System.out.println("Queue1 is empty!.");
                // show Queue2
                if(!queue2.isEmpty()){
                    for (int x = 0; x < queue2.size(); x++) {
                        System.out.print(queue2.get(x).getPID() + "\t");
                        System.out.print(queue2.get(x).getArrivalTime() + "\t");

                        for (int j = 0; j < (queue2.get(x).getBurst_no() * 2) - 1; j++) {
                            System.out.print(queue2.get(x).getBursts(j) + "\t");

                        }
                        System.out.println("");

                    }

                }
                else
                    System.out.println("Queue2 is empty!.");



                if(!queue3.isEmpty()){
                    for (int x = 0; x < queue3.size(); x++) {
                        System.out.print(queue3.get(x).getPID() + "\t");
                        System.out.print(queue3.get(x).getArrivalTime() + "\t");

                        for (int j = 0; j < (queue3.get(x).getBurst_no() * 2) - 1; j++) {
                            System.out.print(queue3.get(x).getBursts(j) + "\t");

                        }
                        System.out.println("");

                    }

                }
                else
                    System.out.println("Queue3 is empty!.");


                if(!queue4.isEmpty()){
                    for (int x = 0; x < queue4.size(); x++) {
                        System.out.print(queue4.get(x).getPID() + "\t");
                        System.out.print(queue4.get(x).getArrivalTime() + "\t");

                        for (int j = 0; j < (queue4.get(x).getBurst_no() * 2) - 1; j++) {
                            System.out.print(queue4.get(x).getBursts(j) + "\t");

                        }
                        System.out.println("");

                    }

                }
                else {
                    System.out.println("Queue4 is empty!.");
                }
                try {
                    Thread.sleep(2000);
                    Thread.sleep(2000);
                }catch (InterruptedException e) {
                    break;
                }

            } else if (command.equals("e")) {
                break;
            }
        }


        System.out.println("All processes are done !!");

        System.out.println("=====================================================================================================================================================================" );

        System.out.println("Start time (s)   /  ProcessNumber,CPU burst index   / Finish time (f)");
        System.out.print("\nGantt Chart :  " );
        for (int x = 0; x < onCPU.size(); x++) {
            System.out.print("s:" + onCPU.get(x).getStartTime() + "\t\tp" + onCPU.get(x).getPID()+",b"+onCPU.get(x).getBurstIndex() + "\t\t f:" + onCPU.get(x).getFinishTime() + " | ");
        }
        System.out.println();
        System.out.print("=====================================================================================================================================================================" );


        double cpuUtilization = (double)(currentTime[0] - totalWaitingTime) * 100 / currentTime[0];
        double avgWaitingTime = (double)totalWaitingTime / processes.size();
        double avgTurnaroundTime =(double) totalTurnaroundTime / processes.size();
        System.out.println("====================================================\n" );
        System.out.printf("CPU Utilization = %.2f ",cpuUtilization);
        System.out.print("%");
        System.out.println();
        System.out.printf("avg WaitingTime = %.2f",avgWaitingTime);
        System.out.println();
        System.out.printf("avgTurnaroundTime = %.2f",avgTurnaroundTime);
        System.out.println();
        System.out.println("====================================================\n" );





        input.close();
    }
    public static void Workload_Generator(int processNO) {

        System.out.println("Enter max arrival time:\n");
        int maxArrivalTime = in.nextInt();

        System.out.println("Enter max number of CPU burst:\n");
        int maxCpuBurst = in.nextInt();
        int cpuBurst;
        int maxCPU;
        int minCPU;
        int maxIO;
        int minIO;
        System.out.println("Enter max CPU time burst:\n");
        maxCPU = in.nextInt();

        System.out.println("Enter min CPU time burst:\n");
        minCPU = in.nextInt();

        System.out.println("Enter max IO time burst:\n");
        maxIO = in.nextInt();

        System.out.println("Enter min IO time burst:\n");
        minIO = in.nextInt();
        String [] status ;
        for (int i = 0; i < processNO; i++) {

            int PID = i;
            int arriveTime = printRandoms(0, maxArrivalTime);
            int cpuBurstNo = printRandoms(1, maxCpuBurst);
            int cpuIObursts[];
            int orginburst[];
            int counter=0;
            cpuIObursts = new int[(cpuBurstNo *2)-1];
            for (int j = 0; j < cpuBurstNo * 2 ; j++) {
                cpuIObursts[j] = printRandoms(minCPU, maxCPU);
                j++;
                if(j!=(cpuBurstNo*2)-1){
                    cpuIObursts[j] = printRandoms(minIO, maxIO);

                }
            }
            orginburst = new int[(cpuBurstNo *2)-1];
            for (int j = 0; j < cpuBurstNo * 2-1 ; j++) {
                orginburst[j]=cpuIObursts[j];

            }


            status=new String[(cpuBurstNo * 2)-1];
            for (int j = 0; j < (cpuBurstNo * 2 )-1; j++) {
                status[j]="NotFinished";

            }
            processes.add(new Process(PID, arriveTime, cpuBurstNo, cpuIObursts,orginburst,status));


        }

        try {
            FileWriter writerObj = new FileWriter("semulator.txt", false);

            for (int i = 0; i < processes.size(); i++) {
                writerObj.write(processes.get(i).getPID() + ",");
                writerObj.write(processes.get(i).getArrivalTime() + ",");

                for (int j = 0; j < (processes.get(i).getBurst_no() * 2) - 1; j++) {

                    writerObj.write(processes.get(i).getBursts(j) + ",");

                }
                writerObj.write("\n");

            }
            writerObj.close();
            System.out.println("================================\n" + "File successfully overwritten.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        processes.removeAll(processes);

    }

    public static int printRandoms(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static void readFile(){
        String []status;
        String fileName = "semulator.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int[] values = new int[parts.length-2];
                int[] orginvalues = new int[parts.length-2];
                int ind=0;
                for (int i = 2; i < parts.length; i++) {
                    values[ind] = Integer.parseInt(parts[i]);
                    ind++;
                }
                ind=0;
                for (int i = 2; i < parts.length; i++) {
                    orginvalues[ind] = Integer.parseInt(parts[i]);
                    ind++;
                }
                status=new String[values.length];
                for (int j = 0; j < values.length; j++) {
                    status[j]="NotFinished";

                }
                processes.add(new Process(Integer.parseInt(parts[0]),Integer.parseInt(parts[1]),(values.length+1)/2, values,orginvalues,status));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

    }

}
