package comms.telemetry;

import comms.Encoder;
import comms.packets.common.TLVPacket;
import core.missions.DepositSampleMission;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.ByteBuffer;

public class TelemetryDepositSample extends MissionTelemetry {

    public Coordinate position;
    public String sampleId;

    public TelemetryDepositSample(MissionType type, DepositSampleMission mission) {
        super(type, mission);
        this.position = mission.getPosition();
        this.sampleId = mission.getSampleId();
    }

    public TelemetryDepositSample(MissionType type, ByteBuffer buffer) {
        super(type, buffer);
        this.position = Encoder.decodeCoordinate(buffer);
        this.sampleId = Encoder.decodeString(buffer);
    }

    @Override
    public Element getElement(Document doc) {
        Element base = super.getElement(doc); 
        Element info = doc.createElement("info");
        info.setAttribute("depositPosition", position.toString());
        info.setAttribute("depositedSampleID", sampleId);
        
        base.appendChild(info);
        return base;
    }

    @Override
    public TLVPacket getEncodeData() {
        TLVPacket packet = super.getEncodeData();
        packet.writeCoordinate(position);
        packet.writeString(sampleId);
        return packet;
    }
    
    @Override
    public String toString() {
        return super.toString() + " | Dep. Position: " + position + " | Sample ID: " + sampleId;
    }
}