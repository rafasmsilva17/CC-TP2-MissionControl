package comms.packets;

public class MissionCancelPacket extends TLVPacket implements RoverPacket{
    public static final PacketType type = PacketType.MISSIONCANCEL;

    public MissionCancelPacket(){
        super();
        writeByte(type.toByte());
    }
}
