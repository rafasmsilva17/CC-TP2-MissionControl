package comms.packets;

public class RegisterRoverPacket extends TLVPacket implements RoverPacket{
    public static RoverPacketType type = RoverPacketType.REGISTER;

    public RegisterRoverPacket(int roverID){
       super();
       writeByte((byte)type.toInteger());
       writeInt(roverID);
    }
}
