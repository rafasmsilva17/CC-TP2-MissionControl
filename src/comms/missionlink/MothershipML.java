package comms.missionlink;

import comms.Encoder;
import comms.packets.MissionPacket;
import core.missions.Mission;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

public class MothershipML {
    private final UDPServer uServer;

    public MothershipML(UDPServer uServer){
        this.uServer = uServer;
    }

    public void assignMission(int roverID, InetAddress roverAddress, Mission mission){
        MissionPacket misPacket = new MissionPacket(mission);
        DatagramPacket packet =
                new DatagramPacket(misPacket.getBuffer(), misPacket.getBuffer().length,
                        roverAddress, 3000);

        uServer.sendPacket(roverID ,packet);
    }
}
