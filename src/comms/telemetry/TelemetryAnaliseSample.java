package comms.telemetry;

import comms.Encoder;
import comms.packets.TLVPacket;
import core.missions.AnaliseSampleMission;
import core.missions.common.MissionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TelemetryAnaliseSample extends MissionTelemetry {
    public String sampleID;
    byte[] analysisResult;

    public TelemetryAnaliseSample(MissionType type, AnaliseSampleMission mission){
        super(type, mission);
        this.sampleID = mission.getSample_ID();
        this.analysisResult = mission.getResult();
        if (analysisResult == null) analysisResult = new byte[]{};
    }

    public TelemetryAnaliseSample(MissionType type, ByteBuffer buf){
        super(type, buf);
        this.sampleID = Encoder.decodeString(buf);
        this.analysisResult = Encoder.decodeByteArray(buf);
    }

    public Element getElement(Document doc) {
        Element base = super.getElement(doc);
        Element info = doc.createElement("info");
        info.setAttribute("sampleID", sampleID);
        info.setAttribute("analysis", Arrays.toString(analysisResult));
        base.appendChild(info);
        return base;
    }

    public TLVPacket getEncodeData() {
        TLVPacket packet = super.getEncodeData();
        packet.writeString(sampleID);
        packet.writeByteArray(analysisResult);
        return packet;
    }
}