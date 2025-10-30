package core.missions;


import comms.Encodable;

import java.nio.ByteBuffer;

public abstract class Mission implements Encodable {
    public static int ID_COUNTER = 0;

    public MissionType type;
    public String id;



    public Mission(){
       id = "M-".concat(String.format("%03d", ID_COUNTER));
       ID_COUNTER++;
    }

    public Mission(ByteBuffer buf){
        type = MissionType.fromInteger((int)buf.get());
        // Talvez um if/switch aqui para usar o construtor da missao dependendo do tipo
    }
}
