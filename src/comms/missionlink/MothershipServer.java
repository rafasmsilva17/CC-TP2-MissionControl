package comms.missionlink;


import comms.Encoder;
import comms.packets.RoverPacketType;
import core.MotherShip;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MothershipServer extends Thread{
    private final DatagramSocket socket;
    private boolean running = false;
    private final byte[] buf = new byte[1024];
    private final HashMap<Integer, String> awaitingConfirmation = new HashMap<>();
    private final TimeoutThread timeoutHandler = new TimeoutThread(this);
    private final Lock confBufferLock = new ReentrantLock();
    private final Condition hasConfirmsCond = confBufferLock.newCondition();

    public MothershipServer() throws SocketException {
        socket = new DatagramSocket(3001);
        timeoutHandler.start();
    }

    public void addToConfirmationBuffer(int roverID, String missionID) {
        awaitingConfirmation.put(roverID, missionID);
        timeoutHandler.addTimeout(roverID);
        System.out.println("Mission added to confirmation buffer");
    }

    public void run() {
        while(true) {
            running = true;
            // Buffer de confirmaçao tem elementos -> Continuar trabalho
            Arrays.fill(buf, (byte)0);
            DatagramPacket packet =
                    new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);

                // Recebe dados
                ByteBuffer receivedData = ByteBuffer.wrap(packet.getData());
                int packetType = Encoder.decodeByte(receivedData);
                if(RoverPacketType.fromInteger(packetType) == RoverPacketType.REQUEST){
                    // se for request de missao, mandar missao e passa para o proximo
                    System.out.println("[MOTHERSHIP] Received mission request from " +
                            packet.getAddress() + ":" + packet.getPort());

                    continue;
                }
                int senderRoverID = Encoder.decodeInt(receivedData);
                String missionID = Encoder.decodeString(receivedData);

                // Verifica se o que recebeu está no buffer
                System.out.println("Received confirmation from: " + senderRoverID);
                if (confirmReceived(senderRoverID, missionID)) {
                    System.out.println("Rover " + senderRoverID + " received mission " + missionID);
                }

            } catch (SocketException e) {
                // So deve acontecer quando o servidor está à espera de ConfirmationPackets
                // mas eles dao todos timeout
                System.out.println("[MOTHERSHIP SERVER] " +
                        "Socket exception! Did everything timeout?" +
                        "If yes, you can probably ignore this.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            running = !awaitingConfirmation.isEmpty();
        }
    }

    private boolean confirmReceived(int roverID, String missionID){
        // TODO Fazer algo caso o que recebeu não está no buffer
        // Isto aconteceria caso um packet tenha excedido timeout
        // E ainda nao foi enviado novamente
        System.out.println(awaitingConfirmation);
        if(!awaitingConfirmation.containsKey(roverID)){
            System.out.println( "[MOTHERSHIP SERVER] " +
                    "Received confirmation for a rover I was not expecting! " +
                    "Are you sure this behaviour is intended?");
            return false;
        }

        boolean exists = awaitingConfirmation.get(roverID).equals(missionID);
        if (exists) {
            awaitingConfirmation.remove(roverID);
            timeoutHandler.removeTimeout(roverID);
        }
        return exists;
    }

    public void receiveTimedOuts(ArrayList<Integer> timedOutIDs){
        for (Integer timedOutID : timedOutIDs) {
            String missionID = awaitingConfirmation.get(timedOutID);
            if (missionID == null){
                System.out.println("[MOTHERSHIP SERVER] " +
                        "Timed out for a confirmation I wasn't waiting for!" +
                        "You can probably ignore this.");
                continue;
            }
            awaitingConfirmation.remove(timedOutID);
            MotherShip.reassignMissionTo(timedOutID, missionID);
        }
    }
}
