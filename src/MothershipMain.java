import comms.missionlink.RoverServer;
import core.MotherShip;
import core.Rover;
import core.missions.*;
import core.missions.common.Coordinate;
import core.missions.common.Priority;


public class MothershipMain {
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

        Coordinate coord = new Coordinate(10.54f, 10.64f);
        PhotoMission miss = new PhotoMission(coord, 1, 2, 60 * 10, Priority.URGENT);
        GetSampleMission miss1 = new GetSampleMission(coord, 2, 5.0f, 600, Priority.URGENT);
        AnaliseSampleMission miss11 = new AnaliseSampleMission("1-1");
        VideoMission miss2 = new VideoMission(coord, 1, 60);
        AnaliseAtmosphereMission miss3 = new AnaliseAtmosphereMission(coord, 60 * 10, Priority.NORMAL);

        MotherShip mothership = new MotherShip();
        //Rover rover0 = new Rover();

        try {
            Thread.sleep(10000); // iniciar o servidor do rover durante este tempo
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        mothership.assignMissionTo(1, miss3);
        try {
            Thread.sleep(6000); // iniciar o servidor do rover durante este tempo
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        mothership.cancelRoverMission(1);
    }
}
