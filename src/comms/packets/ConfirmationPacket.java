package comms.packets;

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
