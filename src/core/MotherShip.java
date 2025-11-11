package core;

import comms.missionlink.MothershipML;
import comms.missionlink.MothershipServer;
import core.missions.Mission;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

public class MotherShip {
    // Mapa {idMissao, Missao}
    private static final HashMap<String, Mission> allMissions = new HashMap<>();

    // Mapa {idMissao , idRover}
    private static final HashMap<String, Integer> roverMissions = new HashMap<>();

    private static final HashMap<Integer, InetAddress> roversAddresses = new HashMap<>();
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

    public static void registerRover(int roverID, InetAddress address){
        roversAddresses.put(roverID, address);
        System.out.println("[MOTHERSHIP] Registered rover " + roverID);
    }


    public void assignMissionTo(int roverID, Mission mission) {
        if (roverMissions.containsKey(mission.id)) {
            System.out.println("Attempt to assign previously assigned mission. Stopping!");
            return;
        }
        allMissions.put(mission.id, mission); // Isto não deve ficar aqui no final
        roverMissions.put(mission.id, roverID);
        missionLinker.assignMission(roverID, mission);
        missionLinkServer.addToConfirmationBuffer(roverID, mission.id);
        System.out.println("Mission sent to rover");
    }

    public static void reassignMissionTo(int roverID, String missionID){
        if(!allMissions.containsKey(missionID)){
            System.out.println("Trying to reassign mission that does not exist!" +
                    "Are you sure this behaviour is intended?");
            return;
        }
        missionLinker.assignMission(roverMissions.get(missionID), allMissions.get(missionID));
        missionLinkServer.addToConfirmationBuffer(roverID, missionID);
        System.out.println("Attempting to reassign mission " + missionID + " to rover " + roverID);
    }
}
