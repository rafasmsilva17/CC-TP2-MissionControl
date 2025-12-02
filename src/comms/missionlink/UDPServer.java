package comms.missionlink;

import comms.Encoder;
import comms.packets.ConfirmationPacket;
import comms.packets.common.PacketType;

import java.io.IOException;
import java.net.*;
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

    public String getHostName(){
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
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

    public void sendACK(int packetIdentifier, InetAddress address, int port){
        ConfirmationPacket ACK = new ConfirmationPacket(packetIdentifier);
        try {
            System.out.println("[" + getName() + "] Sending UDP ACK");
            socket.send(new DatagramPacket(ACK.getBuffer(), ACK.getBuffer().length,
                    address, port));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                    "I am already expecting an ACK for this identifier! " + packetIdentifier  +
                    " Overwriting for newer packet");
            timeoutHandler.removeTimeout(packetIdentifier);
            System.out.println(packet.toString());
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

    // Mandar packet sem esperar por um ACK
    public void sendPacket(DatagramPacket packet){
        try{
            socket.send(packet);
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
