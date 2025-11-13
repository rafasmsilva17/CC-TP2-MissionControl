package comms;

import comms.packets.TLVPacket;
import core.missions.common.Coordinate;
import core.missions.common.IncorrectFieldSizeException;
import core.missions.common.IncorrectFieldTypeException;
import core.missions.Mission;

import java.nio.ByteBuffer;

public class Encoder {
    public static byte BYTE_TYPE = 0x00;
    public static byte INTEGER_TYPE = 0x01;
    public static byte FLOAT_TYPE = 0x02;
    public static byte DOUBLE_TYPE = 0x03;
    public static byte CHAR_TYPE = 0x04;
    public static byte STRING_TYPE = 0x05;
    public static byte COORDINATE_TYPE = 0x06;
    public static byte ARRAY_TYPE = 0x10;

    @Deprecated
    public static byte sizeof(byte type){
        if(type == BYTE_TYPE)       return 1; // isto nao vai ser usado
        if(type == INTEGER_TYPE)    return 4;
        if(type == FLOAT_TYPE)      return 4;
        if(type == DOUBLE_TYPE)     return 8;
        if(type == CHAR_TYPE)       return 1;
        if(type == ARRAY_TYPE)      return 1;
        else return 0; // quem fizer sizeof de uma string que se atire de uma ponte
    }

    public static byte[] encode(Encodable e_obj){
        return e_obj.getEncodeData().getBuffer();
    }

    public static TLVPacket encodeToPacket(Encodable e_obj) { return e_obj.getEncodeData(); }

    public static byte[] encodeMission(Mission mission){
        return encode(mission);
    }

    public static byte decodeByteFull(ByteBuffer buffer){
        try{
            if(buffer.get() != BYTE_TYPE)
                throw new IncorrectFieldTypeException("On decoding byte at " + buffer.arrayOffset());
            return buffer.get();
        } catch (IncorrectFieldTypeException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte decodeByte(ByteBuffer buffer){
        return buffer.get();
    }

    public static int decodeIntFull(ByteBuffer buffer){
        try {
            if(buffer.get() != INTEGER_TYPE)
                throw new IncorrectFieldTypeException("On decoding integer at " + buffer.arrayOffset());
            return buffer.getInt();
        } catch (IncorrectFieldTypeException | IncorrectFieldSizeException e) {
            throw new RuntimeException(e);
        }
    }

    public static int decodeInt(ByteBuffer buffer){
        return buffer.getInt();
    }


    public static float decodeFloatFull(ByteBuffer buffer){
        try {
            if(buffer.get() != FLOAT_TYPE)
                throw new IncorrectFieldTypeException("On decoding float at " + buffer.arrayOffset());
            return buffer.getFloat();
        } catch (IncorrectFieldTypeException | IncorrectFieldSizeException e) {
            throw new RuntimeException(e);
        }
    }

    public static float decodeFloat(ByteBuffer buffer){
        return buffer.getFloat();
    }

    public static String decodeStringFull(ByteBuffer buffer){
        try {
            if(buffer.get() != STRING_TYPE)
                throw new IncorrectFieldTypeException("On decoding string at " + buffer.arrayOffset());
            return decodeString(buffer);
        } catch (IncorrectFieldTypeException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decodeString(ByteBuffer buffer){
        // 2 bytes para o tamanho da string
        int stringLen = (buffer.get() << 8 | buffer.get());
        StringBuilder strBuild = new StringBuilder();
        for (int i = 0; i < stringLen; i++){
            strBuild.append((char)buffer.get());
        }
        return strBuild.toString();
    }

    // Array decoding
    public static int[] decodeIntArray(ByteBuffer buffer){
        int arrayLen = (buffer.get() << 8 | buffer.get());
        int[] decoded = new int[arrayLen];
        for(int i = 0; i < arrayLen; i++){
            decoded[i] = buffer.getInt();
        }
        return decoded;
    }

    public static String[] decodeStringArray(ByteBuffer buffer){
        int arrayLen = (buffer.get() << 8 | buffer.get());
        String[] decoded = new String[arrayLen];
        for (int i = 0; i < arrayLen; i++) decoded[i] = decodeString(buffer);
        return decoded;
    }

    public static Coordinate decodeCoordinateFull(ByteBuffer buffer){
        try{
            if(buffer.get() != COORDINATE_TYPE)
                throw new IncorrectFieldTypeException("On decoding array at " + buffer.arrayOffset());
            return decodeCoordinate(buffer);
        }catch (IncorrectFieldTypeException e){
            throw new RuntimeException(e);

        }
    }

    public static Coordinate decodeCoordinate(ByteBuffer buffer){
        float latitude = buffer.getFloat();
        float longitude = buffer.getFloat();
        return new Coordinate(latitude, longitude);
    }

}
