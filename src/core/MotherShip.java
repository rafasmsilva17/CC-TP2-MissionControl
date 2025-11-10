package core;

import comms.missionlink.MothershipML;
import comms.missionlink.MothershipServer;
import core.missions.Mission;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

public class MotherShip {
    private static final HashMap<String, Mission> allMissions = new HashMap<>();
    HashMap<Integer, InetAddress> roversAddresses = new HashMap<>();
    private static final MothershipML missionLinker = new MothershipML();
    private static MothershipServer missionLinkServer;


    public MotherShip() {
        try {
            missionLinkServer = new MothershipServer();
            missionLinkServer.start();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void reassignMissionTo(int roverID, Mission mission){
        allMissions.put(mission.id, mission);
        missionLinker.assignMission(mission);
        missionLinkServer.addToConfirmationBuffer(roverID, mission.id);
        System.out.println("Mission sent to rover");
    }

    public static void reassignMissionTo(int roverID, String missionID){
        if(!allMissions.containsKey(missionID)){
            System.out.println("Trying to reassign mission that does not exist!" +
                    "Are you sure this behaviour is intended?");
            return;
        }
        missionLinker.assignMission(allMissions.get(missionID));
        missionLinkServer.addToConfirmationBuffer(roverID, missionID);
        System.out.println("Attempting to reassign mission " + missionID + " to rover " + roverID);
    }
}
