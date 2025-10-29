package core.missions;


import comms.Encodable;

public abstract class Mission implements Encodable {
    public static int ID_COUNTER = 0;

    public MissionType type;
    public final String id;



    public Mission(){
       id = "M-".concat(String.format("%03d", ID_COUNTER));
       ID_COUNTER++;
    }

}
