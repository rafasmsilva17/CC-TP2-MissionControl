package comms;


import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MothershipServer extends Thread{
    long TIMEOUT_LIMIT = 5000; // 5 Segundos

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[1024];
    HashMap<Integer, String> awaitingConfirmation = new HashMap<>();
    HashMap<Integer, Long> timeouts = new HashMap<>();
    Lock lock = new ReentrantLock();
    Condition hasConfirmsCond = lock.newCondition();

    public MothershipServer() throws SocketException {
        socket = new DatagramSocket(3000);
    }

    public void addToConfirmationBuffer(int roverID, String missionID) {
        awaitingConfirmation.put(roverID, missionID);
        timeouts.put(roverID, System.currentTimeMillis());
        if(!running) hasConfirmsCond.signal();
    }

    public void run() {

        // Ficar parado enquanto o buffer de confirmação estiver vazio
        lock.lock();
        while(awaitingConfirmation.isEmpty()) {
            try {
                hasConfirmsCond.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Buffer tem elementos, fica á espera de receber packets de confirmação
        running = true;
        while(running) {
            DatagramPacket packet =
                    new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                ByteBuffer receivedData = ByteBuffer.wrap(packet.getData());
                int senderRoverID = receivedData.getInt();

                // Recebe dados
                int tamanhoID = receivedData.get();
                StringBuilder missionIDBuilder = new StringBuilder();
                for (int i = 0; i < tamanhoID; i++){
                    missionIDBuilder.append(receivedData.getChar());
                }
                String missionID = missionIDBuilder.toString();

                // Verifica se o que recebeu está no buffer
                if (confirmReceived(senderRoverID, missionID)) {
                    System.out.println("Rover " + senderRoverID + " received mission " + missionID);
                }

            } catch (IOException e) {
                lock.unlock();
                throw new RuntimeException(e);
            }

            // Verificar se alguma missao já está em timeout
            tickTimeouts();
            running = !awaitingConfirmation.isEmpty();
        }
        socket.close();
        System.out.println("Closing!");

        lock.unlock();
    }

    private boolean confirmReceived(int roverID, String missionID){
        // TODO Fazer algo caso o que recebeu não está no buffer
        // Isto aconteceria caso um packet tenha excedido timeout
        // E ainda nao foi enviado novamente
        boolean exists = awaitingConfirmation.get(roverID).equals(missionID);
        if (exists) {
            awaitingConfirmation.remove(roverID);
        }
        return exists;
    }

    private void tickTimeouts(){
        timeouts.forEach((id, arrivalTime) -> {
            if(arrivalTime - System.currentTimeMillis() > TIMEOUT_LIMIT){
                // TODO Fazer qualquer coisa com timeout
            }
        });
    }
}
