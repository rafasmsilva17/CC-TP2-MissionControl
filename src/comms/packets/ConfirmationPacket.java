package comms.packets;

import comms.packets.common.PacketType;
import comms.packets.common.RoverPacket;
import comms.packets.common.TLVPacket;

public class ConfirmationPacket extends TLVPacket implements RoverPacket {
    public static final PacketType type = PacketType.ACK;

    public ConfirmationPacket(int identifier, String missionID){
        super();
        writeByte((byte)type.toByte());
        writeInt(identifier);
        writeString(missionID);
    }

    public ConfirmationPacket(int identifier){
        super();
        writeByte((byte)type.toByte());
        writeInt(identifier);
    }
}
