# cpu-scedualing-queues
In this project you will simulate a multilevel feedback queue scheduling algorithm and report some 
performance measures on randomly generated workloads.
The input of the simulator is a file describing a workload. The file should have the structure 
described in the previous section and can be either randomly generated or given by the user.
You need to simulate a multilevel feedback queue scheduling algorithm with 4 queues:
- Queue#1: Round Robin (RR) with time quantum q1.
- Queue#2: Round Robin (RR) with time quantum q2.
- Queue#3: Shortest-remaining-time first.
- Queue#4: FCFS
The user should be able to specify the value of q1, q2 and 𝛼 that is used to predict the 
duration of next CPU burst for the shortest-remaining-time first algorithm.
