package core.missions.common;

public enum MissionStatus {
    WAITING,
    ASSIGNED,
    IN_PROGRESS,
    FINISHED,
    CANCELED;

    public static MissionStatus fromByte(byte b){
        return switch (b) {
          case 0x00 -> WAITING;
          case 0x01 -> ASSIGNED;
          case 0x02 -> IN_PROGRESS;
          case 0x03 -> FINISHED;
          case 0x04 -> CANCELED;
            default -> null;
        };
    }

    public byte toByte(){
        return switch (this){
            case WAITING -> 0x00;
            case ASSIGNED -> 0x01;
            case IN_PROGRESS -> 0x02;
            case FINISHED -> 0x03;
            case CANCELED -> 0x04;
        };
    }
}
