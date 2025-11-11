package core.missions;


import comms.Encoder;
import comms.packets.TLVPacket;

import java.nio.ByteBuffer;

public class PhotoMission extends Mission{
    private Coordinate position;
    private float direction;
    private int quantity;

    public PhotoMission(Coordinate position, int direction, int quantity) {
        super();
        this.type = MissionType.PHOTO;
        this.position = position;
        this.direction = direction;
        this.quantity = quantity;
    }

    public PhotoMission(ByteBuffer buf){
        super(buf);
        position = Encoder.decodeCoordinate(buf);
        direction = Encoder.decodeFloat(buf);
        quantity = Encoder.decodeInt(buf);
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
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

        packet.writeCoordinate(position);
        packet.writeFloat(direction);
        packet.writeInt(quantity);
        return packet;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Mission ").append(id).append(" -> ");
        builder.append("Position ( ").append(position.getLatitude()).append(" , ")
                .append(position.getLongitude()).append(" ) | ");
        builder.append("Direction ").append(direction).append(" | ");
        builder.append("Quantity ").append(quantity);
        return builder.toString();
    }
}
