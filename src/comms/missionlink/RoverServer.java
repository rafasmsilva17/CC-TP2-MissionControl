package comms.missionlink;

import comms.packets.ConfirmationPacket;
import comms.packets.MissionRequestPacket;
import core.Rover;
import core.missions.PhotoMission;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RoverServer extends Thread{
    private DatagramSocket socket;
    private byte[] buf = new byte[1024];
    private final Rover parentRover;

    public RoverServer(Rover parentRover){
        this.parentRover = parentRover;
        try{
            socket = new DatagramSocket(3000);
        } catch (SocketException e){
            System.out.println("Error creating Rover Server socket");
        }
    }

    public void sendMissionRequest(){
        MissionRequestPacket request = new MissionRequestPacket();
        try {
            // TODO mudar o hardcode do endereço da navemae
            DatagramPacket packet = new DatagramPacket(request.getBuffer(),
                    request.getBuffer().length,
                    InetAddress.getByName("localhost"),
                    3001);
            socket.send(packet);
            System.out.println("[ROVER SERVER] Mission request sent!");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                parentRover.receiveMission(mission);

                ConfirmationPacket confirmation = new ConfirmationPacket(parentRover.getId(), mission.id);

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
