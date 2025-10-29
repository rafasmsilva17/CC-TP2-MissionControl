package comms;

import java.io.IOException;
import java.net.*;

public class RoverClient{
    private DatagramSocket socket;
    private InetAddress mothershipAddress;
    private byte[] buf;

    public RoverClient() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        mothershipAddress = InetAddress.getByName("localhost");
    }

    public String sendMsg(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet =
                new DatagramPacket(buf, buf.length, mothershipAddress, 3000);

        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);

        // wait for response
        socket.receive(packet);
        String received = new String(
                packet.getData(), 0, packet.getLength()
        );
        return received;
    }

    public void close(){
        socket.close();
    }
}
