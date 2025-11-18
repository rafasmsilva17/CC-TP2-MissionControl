package core;

import comms.Encoder;
import comms.gc.MsHTTP;
import comms.missionlink.MothershipServer;
import comms.packets.common.PacketType;
import comms.rovertelemetry.RoverTelemetry;
import comms.tcp.MothershipTCPServer;
import comms.telemetry.MissionTelemetry;
import core.missions.*;
import core.missions.common.Coordinate;
import core.missions.common.Priority;

import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MotherShip {
    public static final int TCPSERVER_PORT = 6666;

    // Mapa {idMissao, Missao}
    private static final HashMap<String, Mission> allMissions = new HashMap<>();
    // Mapa {idMissao , idRover}
    private static final HashMap<String, Integer> roverMissions = new HashMap<>();
    private static final HashMap<Integer, InetAddress> roversAddresses = new HashMap<>();

    private static MothershipServer missionLinkServer;
    private static MothershipTCPServer tcpServer;
    private static MsHTTP serverHTTP;
    private static int roverIDCounter = 1;

    public MotherShip() {
        try {
            missionLinkServer = new MothershipServer();
            tcpServer = new MothershipTCPServer(TCPSERVER_PORT);
            serverHTTP = new MsHTTP();
            missionLinkServer.start();
            tcpServer.start();
            run();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    // Main Thread
    public void run(){

        // A fazer trabalho de main aqui!
        Coordinate coord = new Coordinate(10.54f, 10.64f);
        PhotoMission miss = new PhotoMission(coord, 1, 2, 60 * 10, Priority.URGENT);
        GetSampleMission miss1 = new GetSampleMission(coord, 2, 5.0f, 600, Priority.URGENT);
        AnaliseSampleMission miss11 = new AnaliseSampleMission("1-1");
        VideoMission miss2 = new VideoMission(coord, 1, 60);
        AnaliseAtmosphereMission miss3 = new AnaliseAtmosphereMission(coord, 60 * 10, Priority.NORMAL);
        try {
            Thread.sleep(10000); // iniciar o servidor do rover durante este tempo
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.assignMissionTo(1, miss3);
        this.assignMissionTo(1, miss);
        this.assignMissionTo(1, miss1);

        // True
        while(true){
            ArrayList<ByteBuffer> rTelemPackets = (ArrayList<ByteBuffer>) tcpServer.flushPackets();
            rTelemPackets.forEach(buffer -> {
                RoverTelemetry rTelem = new RoverTelemetry(buffer);
                serverHTTP.addRoverTelemetry(rTelem);
            });
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public static int assignRoverID(){
        return roverIDCounter++;
    }

    public static boolean isRoverRegistered(InetAddress address){
        return roversAddresses.containsValue(address);
    }

    public static int getRoverID(InetAddress address){
        if(!isRoverRegistered(address)) return -1;
        AtomicInteger roverID = new AtomicInteger();
        roversAddresses.forEach((id, add) -> {
            if(add == address){
                roverID.set(id);
            }
        });
        return roverID.get();
    }

    public static void registerRover(int roverID, InetAddress address){
        roversAddresses.put(roverID, address);
        System.out.println("[MOTHERSHIP] Registered rover " + roverID);
    }


    public void assignMissionTo(int roverID, Mission mission) {
        if(!roversAddresses.containsKey(roverID)){
            System.out.println("This rover is not registered! Stopping mission assignment.");
            return;
        }
        if (roverMissions.containsKey(mission.id)) {
            System.out.println("Attempt to assign previously assigned mission. Stopping!");
            return;
        }
        allMissions.put(mission.id, mission); // Isto não deve ficar aqui no final
        roverMissions.put(mission.id, roverID);
        missionLinkServer.assignMission(roverID, roversAddresses.get(roverID), mission);
        System.out.println("Mission sent to rover");
    }


    public static void handleMissionTelemetry(MissionTelemetry telem){
        serverHTTP.addMissionTelemetry(telem);
    }

    public void cancelRoverMission(int roverID){
        if (!roversAddresses.containsKey(roverID)){
            System.out.println("This rover is not registered! Stopping mission cancel.");
            return;
        }
        missionLinkServer.sendCancelMission(roverID, roversAddresses.get(roverID));
    }
}
