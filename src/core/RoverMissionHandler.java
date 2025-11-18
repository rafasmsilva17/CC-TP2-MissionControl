package core;

import comms.missionlink.RoverServer;
import core.missions.Mission;
import core.missions.common.MissionStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RoverMissionHandler extends Thread{
    Rover parentRover;
    private final PriorityQueue<Mission> priorityQueue =
            new PriorityQueue<>(Comparator.comparingInt((Mission m) -> m.priority.toInteger()).reversed());
    private Mission currentMission = null;
    private Mission lastFinishedMission = null;
    private final Lock missionQueueLock = new ReentrantLock();
    private final Lock cancelLock = new ReentrantLock();

    public RoverMissionHandler(Rover parent){
        parentRover = parent;
        setName("Rover " + parentRover.getId() + " Mission Handler");
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

    public void cancelMission(){
        if (currentMission == null){
            System.out.println("No mission in progress! Cannot cancel");
            return;
        }
        currentMission.cancel();
    }

    private void doMission(){
        while (currentMission == null) currentMission = priorityQueue.poll();
        // Fazer missão de alguma forma
        currentMission.start(parentRover.getId());
        while(currentMission.isActive()){
            System.out.println("Doing mission " + currentMission.id);
            boolean finished = currentMission.executeMission(parentRover);
            if (finished){
                finishMission();
                return;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Missao foi cancelada
            if (currentMission.status == MissionStatus.CANCELED){
                queueCancelTelem();
                return;
            }
        }
    }

    private void finishMission(){
        currentMission.finish();
        lastFinishedMission = currentMission;
        currentMission = null;
        parentRover.notifyMissionFinish();
        System.out.println("[" + getName() + "] Finished mission!");
    }

    private void queueCancelTelem(){
        lastFinishedMission = currentMission;
        currentMission = null;
        parentRover.notifyMissionFinish();
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
            doMission();
            // Acabou missao
        }
    }

    public Mission getLastFinishedMission() {
        return lastFinishedMission;
    }

    public void setLastFinishedMission(Mission lastFinishedMission) {
        this.lastFinishedMission = lastFinishedMission;
    }

    public List<String> missionBuffer(){
        return new ArrayList<>(priorityQueue.stream().map(mission -> mission.id).toList());
    }

    public Mission getCurrentMission(){
        return currentMission;
    }
}
