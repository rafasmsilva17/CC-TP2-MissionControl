package comms.missionlink;

import comms.packets.MissionPacket;
import core.missions.Mission;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class MothershipML {
    private final UDPServer uServer;

    public MothershipML(UDPServer uServer){
        this.uServer = uServer;
    }

    // TODO identifier deste pacote tbm deve conter mission ID de alguma forma
    public void assignMission(int roverID, InetAddress roverAddress, Mission mission){
        MissionPacket misPacket = new MissionPacket(mission);
        int missionID = Integer.parseInt(mission.id.substring(2));
        int digits = missionID/10;
        int packetID = (int)(roverID*(Math.pow(10, digits + 1))) + missionID;
        DatagramPacket packet =
                new DatagramPacket(misPacket.getBuffer(), misPacket.getBuffer().length,
                        roverAddress, 3000);

        uServer.sendPacket(packetID,packet);
    }
}
