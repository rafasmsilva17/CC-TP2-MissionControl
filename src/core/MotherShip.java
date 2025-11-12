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
        if(!roversAddresses.containsKey(roverID)){
            System.out.println("This rover is not registered! Stopping mission assignment.");
            return;
        }
        if (roverMissions.containsKey(mission.id)) {
            System.out.println("Attempt to assign previously assigned mission. Stopping!");
            return;
        }
        allMissions.put(mission.id, mission); // Isto não deve ficar aqui no final
        roverMissions.put(mission.id, roverID);
        missionLinkServer.assignMission(roverID, roversAddresses.get(roverID), mission);
        System.out.println("Mission sent to rover");
    }
}
