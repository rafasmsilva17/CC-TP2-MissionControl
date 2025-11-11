package core;

import comms.missionlink.RoverServer;
import core.missions.Mission;
import core.missions.common.Sample;

import java.util.PriorityQueue;
import java.util.Set;

public class Rover {
    private static int id_counter = 0;
    private int id;
    private Battery battery = new Battery();
    private PriorityQueue<Mission> priorityQueue = new PriorityQueue<>(); //necessario criar comparator ?
    private Set<Sample> collectedSamples; //amostras atualmente no rover

    private final RoverServer missionServer;

    public Rover(){
        id = id_counter++;
        missionServer = new RoverServer(id);
        missionServer.start();
    }

    public int getId(){ return id; }

}
