package core.missions;


import comms.Encoder;
import comms.packets.TLVPacket;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;
import core.missions.common.Priority;

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

    public PhotoMission(Coordinate position,
                        int direction,
                        int quantity,
                        int duracaoMaxima,
                        Priority p) {
        super(p, duracaoMaxima);
        this.type = MissionType.PHOTO;
        this.position = position;
        this.direction = direction;
        this.quantity = quantity;
    }

    protected PhotoMission(MissionType type, ByteBuffer buf){
        super(type, buf);
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
        TLVPacket packet = super.getEncodeData();

        packet.writeCoordinate(position);
        packet.writeFloat(direction);
        packet.writeInt(quantity);
        return packet;
    }

    public String toString(){
        return "Mission " + id + " -> " +
                "Priority " + priority + " -> " +
                "Maximum Duration : " + maxDuration + " minutes | " +
                "Updates every " + updateInterval + " seconds | " +
                "Position ( " + position.getLatitude() + " , " +
                position.getLongitude() + " ) | " +
                "Direction " + direction + " | " +
                "Quantity " + quantity;
    }
}
