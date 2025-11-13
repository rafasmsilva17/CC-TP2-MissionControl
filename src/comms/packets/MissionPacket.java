package comms.packets;

import comms.Encodable;
import comms.Encoder;
import core.missions.Mission;

import java.util.Arrays;

public class MissionPacket extends TLVPacket implements RoverPacket {
    public static final PacketType type = PacketType.MISSION;

    public MissionPacket(Mission mission){
        super(Encoder.encodeToPacket(mission));
    }
}
