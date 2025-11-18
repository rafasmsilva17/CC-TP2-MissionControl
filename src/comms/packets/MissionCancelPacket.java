package comms.packets;

import comms.packets.common.PacketType;
import comms.packets.common.RoverPacket;
import comms.packets.common.TLVPacket;

public class MissionCancelPacket extends TLVPacket implements RoverPacket {
    public static final PacketType type = PacketType.MISSIONCANCEL;

    public MissionCancelPacket(){
        super();
        writeByte(type.toByte());
    }
}
