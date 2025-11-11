package comms.missionlink;

import comms.packets.ConfirmationPacket;
import core.Rover;
import core.missions.PhotoMission;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RoverServer extends Thread{
    private DatagramSocket socket;
    private byte[] buf = new byte[1024];
    private final int parentRover;

    public RoverServer(int parentRover){
        this.parentRover = parentRover;
        try{
            socket = new DatagramSocket(3000);
        } catch (SocketException e){
            System.out.println("Error creating Rover Server socket");
        }
    }

    public void run(){
        while(true) {
            DatagramPacket packet =
                    new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                //System.out.println("Rover Received:" + Arrays.toString(packet.getData()).trim());

                // Verificar se a missao esta correta (aqui por causa de DEBUG)
                PhotoMission mission = new PhotoMission(ByteBuffer.wrap(packet.getData()));
                System.out.println("Rover received mission: " + mission);

                ConfirmationPacket confirmation = new ConfirmationPacket(parentRover, mission.id);

                int port = packet.getPort();
                InetAddress senderAddress = packet.getAddress();
                DatagramPacket confirmationPacket =
                        new DatagramPacket(confirmation.getBuffer(), confirmation.getBuffer().length,
                                senderAddress, 3001);

                System.out.println("[ROVER SERVER] Sending UDP ACK");

                // Simular packet loss
                if(ThreadLocalRandom.current().nextInt(0, 11) == 5){
                    socket.send(confirmationPacket);

                }
            } catch (IOException e) {
                System.out.println("Rover failed to receive Packet");
            }
        }
    }
}
