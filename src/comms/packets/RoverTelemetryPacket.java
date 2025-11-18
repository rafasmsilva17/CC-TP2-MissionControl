package comms.packets;

import comms.Encoder;
import comms.packets.common.PacketType;
import comms.packets.common.TLVPacket;
import comms.rovertelemetry.RoverTelemetry;

public class RoverTelemetryPacket extends TLVPacket {
    public PacketType type = PacketType.ROVERTELEMETRY;

    public RoverTelemetryPacket(RoverTelemetry rTelemetry){
        super(Encoder.encodeToPacket(rTelemetry));
    }
}
