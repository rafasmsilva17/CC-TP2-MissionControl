package comms.missionlink;

import comms.Encoder;
import comms.packets.PacketType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UDPServer extends Thread{
    private final DatagramSocket socket;
    private final byte[] buf = new byte[1024];
    private final UDPServerLogic logicThread;

    // Mudar isto
    private final ConcurrentLinkedQueue<Integer> waitingACKs = new ConcurrentLinkedQueue<>();
    private final TimeoutThread timeoutHandler = new TimeoutThread(this);

    public UDPServer(int port, UDPServerLogic logic) throws SocketException {
        socket = new DatagramSocket(port);
        this.logicThread = logic;
        timeoutHandler.start();
    }


    public void run(){
        while(true) {
            Arrays.fill(buf, (byte)0);
            DatagramPacket packet =
                    new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                ByteBuffer packetBuf = ByteBuffer.wrap(packet.getData());
                // checkar tipo e se for ack nem sequer faz mais merda nenhuma
                PacketType packetType = PacketType.fromByte(Encoder.decodeByte(packetBuf));
                System.out.println(getName() + " received " + packetType);
                if(packetType == PacketType.ACK){
                    receiveACK(packetBuf);
                } else {
                    logicThread.queuePacket(packet);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendACK(){

    }

    // Esta função so deve ser chamada pelo timeoutHandler
    public void stopWaitingFor(int packetIdentifier){
        if (!waitingACKs.contains(packetIdentifier)){
            System.out.println("[" + getName().toUpperCase() + "] " +
                    "Timed out for a confirmation I wasn't waiting for!" +
                    "You can probably ignore this.");
        }
        waitingACKs.remove(packetIdentifier);
    }

    public void sendPacket(int packetIdentifier, DatagramPacket packet){
        if(waitingACKs.contains(packetIdentifier)){
            System.out.println("[" + getName().toUpperCase() + "] " +
                    "I am already expecting an ACK for this identifier! " +
                    "Canceling packet emission");
            return;
        }
        try {
            socket.send(packet);
            waitingACKs.add(packetIdentifier);
            timeoutHandler.addTimeout(packetIdentifier,
                    new DatagramPacket(packet.getData(),
                            packet.getLength(),
                            packet.getAddress(),
                            packet.getPort()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean receiveACK(ByteBuffer packet){
        int packetIdentifier = packet.getInt();
        if(waitingACKs.contains(packetIdentifier)){
            waitingACKs.remove((Integer) packetIdentifier);
            timeoutHandler.removeTimeout(packetIdentifier);
            System.out.println("[" + getName().toUpperCase() + "] " +
                    "Received ACK for " + packetIdentifier);
            return true;
        }
        System.out.println("[" + getName().toUpperCase() + "] " +
                "Received ACK I was not expecting!");
        return false;
    }
}
