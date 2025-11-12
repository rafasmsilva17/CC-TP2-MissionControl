package comms.missionlink;


import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeoutThread extends Thread {
    public final long TIMEOUT_LIMIT = 5000; // 5 segundos
    private final UDPServer uServer;

    private final ConcurrentHashMap<Integer, DatagramPacket> packetsMap = new ConcurrentHashMap<>();
    private final HashMap<Integer, Long> timeoutsMap = new HashMap<>();
    private final BlockingQueue<Integer> timedOut = new LinkedBlockingQueue<>();

    private final Lock timeoutMapLock = new ReentrantLock();
    private final Condition hasTimeoutsCond = timeoutMapLock.newCondition();

    private final Lock timedOutLock = new ReentrantLock();
    private boolean running = false;

    public TimeoutThread(UDPServer msServer){
        this.uServer = msServer;
    }

    private void resendTimedOuts(){
        for (Integer packetIdentifier : timedOut) {
            uServer.stopWaitingFor(packetIdentifier);
            if(!packetsMap.containsKey(packetIdentifier)){
                System.out.println("I DO NOT HAVE THIS PACKET FOR SOME REASON " + packetIdentifier);
            }
            DatagramPacket resendPacket = packetsMap.get(packetIdentifier);
            packetsMap.remove(packetIdentifier);
            uServer.sendPacket(packetIdentifier, resendPacket);
        }
        timedOut.clear();
    }

    private void addTimedOut(int id){
        timedOutLock.lock();
        try {
            timedOut.add(id);
        } finally {
            timedOutLock.unlock();
        }
    }

    // Adicionar coisa para esperar timeout
    public void addTimeout(int id, DatagramPacket packet){
        timeoutMapLock.lock();
        packetsMap.put(id, packet);
        timeoutsMap.put(id, System.currentTimeMillis());
        if(!running){
            hasTimeoutsCond.signalAll();
        }
        timeoutMapLock.unlock();
        //System.out.println("Timeout added to buffer!");
    }

    public void removeTimeout(int id){
        timeoutMapLock.lock();
        timeoutsMap.remove(id);
        timeoutMapLock.unlock();
        packetsMap.remove(id);
        timedOut.remove(id);
    }

    private void stopTracking(int id){
        timeoutMapLock.lock();
        timeoutsMap.remove(id);
        timeoutMapLock.unlock();
    }



    // Fica a atualizar tempos de timeout enquanto o map tiver coisas dentro
    // Atualiza os tempos a cada 500 milliseconds
    // Aquilo que atinge timeout vai para o buffer de timeouts (arraylist)
    public void run() {
        while(true){
            timeoutMapLock.lock();
            while(timeoutsMap.isEmpty()){
                try {
                    running = false;
                    hasTimeoutsCond.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            timeoutMapLock.unlock();
            running = true;

            while(!timeoutsMap.isEmpty()){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                ArrayList<Integer> timedoutIDS = new ArrayList<>();
                timeoutMapLock.lock();
                timeoutsMap.forEach((id, arrivalTime) -> {
                    if(System.currentTimeMillis() - arrivalTime > TIMEOUT_LIMIT){
                        System.out.println("[TIMEOUT] " + id + " time is up");
                        timedoutIDS.add(id);
                    }
                });
                timeoutMapLock.unlock();
                for (Integer id : timedoutIDS) {
                    stopTracking(id);
                    addTimedOut(id);
                }
                resendTimedOuts();
            }

            running = false;
        }
    }
}
