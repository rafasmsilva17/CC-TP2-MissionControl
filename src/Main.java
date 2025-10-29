import comms.Encoder;
import comms.MotherShipServer;
import comms.RoverClient;
import core.missions.PhotoMission;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws SocketException, UnknownHostException {
        PhotoMission miss = new PhotoMission(new int[]{0, 0}, 1, 2);
        Encoder.encodeMission(miss);
    }
}
