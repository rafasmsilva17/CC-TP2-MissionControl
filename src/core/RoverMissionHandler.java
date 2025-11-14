package core;

import comms.missionlink.RoverServer;
import core.missions.Mission;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RoverMissionHandler extends Thread{
    Rover parentRover;
    private final PriorityQueue<Mission> priorityQueue =
            new PriorityQueue<>(Comparator.comparingInt((Mission m) -> m.priority.toInteger()).reversed());
    private Mission currentMission = null;
    private final Lock missionQueueLock = new ReentrantLock();


    public RoverMissionHandler(Rover parent){
        parentRover = parent;
    }

    public Mission getCurrMission(){ return currentMission; }

    public boolean hasMission(Mission mission){
        if (currentMission == null) return priorityQueue.contains(mission);
        return priorityQueue.contains(mission) || currentMission.equals(mission);
    }

    public void addMission(Mission mission){
        missionQueueLock.lock();
        priorityQueue.add(mission);
        missionQueueLock.unlock();
    }


    private void requestMission(){
        RoverServer server = parentRover.getServer();
        server.sendMissionRequest();
    }

    private void doMission(){
        while (currentMission == null) currentMission = priorityQueue.poll();
        // Fazer missão de alguma forma
        currentMission.start(parentRover.getId());
        while(currentMission.isActive()){
            System.out.println("Doing mission " + currentMission.id);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void run(){
        while(true){
            // Ficar a pedir missão enquanto não tem missão.
            while(true){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                missionQueueLock.lock();
                boolean isEmpty = (priorityQueue.isEmpty() && currentMission == null);
                missionQueueLock.unlock();

                if(!isEmpty) break;
                requestMission();
            }
            System.out.println("a");
            doMission();
            // Acabou missao
        }
    }

}
