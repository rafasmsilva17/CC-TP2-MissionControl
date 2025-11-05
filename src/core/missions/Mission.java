package core.missions;


import comms.Encodable;
import comms.Encoder;

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
       if(checkTelemtryType(type, telemetry)){
            this.telemetry = telemetry;
       }
       else throw new InvalidAttributeValueException("The mission type and telemetry do not match!");
    }


    public Mission(ByteBuffer buf){
        type = MissionType.fromInteger(Encoder.decodeByte(buf));

        id = Encoder.decodeString(buf);
        // Talvez um if/switch aqui para usar o construtor da missao dependendo do tipo
    }

    private Boolean checkTelemtryType(MissionType type, MissionTelemetry telemetry){
        Boolean isCompatible = true;

        //...
        return true;
    }


}
