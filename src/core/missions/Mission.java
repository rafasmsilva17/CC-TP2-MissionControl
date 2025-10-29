package core.missions;


public abstract class Mission {
    public static int ID_COUNTER = 0;

    public MissionType type;
    public final String id;



    public Mission(){
       id = "M-".concat(String.format("%03d", ID_COUNTER));
       ID_COUNTER++;
    }

}
