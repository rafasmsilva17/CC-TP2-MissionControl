package comms.packets;

public class ConfirmationPacket extends TLVPacket{

    public ConfirmationPacket(int roverID, String missionID){
        super();
        writeInt(roverID);
        writeString(missionID);
    }
}
