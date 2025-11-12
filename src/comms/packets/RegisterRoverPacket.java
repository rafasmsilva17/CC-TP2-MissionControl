package comms.packets;

public class RegisterRoverPacket extends TLVPacket implements RoverPacket{
    public static PacketType type = PacketType.REGISTER;

    public RegisterRoverPacket(int roverID){
       super();
       writeByte((byte)type.toByte());
       writeInt(roverID);
    }
}
