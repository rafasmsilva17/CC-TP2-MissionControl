package comms;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class RoverServer extends Thread{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[1024];

    public RoverServer(){
        try{
            socket = new DatagramSocket(3000);
        } catch (SocketException e){
            System.out.println("Error creating Rover Server socket");
        }
    }

    public void run(){
        running = true;

        while(running) {
            DatagramPacket packet =
                    new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                System.out.println("Rover Received:" + packet.getData().toString().trim());

                // Mandar confirmacao? Ou esperar por telemetria
                int port = packet.getPort();
                InetAddress senderAddress = packet.getAddress();

            } catch (IOException e) {
                System.out.println("Rover failed to receive Packet");
            }
        }
    }
}
