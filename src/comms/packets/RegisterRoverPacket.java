package comms.packets;

import comms.packets.common.PacketType;
import comms.packets.common.RoverPacket;
import comms.packets.common.TLVPacket;

public class RegisterRoverPacket extends TLVPacket implements RoverPacket {
    public static PacketType type = PacketType.REGISTER;

    // Constructor para fazer request de um ID
    public RegisterRoverPacket(){
        super();
        writeByte((byte)type.toByte());
    }

    // Constructor que a nave mae usa para mandar ao rover o seu ID
    public RegisterRoverPacket(int roverID){
       super();
       writeByte((byte)type.toByte());
       writeInt(roverID);
    }
}
