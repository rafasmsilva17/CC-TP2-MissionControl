package core.missions;


import comms.Encodable;
import comms.Encoder;
import comms.packets.PacketType;
import comms.packets.TLVPacket;
import comms.telemetry.MissionTelemetry;
import core.missions.common.MissionType;
import core.missions.common.Priority;

import java.nio.ByteBuffer;
import java.util.Objects;


import javax.management.InvalidAttributeValueException;

public abstract class Mission implements Encodable {
    public static int ID_COUNTER = 1;
    public int maxDuration = 10; // 10 segundos ou minutos

    public MissionType type;
    public String id;
    public Priority priority;
    public int updateInterval = 15; // seconds
    public MissionTelemetry telemetry;
    private long startTime;
    private boolean active = true;


    public Mission(){
        id = "M-".concat(String.format("%03d", ID_COUNTER));
        ID_COUNTER++;
        this.priority = Priority.NORMAL;
    }

    public Mission(Priority p, int maxDuration){
        id = "M-".concat(String.format("%03d", ID_COUNTER));
        ID_COUNTER++;
        this.priority = p;
        this.maxDuration = maxDuration;
    }

    public Mission(MissionType type ,Priority p, MissionTelemetry telemetry) throws InvalidAttributeValueException{
       id = "M-".concat(String.format("%03d", ID_COUNTER));
       ID_COUNTER++;
       this.priority = p;
       this.type = type;
       if(checkTelemetryType(type, telemetry)){
            this.telemetry = telemetry;
       }
       else throw new InvalidAttributeValueException("The mission type and telemetry do not match!");
    }

    protected Mission(MissionType type, ByteBuffer buf){
        //type = MissionType.fromInteger(Encoder.decodeByte(buf));
        this.type = type;
        id = Encoder.decodeString(buf);
        priority = Priority.fromInteger(Encoder.decodeByte(buf));
        maxDuration = Encoder.decodeInt(buf);
        updateInterval = Encoder.decodeByte(buf);
    }

    public void finish(){
        active = false;
    }

    public boolean isActive(){
        return active;
    }

    public void start(){
        this.startTime = System.currentTimeMillis();
    }

    public long getTTL(){
        return (System.currentTimeMillis() - startTime);
    }

    @Override
    public TLVPacket getEncodeData(){
        TLVPacket packet = new TLVPacket();
        packet.writeByte(PacketType.MISSION.toByte());
        packet.writeByte((byte)type.toInt());
        packet.writeString(id);
        packet.writeByte((byte)priority.toInteger());
        packet.writeInt(maxDuration);
        packet.writeByte((byte)updateInterval);
        return packet;
    }

    public static Mission fromBuffer(ByteBuffer buf){
        MissionType type = MissionType.fromInteger(Encoder.decodeByte(buf));
        assert type != null;
        return switch (type){
            case PHOTO -> new PhotoMission(type, buf);
            case VIDEO -> new VideoMission(type, buf);
            default -> null;
        };
    }

    private Boolean checkTelemetryType(MissionType type, MissionTelemetry telemetry){
        Boolean isCompatible = true;

        //...
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return Objects.equals(id, mission.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
