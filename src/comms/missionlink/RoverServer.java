package comms.missionlink;

import comms.Encoder;
import comms.packets.ConfirmationPacket;
import comms.packets.MissionRequestPacket;
import comms.packets.PacketType;
import comms.packets.RegisterRoverPacket;
import core.Rover;
import core.missions.Mission;
import core.missions.PhotoMission;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RoverServer extends Thread implements UDPServerLogic{
    private final UDPServer uServer;
    private static final String mothershipName = "Mothership";
    private static final int mothershipPort = 3001;

    private byte[] buf = new byte[1024];
    private final Rover parentRover;

    public RoverServer(Rover parentRover){
        this.parentRover = parentRover;
        try {
            this.uServer = new UDPServer(3000, this);
            this.uServer.setName("Unassigned Rover");
            uServer.start();
            sendRegistration();// ficar preso aqui enquanto não for registado
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRegistrationPacket(){
        RegisterRoverPacket registration = new RegisterRoverPacket();
        try {
            DatagramPacket packet = new DatagramPacket(registration.getBuffer(),
                    registration.getBuffer().length,
                    InetAddress.getByName(mothershipName),
                    mothershipPort);
            System.out.println(InetAddress.getByName(mothershipName));
            uServer.sendPacket(packet);
            System.out.println("[ROVER SERVER] Registration sent!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRegistration(){
        while (!parentRover.isRegistered()){
            sendRegistrationPacket();
            try {
                DatagramPacket packet = packetQueue.poll(5, TimeUnit.SECONDS);
                if(packet == null) continue;

                ByteBuffer receivedData = ByteBuffer.wrap(packet.getData());
                System.out.println(Arrays.toString(receivedData.array()));
                PacketType packetT = PacketType.fromByte(Encoder.decodeByte(receivedData));
                if (packetT != PacketType.REGISTER) continue;
                int assignedID = Encoder.decodeInt(receivedData);
                parentRover.setId(assignedID);
                uServer.setName("Rover " + assignedID + " UDP");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMissionRequest(){
        MissionRequestPacket request = new MissionRequestPacket();
        try {
            // TODO mudar o hardcode do endereço da navemae
            DatagramPacket packet = new DatagramPacket(request.getBuffer(),
                    request.getBuffer().length,
                    InetAddress.getByName(mothershipName),
                    mothershipPort);

            uServer.sendPacket(packet);
            System.out.println("[ROVER SERVER] Mission request sent!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMissionTelemetry(){

    }

    public void run(){
        while(true) {
            DatagramPacket packet;
            while((packet = packetQueue.poll()) != null) {
                ByteBuffer receivedData = ByteBuffer.wrap(packet.getData());
                PacketType packetT = PacketType.fromByte(Encoder.decodeByte(receivedData));

                if(packetT == PacketType.REGISTER){
                    if (parentRover.isRegistered()){
                        System.out.println("Received register but I am already registered..." +
                                "Is this supposed to happen?");
                    }
                } else if (packetT == PacketType.MISSION){
                    PhotoMission mission = new PhotoMission(receivedData);
                    System.out.println("Rover received mission: " + mission);
                    parentRover.receiveMission(mission);

                    // Simular packet loss
                    if(ThreadLocalRandom.current().nextInt(0, 11) == 5){
                        uServer.sendACK(parentRover.getId(), packet.getAddress(), packet.getPort());
                    }
                    System.out.println("[" + getName() + "] Sent Mission ACK" );
                } else {
                    System.out.println("[" + getName() + "] Received Packet of unexpected type: " +
                            packetT);
                }
            }
        }
    }

    @Override
    public void queuePacket(DatagramPacket packet) {
        byte[] dataCopy = Arrays.copyOf(packet.getData(), packet.getLength());
        packetQueue.add(new DatagramPacket(dataCopy,
                packet.getLength(),
                packet.getAddress(),
                packet.getPort()));
    }
}
