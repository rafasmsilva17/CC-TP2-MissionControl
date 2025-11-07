package core.missions;


import comms.Encoder;
import comms.packets.TLVPacket;

import java.nio.ByteBuffer;

public class PhotoMission extends Mission{
    private int[] position = new int[2];
    private float direction;
    private int quantity;

    public PhotoMission(int[] position, int direction, int quantity) {
        super();
        this.type = MissionType.PHOTO;
        this.position = position.clone();
        this.direction = direction;
        this.quantity = quantity;
    }

    public PhotoMission(ByteBuffer buf){
        super(buf);
        position = Encoder.decodeIntArray(buf);
        direction = Encoder.decodeFloat(buf);
        quantity = Encoder.decodeInt(buf);
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position.clone();
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public TLVPacket getEncodeData(){
        TLVPacket packet = new TLVPacket();
        packet.writeByte((byte)type.toInt());
        packet.writeString(id);

        packet.writeIntArray(position);
        packet.writeFloat(direction);
        packet.writeInt(quantity);
        return packet;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Mission ").append(id).append(" -> ");
        builder.append("Position ( ").append(position[0]).append(" , ")
                .append(position[1]).append(" ) | ");
        builder.append("Direction ").append(direction).append(" | ");
        builder.append("Quantity ").append(quantity);
        return builder.toString();
    }
}
