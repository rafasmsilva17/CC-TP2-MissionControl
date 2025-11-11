package core.missions.common;

public enum Priority{
    LOW,
    NORMAL,
    URGENT;

    public static Priority fromInteger (int x){
        return switch(x){
            case 0 -> LOW;
            case 1 -> NORMAL;
            case 2 -> URGENT;
            default -> null;
        };
    }

    public int toInteger (){
        return switch(this){
            case LOW -> 0;
            case NORMAL -> 1;
            case URGENT -> 2;
            default -> -1;
        };
    }


}
