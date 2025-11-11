package comms.packets;

public class ConfirmationPacket extends TLVPacket implements RoverPacket {
    public static final RoverPacketType type = RoverPacketType.ACK;

    public ConfirmationPacket(int roverID, String missionID){
        super();
        writeByte((byte)type.toInteger());
        writeInt(roverID);
        writeString(missionID);
    }
}
