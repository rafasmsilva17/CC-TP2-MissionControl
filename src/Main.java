import comms.MothershipML;
import comms.RoverServer;
import core.missions.PhotoMission;


public class Main {

    static void main(String[] args) {
        PhotoMission miss = new PhotoMission(new int[]{0, 0}, 1, 2);
        //ByteBuffer encodedMission = Encoder.encodeMission(miss);

        Thread roverServer = new Thread(new RoverServer());
        roverServer.start();

        MothershipML missionAssigner = new MothershipML();
        missionAssigner.assignMission(miss);

        try {
            roverServer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Main finished!");
    }
}
