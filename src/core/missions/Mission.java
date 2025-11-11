package core.missions;


import comms.Encodable;
import comms.Encoder;
import comms.packets.TLVPacket;
import comms.telemetry.MissionTelemetry;
import core.missions.common.MissionType;
import core.missions.common.Priority;

import java.nio.ByteBuffer;


import javax.management.InvalidAttributeValueException;

public abstract class Mission implements Encodable {
    public static int ID_COUNTER = 0;

    public MissionType type;
    public String id;
    public Priority priority;
    public MissionTelemetry telemetry;


    public Mission(){
        id = "M-".concat(String.format("%03d", ID_COUNTER));
        ID_COUNTER++;
        this.priority = Priority.NORMAL;
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
    }

    @Override
    public TLVPacket getEncodeData(){
        TLVPacket packet = new TLVPacket();
        packet.writeByte((byte)type.toInt());
        packet.writeString(id);
        packet.writeByte((byte)priority.toInteger());
        return packet;
    }

    private Boolean checkTelemetryType(MissionType type, MissionTelemetry telemetry){
        Boolean isCompatible = true;

        //...
        return true;
    }


}
