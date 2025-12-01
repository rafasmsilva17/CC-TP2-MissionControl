package core.missions;

import comms.Encoder;
import java.nio.ByteBuffer;

import comms.packets.common.TLVPacket;
import comms.telemetry.MissionTelemetry;
import comms.telemetry.TelemetryVideo;
import core.rover.Rover;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;
import core.missions.common.Priority;

public class VideoMission extends Mission{
    private Coordinate position;
    private float direction; // em graus (N,S,E,O)
    private int duration;

    private long videoStartTime = -1;

    // duration in seconds
    public VideoMission(Coordinate position, int direction, int duration){
        super();
        this.type = MissionType.VIDEO;
        this.position = position;
        this.direction = direction;
        this.duration = duration;
    }
    
    public VideoMission(Coordinate position, int direction, int duration, int maxDuration, Priority p){
        super(p, maxDuration);
        this.type = MissionType.VIDEO;
        this.position = position;
        this.direction = direction;
        this.duration = duration;
    }
    

    protected VideoMission(MissionType type, ByteBuffer buf){
        super(type, buf);
        this.position = Encoder.decodeCoordinate(buf);
        this.direction = Encoder.decodeFloat(buf);
        this.duration = Encoder.decodeInt(buf);
    }

    @Override
    public MissionTelemetry getTelemetry() {
        return new TelemetryVideo(type, this);
    }

    @Override
    public boolean executeMission(Rover rover) {
        if (!roverArrived) roverArrived = rover.moveTowards(position);
        else {
            rover.workingStatus();
            if (videoStartTime == -1) videoStartTime = System.currentTimeMillis();
            return (System.currentTimeMillis() - videoStartTime) / 1000 >= duration;
        }
        return false;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public int getDuration() { return duration; }

    public void setDuration(int duration) { this.duration = duration; }


    @Override
    public TLVPacket getEncodeData(){
        TLVPacket packet = super.getEncodeData();

        packet.writeCoordinate(position);
        packet.writeFloat(direction);
        packet.writeInt(duration);
        return packet;
    }
}
