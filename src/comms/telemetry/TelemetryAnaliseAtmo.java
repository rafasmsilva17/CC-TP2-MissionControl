package comms.telemetry;

import comms.Encoder;
import comms.packets.common.TLVPacket;
import core.missions.AnaliseAtmosphereMission;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TelemetryAnaliseAtmo extends MissionTelemetry {
    public Coordinate coord;
    byte[] analysisResult;

    public TelemetryAnaliseAtmo(MissionType type, AnaliseAtmosphereMission mission){
        super(type, mission);
        this.coord = mission.getPosition();
        this.analysisResult = mission.getResult();
        if (analysisResult == null) analysisResult = new byte[]{};
    }

    public TelemetryAnaliseAtmo(MissionType type, ByteBuffer buf){
        super(type, buf);
        this.coord = Encoder.decodeCoordinate(buf);
        this.analysisResult = Encoder.decodeByteArray(buf);
    }

    public Element getElement(Document doc) {
        Element base = super.getElement(doc);
        Element info = doc.createElement("info");
        info.setAttribute("analysis", Arrays.toString(analysisResult));
        info.setAttribute("position", coord.toString());
        base.appendChild(info);
        return base;
    }

    public TLVPacket getEncodeData(){
        TLVPacket packet = super.getEncodeData();
        packet.writeCoordinate(coord);
        packet.writeByteArray(analysisResult);
        return packet;
    }
}
