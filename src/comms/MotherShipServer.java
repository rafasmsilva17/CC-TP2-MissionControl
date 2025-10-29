package comms;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class MotherShipServer extends Thread{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[1024];

    public MotherShipServer() throws SocketException {
        socket = new DatagramSocket(3000);
    }

    public void run() {
        running = true;

        while(running) {
            DatagramPacket packet =
                    new DatagramPacket(buf, buf.length);

            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Get address from sender
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            packet = new DatagramPacket(buf, buf.length, address, port);
            String received =
                    new String(packet.getData(), 0, packet.getLength()).trim();
            System.out.println("Server received " + received);

            if (received.equals("end")){
                running = false;
                continue;
            }

            // send received to sender
            try {
                String answer = "Received from address: " + address;
                packet = new DatagramPacket(answer.getBytes(), answer.getBytes().length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // reset buffer
            Arrays.fill(buf, (byte)0);
        }
        socket.close();
        System.out.println("Closing!");
    }

}
