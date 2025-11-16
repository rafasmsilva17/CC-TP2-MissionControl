package comms.telemetry;

import comms.Encoder;
import comms.packets.TLVPacket;
import core.missions.GetSampleMission;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;
import core.missions.common.Priority;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TelemetryGetSample extends MissionTelemetry {
    public Coordinate coord;
    public int quantity;
    public List<String> samplesPicked;

    public TelemetryGetSample(MissionType type, GetSampleMission mission){
        super(type, mission);
        this.coord = mission.getPosition();
        this.quantity = mission.getQuantity();
        this.samplesPicked = mission.getCollectedSamplesIDS();
    }

    public TelemetryGetSample(MissionType type, ByteBuffer buf){
        super(type, buf);
        this.coord = Encoder.decodeCoordinate(buf);
        this.quantity = Encoder.decodeInt(buf);
        this.samplesPicked = List.of(Encoder.decodeStringArray(buf));
    }

    public Element getElement(Document doc){
        Element base = super.getElement(doc);
        Element info = doc.createElement("info");
        info.setAttribute("position", coord.toString());
        info.setAttribute("totalSamples", String.valueOf(quantity));
        info.setAttribute("doneSamples", String.valueOf(samplesPicked));
        base.appendChild(info);
        return base;
    }

    // IntelliJ dá warning aqui??
    public TLVPacket getEncodeData(){
        TLVPacket packet = super.getEncodeData();
        String[] samplesIDS = new String[samplesPicked.size()];
        int c = 0;
        for (String s : samplesPicked) {
            samplesIDS[c++] = s;
        }

        packet.writeCoordinate(coord);
        packet.writeInt(quantity);
        packet.writeStringArray(samplesIDS);
        return packet;
    }

}
