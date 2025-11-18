package comms.packets;

import comms.Encoder;
import comms.packets.common.PacketType;
import comms.packets.common.RoverPacket;
import comms.packets.common.TLVPacket;
import core.missions.Mission;


public class MissionPacket extends TLVPacket implements RoverPacket {
    public static final PacketType type = PacketType.MISSION;

    public MissionPacket(Mission mission){
        super(Encoder.encodeToPacket(mission));
    }
}
