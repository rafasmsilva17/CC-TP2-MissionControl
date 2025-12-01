package comms.packets;

import comms.packets.common.PacketType;
import comms.packets.common.RoverPacket;
import comms.packets.common.TLVPacket;

public class MissionRequestPacket extends TLVPacket implements RoverPacket {
    public static final PacketType type = PacketType.REQUEST;
    public int roverID;

    public MissionRequestPacket(int roverID){
        super();
        writeByte((byte)type.toByte());
        this.roverID = roverID;
        writeInt(roverID);
    }
}
