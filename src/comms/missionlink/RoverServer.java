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
import java.util.Arrays;

public class RoverServer extends Thread{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[1024];
    Rover rover;

    public RoverServer(){
        try{
            socket = new DatagramSocket(3000);
        } catch (SocketException e){
            System.out.println("Error creating Rover Server socket");
        }
    }

    public void run(){
        running = true;

        while(running) {
            DatagramPacket packet =
                    new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                System.out.println("Rover Received:" + Arrays.toString(packet.getData()).trim());

                // Verificar se a missao esta correta (aqui por causa de DEBUG)
                PhotoMission mission = new PhotoMission(ByteBuffer.wrap(packet.getData()));
                System.out.println("Rover received mission: " + mission);

                ConfirmationPacket confirmation = new ConfirmationPacket(1, mission.id);

                // Mandar confirmacao? Ou esperar por telemetria
                int port = packet.getPort();
                InetAddress senderAddress = packet.getAddress();
                DatagramPacket confirmationPacket =
                        new DatagramPacket(confirmation.getBuffer(), confirmation.getBuffer().length,
                                senderAddress, 3001);

                socket.send(confirmationPacket);

            } catch (IOException e) {
                System.out.println("Rover failed to receive Packet");
            }
        }
    }
}
