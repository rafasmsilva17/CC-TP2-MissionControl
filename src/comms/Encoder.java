package comms;

import core.missions.Coordinate;
import core.missions.IncorrectFieldSizeException;
import core.missions.IncorrectFieldTypeException;
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

    public static byte[] encodeMission(Mission mission){
        return encode(mission);
    }

    public static byte decodeByte(ByteBuffer buffer){
        try{
            if(buffer.get() != BYTE_TYPE)
                throw new IncorrectFieldTypeException("On decoding byte at " + buffer.arrayOffset());
            return buffer.get();
        } catch (IncorrectFieldTypeException e) {
            throw new RuntimeException(e);
        }
    }

    public static int decodeInt(ByteBuffer buffer){
        try {
            if(buffer.get() != INTEGER_TYPE)
                throw new IncorrectFieldTypeException("On decoding integer at " + buffer.arrayOffset());
            return buffer.getInt();
        } catch (IncorrectFieldTypeException | IncorrectFieldSizeException e) {
            throw new RuntimeException(e);
        }
    }

    public static float decodeFloat(ByteBuffer buffer){
        try {
            if(buffer.get() != FLOAT_TYPE)
                throw new IncorrectFieldTypeException("On decoding float at " + buffer.arrayOffset());
            return buffer.getFloat();
        } catch (IncorrectFieldTypeException | IncorrectFieldSizeException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decodeString(ByteBuffer buffer){
        try {
            if(buffer.get() != STRING_TYPE)
                throw new IncorrectFieldTypeException("On decoding string at " + buffer.arrayOffset());
            // 2 bytes para o tamanho da string
            int stringLen = (buffer.get() << 8 | buffer.get());
            StringBuilder strBuild = new StringBuilder();
            for (int i = 0; i < stringLen; i++){
                strBuild.append((char)buffer.get());
            }
            return strBuild.toString();

        } catch (IncorrectFieldTypeException e) {
            throw new RuntimeException(e);
        }
    }

    // Array decoding
    public static int[] decodeIntArray(ByteBuffer buffer){
        try {
            if(buffer.get() != ARRAY_TYPE)
                throw new IncorrectFieldTypeException("On decoding array at " + buffer.arrayOffset());
            if(buffer.get() != INTEGER_TYPE)
                throw new IncorrectFieldTypeException("On decoding array at " + buffer.arrayOffset());
            int arrayLen = (buffer.get() << 8 | buffer.get());
            int[] decoded = new int[arrayLen];

            for(int i = 0; i < arrayLen; i++){
                decoded[i] = buffer.getInt();
            }

            return decoded;
        } catch (IncorrectFieldTypeException e) {
            throw new RuntimeException(e);
        }
    }

    public static Coordinate decodeCoordinate(ByteBuffer buffer){
        try{
            if(buffer.get() != COORDINATE_TYPE)
                throw new IncorrectFieldTypeException("On decoding array at " + buffer.arrayOffset());

            float latitude = buffer.getFloat();
            float longitude = buffer.getFloat();

            return new Coordinate(latitude, longitude);
        
        }catch (IncorrectFieldTypeException e){
            throw new RuntimeException(e);

        }
    }

}
