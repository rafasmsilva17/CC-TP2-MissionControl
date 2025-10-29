package comms;

import core.missions.Mission;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class MothershipML {
    private DatagramSocket socket;

    public MothershipML(){
        try {
            socket = new DatagramSocket(4445);
        } catch (SocketException e){
            System.out.println("Mothership failed to create ML socket");
        }
    }

    public void assignMission(Mission mission){
        ByteBuffer buf = Encoder.encodeMission(mission);

        try {
            DatagramPacket packet =
                    new DatagramPacket(buf.array(), buf.array().length,
                            InetAddress.getByName("localhost"), 3000);

            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
