package core.rover;

import comms.missionlink.RoverServer;
import comms.tcp.RoverTCPClient;
import core.missions.AnaliseSampleMission;
import core.missions.Mission;
import core.missions.common.Coordinate;
import core.missions.common.Sample;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Rover {

    private int id = -1;
    private Long awakeTime = System.currentTimeMillis();
    public Battery battery = new Battery();
    public RoverStatus status = RoverStatus.IDLE;


    // Priority Queue com as missoes passou para o mission handler
    private final HashMap<String, Sample> collectedSamples = new HashMap<>(); //amostras atualmente no rover
    private float speed = 2f;
    private float temperature = 30.0f;
    private final Coordinate position = new Coordinate(0, 0);
    private boolean sendMissionFinish = false;

    private final Lock missionFinishLock = new ReentrantLock();
    private boolean cancelSignal = false;

    private final RoverMissionHandler missionHandler;
    private final RoverServer missionServer;
    private final RoverTCPClient telemetryClient;

    public Rover(){
        missionHandler = new RoverMissionHandler(this);
        missionServer = new RoverServer(this);
        telemetryClient= new RoverTCPClient(this);
        missionServer.start();
        missionServer.setName("Rover" + id + " Server");
        run();
        //missionHandler.start();
        //missionServer.sendRegistration();
    }


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
        boolean wasCharging = false;
        long lastUpdate = -1L;
        while(true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            boolean isCharging = battery.tick();
            if (isCharging){
                wasCharging = true;
                chargingStatus();
            }

            // tickTemperature()

            if (wasCharging && !isCharging){
                System.out.println("[ROVER] Battery full. Signaling to mission handler.");
                missionHandler.signalBatteryFull();
                wasCharging = false;
            }

            telemetryClient.tick();

            if (cancelSignal){
                missionHandler.cancelMission();
                cancelSignal = false;
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

    public boolean moveTowards(Coordinate objective){
        movingStatus();
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

    public void tickTemperature(){
        if (status == RoverStatus.COOLING && temperature <= 50){
            status = RoverStatus.IDLE;
            return;
        }
        float netChange = 0.0f;
        switch (status){
            case IDLE -> netChange = 0.1f;
            case CHARGING -> netChange = 0.0f;
            case MOVING -> netChange = 0.4f;
            case WORKING -> netChange = 0.2f;
            case COOLING -> netChange = 1.0f;
        }
        temperature += netChange;
        netChange = Math.clamp(netChange, 30.0f, 90.0f);
        if (temperature == 90) coolingStatus();
    }

    public void cancelMission(){
        cancelSignal = true;
    }

    public List<String> getMissionBuffer(){
        return missionHandler.missionBuffer();
    }

    public Mission getCurrentMission(){
        return missionHandler.getCurrentMission();
    }

    public float getTemperature(){ return temperature; }

    public void idleStatus(){
        status = RoverStatus.IDLE;
    }

    public void movingStatus(){
        status = RoverStatus.MOVING;
    }

    public void workingStatus(){
        status = RoverStatus.WORKING;
    }

    public void chargingStatus(){
        status = RoverStatus.CHARGING;
    }

    public void coolingStatus(){
        status = RoverStatus.COOLING;
    }
}
