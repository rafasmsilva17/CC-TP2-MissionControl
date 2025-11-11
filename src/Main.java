import comms.*;
import comms.missionlink.MothershipTS;
import comms.missionlink.RoverServer;
import core.MotherShip;
import core.missions.Coordinate;
import core.missions.PhotoMission;


public class Main {
    /*
    static void main1(String[] args)  {
        PhotoMission miss = new PhotoMission(new int[]{0, 0}, 1, 2);
        //ByteBuffer encodedMission = Encoder.encodeMission(miss);

        Thread roverServer = new Thread(new RoverServer());
        roverServer.start();

        //MothershipML missionAssigner = new MothershipML();
        //missionAssigner.assignMission(miss);


        try {
            roverServer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //TESTE TS
        new Thread(() -> {
            MothershipTS server = new MothershipTS();
            server.startServer();
        }).start();

        try { //tempo para o server iniciar
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            RoverTS rover = new RoverTS("R1");
            int posX = 10;
            while (true) {
                posX++;
                String telemetria = "POS:" + posX + ",15;ESTADO:OPERACIONAL";
                rover.sendMsg(telemetria);
                
                // Pausa por 30 segundos
                Thread.sleep(30000); 
            }

        } catch (Exception e) {
            System.err.println("Erro no Rover R1: " + e.getMessage());
        }

        System.out.println("Main finished!");
    }
    */

    public static void main(String[] args){
        Coordinate coord = new Coordinate(0, 0);
        PhotoMission miss = new PhotoMission(coord, 1, 2);

        MotherShip mothership = new MotherShip();
        RoverServer roverS = new RoverServer();
        roverS.start();

        mothership.reassignMissionTo(1, miss);

    }
}
