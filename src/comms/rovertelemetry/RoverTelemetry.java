package comms.rovertelemetry;

import comms.Encoder;
import comms.Telemetry;
import comms.packets.common.PacketType;
import comms.packets.common.TLVPacket;
import core.rover.Rover;
import core.missions.Mission;
import core.missions.common.Coordinate;
import core.rover.RoverStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoverTelemetry extends Telemetry {
    public int roverID;
    public int batteryCharge;
    public Coordinate coord;
    public float currSpeed;
    public List<String> missionBuffer = new ArrayList<>();
    public String currMission = null;
    public RoverStatus status;


    public RoverTelemetry(Rover rover){
        this.roverID = rover.getId();
        this.batteryCharge = rover.battery.getCharge();
        this.coord = rover.getPosition();
        this.currSpeed = rover.getSpeed();
        this.missionBuffer = rover.getMissionBuffer();
        Mission curr = rover.getCurrentMission();
        if (curr != null) currMission = curr.id;
        this.status = rover.status;
    }

    public RoverTelemetry(ByteBuffer buffer){
        this.roverID = Encoder.decodeInt(buffer);
        this.batteryCharge = Encoder.decodeByte(buffer);
        this.status = RoverStatus.fromByte(Encoder.decodeByte(buffer));
        this.coord = Encoder.decodeCoordinate(buffer);
        this.currSpeed = Encoder.decodeFloat(buffer);
        missionBuffer.addAll(Arrays.asList(Encoder.decodeStringArray(buffer)));
        this.currMission = Encoder.decodeString(buffer);
        if (currMission.equals("null")) currMission = null;
    }

    public TLVPacket getEncodeData(){
        TLVPacket packet = new TLVPacket();
        packet.writeByte(PacketType.ROVERTELEMETRY.toByte());
        packet.writeInt(roverID);
        packet.writeByte((byte)batteryCharge);
        packet.writeByte(status.toByte());
        packet.writeCoordinate(coord);
        packet.writeFloat(currSpeed);
        String[] missionIDS = new String[missionBuffer.size()];
        int c = 0;
        for (String s : missionBuffer) {
            missionIDS[c++] = s;
        }
        packet.writeStringArray(missionIDS);
        packet.writeString((currMission != null ? currMission : "null"));
        return packet;
    }

    @Override
    public Element getElement(Document doc) {
        Element base = doc.createElement("Rover" + roverID);
        base.setAttribute("id", String.valueOf(roverID));
        base.setAttribute("battery", String.valueOf(batteryCharge));
        base.setAttribute("position", coord.toString());
        base.setAttribute("speed", String.valueOf(currSpeed));
        base.setAttribute("DoingMission", (currMission != null) ? currMission : "none");
        base.setAttribute("MissionBuffer", missionBuffer.toString());
        base.setAttribute("Status", status.name());
        return base;
    }
}
