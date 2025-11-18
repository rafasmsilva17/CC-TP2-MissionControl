package comms.missionlink;

import comms.Encoder;
import comms.packets.*;
import comms.packets.common.PacketType;
import comms.telemetry.MissionTelemetry;
import core.Rover;
import core.missions.Mission;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RoverServer extends Thread implements UDPServerLogic{
    private final UDPServer uServer;
    public static final String mothershipName = "localhost";
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


    public void sendMissionTelemetry(Mission mission){
        MissionTelemetry telem = mission.getTelemetry();
        if (telem == null){
            System.out.println("Trying to send telemetry for a nonexistent mission type?");
            return;
        }
        try {
            MissionTelemetryPacket telemPacket = new MissionTelemetryPacket(telem);
            DatagramPacket packet = new DatagramPacket(telemPacket.getBuffer(),
                    telemPacket.getBuffer().length,
                    InetAddress.getByName(mothershipName),
                    mothershipPort);
            // Manda negativo para distinguir dos pacotes dos rovers
            uServer.sendPacket(-Integer.parseInt((String) mission.id.substring(2)),
                    packet);
            System.out.println("[" + getName() + "] Sending Mission Telemetry. Will wait for ACK");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
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
                } else if (packetT == PacketType.MISSIONCANCEL){
                    parentRover.cancelMission();
                    System.out.println("[" + getName() + "] SENT MISSION CANCEL SIGNAL");
                    uServer.sendACK(parentRover.getId(), packet.getAddress(), packet.getPort());
                } else if (packetT == PacketType.MISSION){
                    Mission mission = Mission.fromBuffer(receivedData);
                    if (mission == null) {
                        System.out.println("[" + getName() + "] Something went wrong when receiving mission!");
                        continue;
                    }
                    System.out.println("Rover received mission: " + mission);
                    parentRover.receiveMission(mission);
                    int missionID = Integer.parseInt(mission.id.substring(2));
                    int digits = missionID/10;
                    int packetID = (int)(parentRover.getId()*(Math.pow(10, digits+1))) + missionID;
                    uServer.sendACK(packetID, packet.getAddress(), packet.getPort());
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
