package core;

import comms.missionlink.RoverServer;
import core.missions.AnaliseSampleMission;
import core.missions.Mission;
import core.missions.PhotoMission;
import core.missions.common.Coordinate;
import core.missions.common.Sample;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Rover {

    private int id = -1;
    private Long awakeTime = System.currentTimeMillis();
    private Battery battery = new Battery();
    // Priority Queue com as missoes passou para o mission handler
    private HashMap<String, Sample> collectedSamples = new HashMap<>(); //amostras atualmente no rover
    private float speed = 2f;
    private final Coordinate position = new Coordinate(0, 0);
    private boolean sendMissionFinish = false;
    private final Lock missionFinishLock = new ReentrantLock();

    // TODO mission handler thread
    private final RoverMissionHandler missionHandler;
    private final RoverServer missionServer;

    public Rover(){
        missionHandler = new RoverMissionHandler(this);
        missionServer = new RoverServer(this);
        missionServer.start();
        missionServer.setName("Rover" + id + " Server");
        run();
        //missionHandler.start();
        //missionServer.sendRegistration();
    }

    public int getId(){ return id; }

    public void setId(int assignedID){
        if(id != -1) return; // so pode ser usada uma vez
        id = assignedID;
        System.out.println("Im registered as Rover " + assignedID);
        missionHandler.start(); // ja esta registado, pode começar a fazer missões
    }

    public boolean isRegistered(){
        return (id != -1);
    }

    public RoverServer getServer(){ return missionServer; }

    public void notifyMissionFinish(){
        missionFinishLock.lock();
        sendMissionFinish = true;
        missionFinishLock.unlock();
    }

    private boolean toSendMissionFinish(){
        missionFinishLock.lock();
        try{
            return sendMissionFinish;
        } finally {
            missionFinishLock.unlock();
        }

    }

    public void receiveMission(Mission mission){
        if(missionHandler.hasMission(mission)){
            System.out.println("[ROVER " + id + "] " + " Received duplicate mission. " +
                    "Is there packet loss?");
            return;
        }
        if (mission instanceof AnaliseSampleMission){
            String sampleID = ((AnaliseSampleMission) mission).getSample_ID();
            System.out.println("[ROVER " + id + "] Received mission for sample that I don't have!");
            if (!collectedSamples.containsKey(sampleID)) return;
        }
        missionHandler.addMission(mission);
    }

    // Isto não é implementaçao de thread
    public void run(){
        Mission currentMission = null;
        long lastUpdate = -1L;
        while(true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (toSendMissionFinish()){
                missionServer.sendMissionTelemetry(missionHandler.getLastFinishedMission());
                sendMissionFinish = false;
                continue;
            }
            currentMission = missionHandler.getCurrMission();
            if(currentMission != null){
                if (lastUpdate == -1L) lastUpdate = System.currentTimeMillis();
                if(System.currentTimeMillis() > lastUpdate + currentMission.updateInterval * 1000L){
                    missionServer.sendMissionTelemetry(currentMission);
                    System.out.println("Sent mission telemetry");
                    lastUpdate = System.currentTimeMillis() + currentMission.updateInterval * 1000L;
                }
            }

            // updateRoverStatus

        }
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setLatitude(float latitude){
        this.position.setLatitude(latitude);
    }

    public void setLongitude(float longitude){
        this.position.setLongitude(longitude);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float newS){
        this.speed = newS;
    }

    public void addSample(Sample newSample){
        collectedSamples.put(newSample.getId(), newSample);
    }

    public List<Sample> getCollectedSamples(){
        return new ArrayList<>(collectedSamples.values());
    }

    public List<String> getCollectedSamplesIDS(){
        return new ArrayList<>(collectedSamples.keySet());
    }


    public boolean moveTowards(Coordinate objective){
        float latitude = position.getLatitude();
        float longitude = position.getLongitude();
        float distX = objective.getLatitude() - latitude;
        float distY = objective.getLongitude() - longitude;

        float dist = (float) Math.sqrt(distX * distX + distY * distY);

        if (dist <= speed) return true;

        System.out.println("Distance to objective: " + dist);
        this.setLatitude(latitude + ((distX / dist) * speed));
        this.setLongitude(longitude+ ((distY / dist) * speed));
        // variar velocidade
        float MAXIMUM_SPEED = 2f;
        float MINIMUM_SPEED = 0.5f;
        speed = new Random().nextFloat(MINIMUM_SPEED, MAXIMUM_SPEED);
        return false;
    }
}
