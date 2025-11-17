package comms.missionlink;


import comms.Encoder;
import comms.packets.MissionCancelPacket;
import comms.packets.PacketType;
import comms.packets.RegisterRoverPacket;
import comms.telemetry.MissionTelemetry;
import core.MotherShip;
import core.missions.Mission;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MothershipServer extends Thread implements UDPServerLogic{
    private final UDPServer uServer;
    private final MothershipML missionLinker;

    public MothershipServer() throws SocketException {
        this.uServer = new UDPServer(3001, this);
        System.out.println(uServer.getHostName());
        this.setName("MSUDP LogicThread");
        uServer.setName("MSUDP Thread");
        uServer.start();
        missionLinker = new MothershipML(uServer);
    }

    public void run() {
        while(true) {
            DatagramPacket packet;
            while((packet = packetQueue.poll()) != null){
                ByteBuffer receivedData = ByteBuffer.wrap(packet.getData());
                PacketType packetT = PacketType.fromByte(Encoder.decodeByte(receivedData));
                if (packetT == PacketType.REQUEST) {
                    // se for request de missão, mandar missão e passa para o proximo
                    System.out.println("[MOTHERSHIP] Received mission request from " +
                            packet.getAddress() + ":" + packet.getPort());

                } else if (packetT == PacketType.MISSIONTELEMETRY){
                    System.out.println("Received mission telemetry!");
                    MissionTelemetry telemetry = MissionTelemetry.fromBuffer(receivedData);
                    if (telemetry == null) {
                        System.out.println("[" + getName() + "] Received null telemetry");
                    }
                    System.out.println(telemetry);
                    uServer.sendACK(-Integer.parseInt(telemetry.id.substring(2)), packet.getAddress(), packet.getPort());
                    MotherShip.handleMissionTelemetry(telemetry);
                    // TODO mandar telemetry para o HTTP

                } else if (packetT == PacketType.REGISTER) {
                    // registo que o rover manda assim que é iniciado
                    int assignRoverID;
                    if (MotherShip.isRoverRegistered(packet.getAddress())){
                        // Caso ja esteja registado, mas o rover não recebeu
                        assignRoverID = MotherShip.getRoverID(packet.getAddress());
                    } else {
                        assignRoverID = MotherShip.assignRoverID();
                    }
                    System.out.println("Received register from " + assignRoverID);
                    MotherShip.registerRover(assignRoverID, packet.getAddress());
                    RegisterRoverPacket idAssignPacket = new RegisterRoverPacket(assignRoverID);
                    uServer.sendPacket(new DatagramPacket(idAssignPacket.getBuffer(),
                            idAssignPacket.getBuffer().length,
                            packet.getAddress(),
                            packet.getPort()));

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

    public void assignMission(int roverID, InetAddress roverAddress, Mission mission){
        missionLinker.assignMission(roverID, roverAddress, mission);
    }

    public void sendCancelMission(int roverID, InetAddress address){
        MissionCancelPacket missionCancel = new MissionCancelPacket();
        DatagramPacket packet = new DatagramPacket(missionCancel.getBuffer(),
                missionCancel.getBuffer().length,
                address,
                3000);
        uServer.sendPacket(roverID, packet);
    }
}
