package comms.packets;

import comms.Encoder;
import comms.telemetry.MissionTelemetry;

public class MissionTelemetryPacket extends TLVPacket {
    public PacketType type = PacketType.MISSIONTELEMETRY;

    public MissionTelemetryPacket(MissionTelemetry mTelemetry){
        super(Encoder.encodeToPacket(mTelemetry));
    }

}
