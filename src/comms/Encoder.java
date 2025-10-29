package comms;

import core.missions.MissionType;
import core.missions.PhotoMission;

import java.nio.ByteBuffer;

public class Encoder {
    public static int SIZEOF_INTEGER = 4;

    private static void addToBuffer(ByteBuffer buf ,Class dataType, Object toAdd){
        buf.put((byte)(sizeof(dataType)));
        if(dataType == int.class    || dataType == Integer.class)   buf.putInt((int)toAdd);
        if(dataType == float.class  || dataType == Float.class)     buf.putFloat((float)toAdd);
        if(dataType == char.class   || dataType == Character.class) buf.putChar((char)toAdd);
        else return;
    }

    public static int sizeof(Class dataType){
        if(dataType == int.class    || dataType == Integer.class)   return 4;
        if(dataType == float.class  || dataType == Float.class)     return 4;
        if(dataType == char.class   || dataType == Character.class) return 2;
        else return 4;
    }

    private static ByteBuffer encodeMission(MissionType type, Object[] data, String missionID){
        int numOfBytes = 0;
        numOfBytes += missionID.length() * 2; // 2 bytes por character in java??? kys
        for(int i = 0; i < data.length; i+=2){
            numOfBytes += sizeof((Class)data[0]);
        }
        numOfBytes += 1 + 1 + (data.length / 2) + 1; // numElementos (1 byte)+ tamanho de cada elemento + idLength(1 byte)

        System.out.println("Buffer with " + numOfBytes);

        ByteBuffer encoded = ByteBuffer.allocate(numOfBytes);
        encoded.put((byte)(type.toInt()));
        encoded.put((byte)(data.length / 2));
        for(int i = 0; i < data.length; i+=2){
            addToBuffer(encoded, (Class)data[i], data[i+1]);
            //System.out.println(encoded);
        }

        encoded.put((byte)missionID.length()); // tamanho da string
        for(int i = 0; i < missionID.length(); i++){
            encoded.putChar(missionID.charAt(i));
            //System.out.println(encoded + " " + missionID.charAt(i));
        }
        return encoded;
    }

    // retorna ByteBuffer de forma:
    // numElementos | sizePos [posicao] | sizeDir [direcao] ...
    // id da missao (String) vai no fim
    public static ByteBuffer encodeMission(PhotoMission mission){
        return encodeMission(mission.type, mission.getEncodeData(), mission.id);
    }
}
