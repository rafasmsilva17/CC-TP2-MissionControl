package comms.missionlink;

import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public interface UDPServerLogic {
    BlockingQueue<DatagramPacket> packetQueue = new LinkedBlockingQueue<>();

    public void queuePacket(DatagramPacket packet);
}
