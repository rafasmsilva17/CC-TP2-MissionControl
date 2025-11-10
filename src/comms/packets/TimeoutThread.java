package comms.packets;

import comms.missionlink.MothershipServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeoutThread extends Thread {
    public final long TIMEOUT_LIMIT = 5000; // 5 segundos
    private final MothershipServer msServer;

    private final HashMap<Integer, Long> timeoutsMap = new HashMap<>();
    private final List<Integer> timedOut = new ArrayList<>();
    private final Lock timeoutMapLock = new ReentrantLock();
    private final Condition hasTimeoutsCond = timeoutMapLock.newCondition();

    private final Lock timedOutLock = new ReentrantLock();
    private boolean running = false;

    public TimeoutThread(MothershipServer msServer){
        this.msServer = msServer;
    }



    private void notifyTimeouts(){
        msServer.receiveTimedOuts(new ArrayList<>(timedOut));
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
    public void addTimeout(int id){
        timeoutMapLock.lock();
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

                timeoutMapLock.lock();
                timeoutsMap.forEach((id, arrivalTime) -> {
                    if(System.currentTimeMillis() - arrivalTime > TIMEOUT_LIMIT){
                        System.out.println("[TIMEOUT] " + id + " time is up");
                        addTimedOut(id);
                    }
                });
                for (Integer id : timedOut) {
                    removeTimeout(id);
                }
                notifyTimeouts();
                timeoutMapLock.unlock();
            }

            running = false;
        }
    }
}
