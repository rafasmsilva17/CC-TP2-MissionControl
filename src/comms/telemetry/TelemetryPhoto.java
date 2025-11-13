package comms.telemetry;

import comms.Encoder;
import comms.packets.TLVPacket;
import core.missions.PhotoMission;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.ByteBuffer;
import java.util.List;

public class TelemetryPhoto extends MissionTelemetry {
    public Coordinate coord;
    public float direction;
    public int quantity;

    public TelemetryPhoto(MissionType type, PhotoMission mission){
        super(type, mission);
        this.coord = mission.getPosition();
        this.direction = mission.getDirection();
        this.quantity = mission.getQuantity();
    }

    public TelemetryPhoto(MissionType type, ByteBuffer buf){
        super(type, buf);
        this.coord = Encoder.decodeCoordinate(buf);
        this.direction = Encoder.decodeFloat(buf);
        this.quantity = Encoder.decodeInt(buf);
    }

    public List<Attr> getAttributes(){
        List<Attr> attributes = super.getAttributes();
        attributes.add(createAttr("position", coord.toString()));
        attributes.add(createAttr("direction", String.valueOf(direction)));
        attributes.add(createAttr("quantity", String.valueOf(quantity)));

        return attributes;
    }

    public TLVPacket getEncodeData(){
        TLVPacket packet = super.getEncodeData();
        packet.writeCoordinate(coord);
        packet.writeFloat(direction);
        packet.writeInt(quantity);

        return packet;
    }
}
