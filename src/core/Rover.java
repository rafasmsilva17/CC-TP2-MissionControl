package core;

import comms.missionlink.RoverServer;
import core.missions.Mission;
import core.missions.PhotoMission;
import core.missions.common.Sample;

import java.util.Set;

public class Rover {
    private static int id_counter = 0;
    private final int id;
    private Battery battery = new Battery();
    // Priority Queue com as missoes passou para o mission handler
    private Set<Sample> collectedSamples; //amostras atualmente no rover

    // TODO mission handler thread
    private final RoverMissionHandler missionHandler;
    private final RoverServer missionServer;

    public Rover(){
        id = id_counter++;
        missionHandler = new RoverMissionHandler(this);
        missionServer = new RoverServer(this);
        missionServer.start();
        missionHandler.start();
        missionServer.sendRegistration();
    }

    public int getId(){ return id; }

    public RoverServer getServer(){ return missionServer; }


    public void receiveMission(Mission mission){
        if(missionHandler.hasMission(mission)){
            System.out.println("[ROVER " + id + "] " + " Received duplicate mission. " +
                    "Is there packet loss?");
            return;
        }
        missionHandler.addMission(mission);
    }

}
