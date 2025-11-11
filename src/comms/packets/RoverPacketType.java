package comms.packets;

public enum RoverPacketType {
    ACK,
    REQUEST,
    REGISTER;

    public static RoverPacketType fromInteger(int integer){
        return switch (integer){
            case 0 -> ACK;
            case 1 -> REQUEST;
            case 2 -> REGISTER;
            default -> null;
        };
    }

    public int toInteger(){
        return switch (this) {
            case ACK -> 0;
            case REQUEST -> 1;
            case REGISTER -> 2;
            default -> -1;
        };
    }
}