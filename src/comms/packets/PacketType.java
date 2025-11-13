package comms.packets;

public enum PacketType {
    ACK,
    REQUEST,
    REGISTER,
    MISSION,
    MISSIONTELEMETRY;

    public static PacketType fromByte(byte b){
        return switch (b){
            case 0x00 -> ACK;
            case 0x01 -> REQUEST;
            case 0x02 -> REGISTER;
            case 0x03 -> MISSION;
            case 0x04 -> MISSIONTELEMETRY;
            default -> null;
        };
    }

    public byte toByte(){
        return switch (this) {
            case ACK -> 0x00;
            case REQUEST -> 0x01;
            case REGISTER -> 0x02;
            case MISSION -> 0x03;
            case MISSIONTELEMETRY -> 0x04;
        };
    }
}