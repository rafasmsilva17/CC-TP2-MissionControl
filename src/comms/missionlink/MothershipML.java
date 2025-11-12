package comms.missionlink;

import comms.Encoder;
import core.missions.Mission;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MothershipML {
    private final UDPServer uServer;

    public MothershipML(UDPServer uServer){
        this.uServer = uServer;
    }

    public void assignMission(int roverID, InetAddress roverAddress, Mission mission){
        byte[] buf = Encoder.encodeMission(mission);

        DatagramPacket packet =
                new DatagramPacket(buf, buf.length,
                        roverAddress, 3000);

        uServer.sendPacket(roverID ,packet);
    }
}
