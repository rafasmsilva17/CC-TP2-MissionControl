package comms.packets;

public enum RoverPacketType {
    ACK,
    REQUEST;

    public static RoverPacketType fromInteger(int integer){
        return switch (integer){
            case 0 -> ACK;
            case 1 -> REQUEST;
            default -> null;
        };
    }

    public int toInteger(){
        return switch (this) {
            case ACK -> 0;
            case REQUEST -> 1;
            default -> -1;
        };
    }
}
