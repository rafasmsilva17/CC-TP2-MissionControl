package core;

import comms.missionlink.RoverServer;
import core.missions.Mission;
import core.missions.PhotoMission;
import core.missions.common.Sample;

import java.util.Set;

public class Rover {
    private int id = -1;
    private Long awakeTime = System.currentTimeMillis();
    private Battery battery = new Battery();
    // Priority Queue com as missoes passou para o mission handler
    private Set<Sample> collectedSamples; //amostras atualmente no rover

    // TODO mission handler thread
    private final RoverMissionHandler missionHandler;
    private final RoverServer missionServer;

    public Rover(){
        missionHandler = new RoverMissionHandler(this);
        missionServer = new RoverServer(this);
        missionServer.start();
        missionServer.setName("Rover" + id + " Server");
        run();
        //missionHandler.start();
        //missionServer.sendRegistration();
    }

    public int getId(){ return id; }

    public void setId(int assignedID){
        if(id != -1) return; // so pode ser usada uma vez
        id = assignedID;
        System.out.println("Im registered as Rover " + assignedID);
        missionHandler.start(); // ja esta registado, pode começar a fazer missões
    }

    public boolean isRegistered(){
        return (id != -1);
    }

    public RoverServer getServer(){ return missionServer; }


    public void receiveMission(Mission mission){
        if(missionHandler.hasMission(mission)){
            System.out.println("[ROVER " + id + "] " + " Received duplicate mission. " +
                    "Is there packet loss?");
            return;
        }
        missionHandler.addMission(mission);
    }

    // Isto não é implementaçao de thread
    public void run(){
        Mission currentMission = null;
        long lastUpdate = -1L;
        while(true){
            currentMission = missionHandler.getCurrMission();
            if(currentMission != null){
                if (lastUpdate == -1L) lastUpdate = System.currentTimeMillis();
                if(System.currentTimeMillis() > lastUpdate + currentMission.updateInterval * 1000L){
                    missionServer.sendMissionTelemetry(currentMission);
                    System.out.println("Sent mission telemetry");
                    lastUpdate = System.currentTimeMillis() + currentMission.updateInterval * 1000L;
                }
            }

            // updateRoverStatus

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
