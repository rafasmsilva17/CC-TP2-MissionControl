package comms.packets;

public class MissionRequestPacket extends TLVPacket implements RoverPacket {
    public static final RoverPacketType type = RoverPacketType.REQUEST;

    public MissionRequestPacket(){
        super();
        writeByte((byte)type.toInteger());
    }
}
