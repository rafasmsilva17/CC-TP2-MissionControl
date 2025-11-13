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
    public static int ID_COUNTER = 0;
    public int maxDuration = 10; // 10 segundos ou minutos

    public MissionType type;
    public String id;
    public Priority priority;
    public int updateInterval = 15; // seconds
    public MissionTelemetry telemetry;


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

    public Mission(ByteBuffer buf){
        type = MissionType.fromInteger(Encoder.decodeByte(buf));
        id = Encoder.decodeString(buf);
        priority = Priority.fromInteger(Encoder.decodeByte(buf));
        maxDuration = Encoder.decodeInt(buf);
        updateInterval = Encoder.decodeByte(buf);
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
