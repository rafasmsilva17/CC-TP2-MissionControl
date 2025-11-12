package comms.packets;

public class MissionRequestPacket extends TLVPacket implements RoverPacket {
    public static final PacketType type = PacketType.REQUEST;

    public MissionRequestPacket(){
        super();
        writeByte((byte)type.toByte());
    }
}
