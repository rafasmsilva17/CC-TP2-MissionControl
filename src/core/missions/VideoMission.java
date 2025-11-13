package core.missions;

import comms.Encoder;
import java.nio.ByteBuffer;

import comms.packets.TLVPacket;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;

public class VideoMission extends Mission{
    private Coordinate position;
    private int direction; // em graus (N,S,E,O)
    private int duration;

    public VideoMission(Coordinate position, int direction, int duration){
        super();
        this.type = MissionType.VIDEO;
        this.position = position;
        this.direction = direction;
        this.duration = duration;
    }

    protected VideoMission(MissionType type, ByteBuffer buf){
        super(type, buf);
        this.position = Encoder.decodeCoordinate(buf);
        this.direction = Encoder.decodeInt(buf);
        this.duration = Encoder.decodeInt(buf);
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
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
