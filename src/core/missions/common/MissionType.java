package core.missions.common;

public enum MissionType {
    PHOTO,
    VIDEO,
    GET_SAMPLE,
    ANALYSE_SAMPLE,
    DEPOSIT_SAMPLE,
    ANALYSE_ATMO;

    public static MissionType fromByte(byte x){
        return switch (x) {
            case 0 -> PHOTO;
            case 1 -> VIDEO;
            case 2 -> GET_SAMPLE;
            case 3 -> ANALYSE_SAMPLE;
            case 4 -> DEPOSIT_SAMPLE;
            case 5 -> ANALYSE_ATMO;
            default -> null;
        };
    }

    public byte toByte(){
        return switch (this) {
            case PHOTO -> 0;
            case VIDEO -> 1;
            case GET_SAMPLE -> 2;
            case ANALYSE_SAMPLE -> 3;
            case DEPOSIT_SAMPLE -> 4;
            case ANALYSE_ATMO -> 5;
        };
    }

}



