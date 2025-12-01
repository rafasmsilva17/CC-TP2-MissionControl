package core.rover;

public enum RoverStatus {
    IDLE,
    MOVING,
    CHARGING,
    WORKING,
    COOLING;

    public static RoverStatus fromByte(byte b){
        return switch (b){
            case 0x00 -> IDLE;
            case 0x01 -> MOVING;
            case 0x02 -> CHARGING;
            case 0x03 -> WORKING;
            case 0x04 -> COOLING;
            default -> null;
        };
    }

    public byte toByte(){
        return switch (this){
            case IDLE -> 0x00;
            case MOVING -> 0x01;
            case CHARGING -> 0x02;
            case WORKING -> 0x03;
            case COOLING -> 0x04;
        };
    }
}
