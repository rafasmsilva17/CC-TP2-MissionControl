package comms.telemetry;

import comms.Encoder;
import comms.Telemetry;
import comms.packets.common.PacketType;
import comms.packets.common.TLVPacket;
import core.missions.Mission;
import core.missions.common.MissionStatus;
import core.missions.common.MissionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.ByteBuffer;
import java.util.Date;

public abstract class MissionTelemetry extends Telemetry {
    public MissionType type;
    public int roverID;
    public String id;
    public int ttl;
    public MissionStatus status;
    public boolean finished = false;
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
        this.status = mission.status;
        this.finished = !mission.active;
    }

    // Construtor utilizado por receiver de telemetria
    public MissionTelemetry(MissionType type, ByteBuffer buffer){
        this();
        this.type = type;
        this.id = Encoder.decodeString(buffer);
        this.roverID = Encoder.decodeInt(buffer);
        this.ttl = Encoder.decodeInt(buffer); // time left till mission reaches max duration
        this.status = MissionStatus.fromByte(Encoder.decodeByte(buffer));
    }


    public Element getElement(Document doc){
        Element base = doc.createElement(id);
        base.setAttribute("rover", String.valueOf(roverID));
        base.setAttribute("type", type.name());
        base.setAttribute("ttl", ttl + " seconds");
        base.setAttribute("status", status.name());

        return base;
    }

    public TLVPacket getEncodeData(){
        TLVPacket packet = new TLVPacket();
        packet.writeByte(PacketType.MISSIONTELEMETRY.toByte());
        packet.writeByte((byte)type.toByte());
        packet.writeString(id);
        packet.writeInt(roverID);
        packet.writeInt(ttl);
        packet.writeByte(status.toByte());
        return packet;
    }

    // TODO Acabar isto, ou seja, implementar todas as missões e as suas telemetrias
    public static MissionTelemetry fromBuffer(ByteBuffer buf){
        MissionType type = MissionType.fromByte(Encoder.decodeByte(buf));
        return switch (type){
            case PHOTO -> new TelemetryPhoto(type, buf);
            case VIDEO -> new TelemetryVideo(type, buf);
            case GET_SAMPLE -> new TelemetryGetSample(type, buf);
            case ANALYSE_SAMPLE -> new TelemetryAnaliseSample(type, buf);
            case ANALYSE_ATMO -> new TelemetryAnaliseAtmo(type, buf);
            case DEPOSIT_SAMPLE -> new TelemetryDepositSample(type,buf);
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
