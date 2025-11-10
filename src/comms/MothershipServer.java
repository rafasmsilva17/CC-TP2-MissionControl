package comms;


import core.MotherShip;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MothershipServer extends Thread{
    long TIMEOUT_LIMIT = 5000; // 5 Segundos

    private final DatagramSocket socket;
    private boolean running = false;
    private byte[] buf = new byte[1024];
    HashMap<Integer, String> awaitingConfirmation = new HashMap<>();
    HashMap<Integer, Long> timeouts = new HashMap<>();
    Lock lock = new ReentrantLock();
    Condition hasConfirmsCond = lock.newCondition();

    public MothershipServer() throws SocketException {
        socket = new DatagramSocket(3001);
    }

    public void addToConfirmationBuffer(int roverID, String missionID) {
        lock.lock();
        awaitingConfirmation.put(roverID, missionID);
        timeouts.put(roverID, System.currentTimeMillis());
        if(!running) hasConfirmsCond.signalAll();
        System.out.println("Mission added to confirmation buffer");
        lock.unlock();
    }

    public void run() {
        while(true) {
            // Ficar parado enquanto o buffer de confirmação estiver vazio
            lock.lock();
            while(awaitingConfirmation.isEmpty()) {
                try {
                    running = false;
                    System.out.println("Confirmation buffer empty. Mothership server Thread awaiting");
                    hasConfirmsCond.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            lock.unlock();
            running = true;

            // Buffer de confirmaçao tem elementos -> Continuar trabalho
            Arrays.fill(buf, (byte)0);
            DatagramPacket packet =
                    new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);

                // Recebe dados
                ByteBuffer receivedData = ByteBuffer.wrap(packet.getData());
                int senderRoverID = Encoder.decodeInt(receivedData);
                String missionID = Encoder.decodeString(receivedData);

                // Verifica se o que recebeu está no buffer
                System.out.println("Received confirmation from: " + senderRoverID);
                if (confirmReceived(senderRoverID, missionID)) {
                    System.out.println("Rover " + senderRoverID + " received mission " + missionID);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Verificar se alguma missao já está em timeout
            tickTimeouts();
            running = !awaitingConfirmation.isEmpty();
        }
    }

    private boolean confirmReceived(int roverID, String missionID){
        // TODO Fazer algo caso o que recebeu não está no buffer
        // Isto aconteceria caso um packet tenha excedido timeout
        // E ainda nao foi enviado novamente
        System.out.println(awaitingConfirmation);
        if(!awaitingConfirmation.containsKey(roverID)){
            System.out.println("Mothership server received confirmation for a rover it was not expecting! " +
                    "Are you sure this behaviour is intended?");
            return false;
        }

        boolean exists = awaitingConfirmation.get(roverID).equals(missionID);
        if (exists) {
            awaitingConfirmation.remove(roverID);
        }
        return exists;
    }

    private void tickTimeouts(){
        timeouts.forEach((id, arrivalTime) -> {
            if(arrivalTime - System.currentTimeMillis() > TIMEOUT_LIMIT){
                timeouts.remove(id);
                awaitingConfirmation.remove(id);
                MotherShip.reassignMissionTo(id, awaitingConfirmation.get(id));
            }
        });
    }
}
