package comms.tcp;

import comms.missionlink.RoverServer;
import comms.packets.RoverTelemetryPacket;
import comms.rovertelemetry.RoverTelemetry;
import core.MotherShip;
import core.Rover;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class RoverTCPClient{
    private final Rover parentRover;
    private final Socket socket;

    private long lastUpdate = System.currentTimeMillis();

    public RoverTCPClient(Rover rover){
        this.parentRover = rover;
        try {
            this.socket = new Socket(InetAddress.getByName(RoverServer.mothershipName), MotherShip.TCPSERVER_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void tick(){
        try{
            int updateInterval = 5;
            if ((System.currentTimeMillis() - lastUpdate)/1000 < updateInterval) return;
            lastUpdate = System.currentTimeMillis();
            OutputStream out = socket.getOutputStream();
            RoverTelemetry telem = new RoverTelemetry(parentRover);
            out.write(new RoverTelemetryPacket(telem).getBuffer());
            out.flush();
            System.out.println("[TCP] Sending telemetry");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
