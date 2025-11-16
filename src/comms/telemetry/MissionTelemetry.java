package comms.telemetry;

import comms.Encodable;
import comms.Encoder;
import comms.Telemetry;
import comms.packets.PacketType;
import comms.packets.TLVPacket;
import core.missions.Mission;
import core.missions.VideoMission;
import core.missions.common.MissionType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class MissionTelemetry extends Telemetry {
    public MissionType type;
    public int roverID;
    public String id;
    public int ttl;
    public Date arrivalTime = new Date(System.currentTimeMillis());

    protected MissionTelemetry(){
        super();
    }

    protected MissionTelemetry(MissionType type, Mission mission) {
        this();
        this.type = type;
        this.id = mission.id;
        this.roverID = mission.executedByRover;
        this.ttl = mission.getTTL();
    }

    // Construtor utilizado por receiver de telemetria
    public MissionTelemetry(MissionType type, ByteBuffer buffer){
        this();
        this.type = type;
        this.id = Encoder.decodeString(buffer);
        this.roverID = Encoder.decodeInt(buffer);
        this.ttl = Encoder.decodeInt(buffer); // time left till mission reaches max duration
    }


    // TODO mudar para retornar um elementos inves de attributo, fica melhor
    public Element getElement(Document doc){
        Element base = doc.createElement(id);
        base.setAttribute("rover", String.valueOf(roverID));
        base.setAttribute("type", type.name());
        base.setAttribute("ttl", ttl + " seconds");

        return base;
    }

    public TLVPacket getEncodeData(){
        TLVPacket packet = new TLVPacket();
        packet.writeByte(PacketType.MISSIONTELEMETRY.toByte());
        packet.writeByte((byte)type.toInt());
        packet.writeString(id);
        packet.writeInt(roverID);
        packet.writeInt(ttl);
        return packet;
    }

    // TODO Acabar isto, ou seja, implementar todas as missões e as suas telemetrias
    public static MissionTelemetry fromBuffer(ByteBuffer buf){
        MissionType type = MissionType.fromInteger(Encoder.decodeByte(buf));
        return switch (type){
            case PHOTO -> new TelemetryPhoto(type, buf);
            case VIDEO -> new TelemetryVideo(type, buf);
            case GET_SAMPLE -> new TelemetryGetSample(type, buf);
            case ANALYSE_SAMPLE -> new TelemetryAnaliseSample(type, buf);
            default -> null;
        };
    }

    @Override
    public String toString() {
        return "Telemetry for mission " + id +
                " | Of type " + type +
                " | TTL " + ttl +
                " | Arrival Time " + arrivalTime;
    }
}
