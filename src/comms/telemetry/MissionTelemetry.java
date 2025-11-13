package comms.telemetry;

import comms.Encodable;
import comms.Encoder;
import comms.packets.PacketType;
import comms.packets.TLVPacket;
import core.missions.Mission;
import core.missions.PhotoMission;
import core.missions.common.MissionType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class MissionTelemetry implements Encodable {
    public MissionType type;
    public String id;
    public long ttl;
    public Document doc;

    protected MissionTelemetry(){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            this.doc = db.newDocument();

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    protected MissionTelemetry(MissionType type, Mission mission) {
        this();
        this.type = type;
        this.id = mission.id;
        this.ttl = mission.getTTL();
    }

    public MissionTelemetry(MissionType type, ByteBuffer buffer){
        this();
        this.type = type;
        this.id = Encoder.decodeString(buffer);
        this.ttl = Encoder.decodeInt(buffer); // time left till mission reaches max duration
    }

    public Attr createAttr(String name, String value){
        Attr newA = doc.createAttribute(name);
        newA.setValue(value);
        return newA;
    }

    // TODO mudar para retornar um elementos inves de attributo, fica melhor
    public List<Attr> getAttributes(){
        List<Attr> attributes = new ArrayList<>();
        attributes.add(createAttr("id", id));
        attributes.add(createAttr("type", type.name()));
        attributes.add(createAttr("ttl", String.valueOf(ttl)));

        return attributes;
    }

    public TLVPacket getEncodeData(){
        TLVPacket packet = new TLVPacket();
        packet.writeByte(PacketType.MISSIONTELEMETRY.toByte());
        packet.writeByte((byte)type.toInt());
        packet.writeString(id);
        packet.writeInt(Math.toIntExact(ttl));
        return packet;
    }

    public static MissionTelemetry fromMission(Mission mission){
        MissionType type = mission.type;
        return switch (type){
            case PHOTO -> new TelemetryPhoto(type, (PhotoMission) mission);
            default -> null;
        };
    }

    public static MissionTelemetry fromBuffer(ByteBuffer buf){
        MissionType type = MissionType.fromInteger(Encoder.decodeByte(buf));
        return switch (type){
            case PHOTO -> new TelemetryPhoto(type, buf);
            default -> null;
        };
    }

    @Override
    public String toString() {
        return "Telemetry for mission " + id +
                " | Of type " + type +
                " | TTL " + ttl;
    }
}
