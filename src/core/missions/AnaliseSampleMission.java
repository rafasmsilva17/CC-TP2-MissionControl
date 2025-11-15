package core.missions;

import comms.packets.TLVPacket;
import comms.telemetry.MissionTelemetry;
import core.Rover;
import core.missions.common.MissionType;

import java.nio.ByteBuffer;


public class AnaliseSampleMission extends Mission {

    private int sample_ID;   


    public AnaliseSampleMission(int sample_ID) {
        this.type = MissionType.ANALYSE_SAMPLE;
        this.sample_ID = sample_ID;
    }

    public AnaliseSampleMission(MissionType type, ByteBuffer buf) {
        super(type, buf);
        this.sample_ID = buf.getInt();
    }

    @Override
    public MissionTelemetry getTelemetry() {
        return null;
    }

    @Override
    public boolean executeMission(Rover rover) {
        return false;
    }


    public int getSample_ID() {
        return this.sample_ID;
    }

    public void setPosition(int id) {
        this.sample_ID = id;
    }

    @Override
    public TLVPacket getEncodeData() {
        TLVPacket var1 = new TLVPacket();
        var1.writeByte((byte)this.type.toInt());
        var1.writeString(this.id);
        var1.writeInt(this.sample_ID);
        return var1;
    }

    @Override
    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("Mission ").append(this.id).append(" -> ");
        var1.append("SampleID ").append(this.sample_ID);
        return var1.toString();
    }
}