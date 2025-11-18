package comms.telemetry;

import comms.Encoder;
import comms.packets.common.TLVPacket;
import core.missions.PhotoMission;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.ByteBuffer;

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

    public Element getElement(Document doc){
        Element base = super.getElement(doc);
        Element specific = doc.createElement("info");
        specific.setAttribute("position", coord.toString());
        specific.setAttribute("direction", String.valueOf(direction));
        specific.setAttribute("quantity", String.valueOf(quantity));
        base.appendChild(specific);
        return base;
    }

    public TLVPacket getEncodeData(){
        TLVPacket packet = super.getEncodeData();
        packet.writeCoordinate(coord);
        packet.writeFloat(direction);
        packet.writeInt(quantity);

        return packet;
    }
}
