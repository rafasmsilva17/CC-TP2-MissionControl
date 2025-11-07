package core;

import comms.MothershipML;
import comms.MothershipServer;
import core.missions.Mission;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

public class MotherShip {
    HashMap<Integer, InetAddress> roversAddresses = new HashMap<>();
    MothershipML missionLinker = new MothershipML();
    MothershipServer missionLinkServer;


    public MotherShip() {
        try {
            missionLinkServer = new MothershipServer();
            missionLinkServer.start();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void assignMissionTo(int roverID, Mission mission){
        missionLinker.assignMission(mission);
        missionLinkServer.addToConfirmationBuffer(roverID, mission.id);
        System.out.println("Mission sent to rover");
    }
}
