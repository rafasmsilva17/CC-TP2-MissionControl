package comms.packets;

import comms.Encoder;

import java.nio.charset.StandardCharsets;

public class TLVPacket {
    // tamanho necessário para escrever cada tipo de dados.
    // isto nao é necessário porque getBuffer() já retorna um buffer com o tamanho minimo
    // mas quem quiser poupar memoria(temporária) pode usar
    public final int SIZEFOR_INTEGER    = 6;
    public final int SIZEFOR_FLOAT      = 6;
    public final int SIZEFOR_BYTE       = 2;

    public int offset = 0;
    private byte[] buffer;

    public TLVPacket(){
        buffer = new byte[1024];
    }

    public TLVPacket(int capacity){
       buffer = new byte[capacity];
    }

    public byte[] getBuffer(){
        byte[] cleanBuf = new byte[offset];
        System.arraycopy(buffer, 0, cleanBuf, 0, offset);
        return cleanBuf;
    }

    // Esta funcao nao deve ser usada sozinha!
    private void writeIntBytes(int toWrite){
        // write int
        buffer[offset++] = (byte)(toWrite >>> 24);
        buffer[offset++] = (byte)(toWrite >>> 16);
        buffer[offset++] = (byte)(toWrite >>> 8);
        buffer[offset++] = (byte)(toWrite);
    }

    public void writeByte(byte toWrite){
        buffer[offset++] = Encoder.BYTE_TYPE;
        buffer[offset++] = toWrite;
    }

    public void writeInt(int toWrite){
        buffer[offset++] = Encoder.INTEGER_TYPE;
        buffer[offset++] = Encoder.sizeof(Encoder.INTEGER_TYPE);
        writeIntBytes(toWrite);
    }

    public void writeFloat(float toWrite){
        buffer[offset++] = Encoder.FLOAT_TYPE;
        buffer[offset++] = Encoder.sizeof(Encoder.FLOAT_TYPE);
        //write float
        writeIntBytes(Float.floatToIntBits(toWrite));
    }

    public void writeString(String toWrite){
        buffer[offset++] = Encoder.STRING_TYPE;
        // 2 bytes para numero de caracteres (32767 caracteres)
        // quem quiser escrever mais devia ter ido para Letras
        buffer[offset++] = (byte)(toWrite.length() >>> 8);
        buffer[offset++] = (byte)(toWrite.length());

        // Caracteres em Java usam 2 bytes. Mas 1 byte chega.
        // A nao ser que algum de vos fale chines
        byte[] stringBytes = toWrite.getBytes(StandardCharsets.UTF_8);
        for (byte stringByte : stringBytes) {
            buffer[offset++] = stringByte;
        }
    }

    // write Arrays
    public void writeIntArray(int[] toWrite){
        buffer[offset++] = Encoder.ARRAY_TYPE;
        buffer[offset++] = Encoder.INTEGER_TYPE;
        // Dois bytes para tamanho do array (32767 elementos)
        buffer[offset++] = (byte)(toWrite.length >>> 8);
        buffer[offset++] = (byte)(toWrite.length);

        for (int i : toWrite) {
            writeIntBytes(i);
        }
    }
}
