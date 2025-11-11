import comms.missionlink.RoverServer;
import core.MotherShip;
import core.Rover;
import core.missions.common.Coordinate;
import core.missions.PhotoMission;
import core.missions.common.Priority;


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
        // Está a simular packet loss de 90%
        // Para tirar ir ao ficheiro RoverServer.java e depois abre os olhos e procura

        Coordinate coord = new Coordinate(88.54f, 32.64f);
        PhotoMission miss = new PhotoMission(coord, 1, 2, 60, Priority.URGENT);

        MotherShip mothership = new MotherShip();
        Rover rover0 = new Rover();

        mothership.assignMissionTo(rover0.getId(), miss);

    }
}
