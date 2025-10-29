package core.missions;

import comms.Encodable;
import comms.Encoder;

import java.nio.ByteBuffer;

public class PhotoMission extends Mission{
    private int[] position;
    private float direction;
    private int quantity;

    public PhotoMission(int[] position, int direction, int quantity) {
        super();
        this.type = MissionType.PHOTO;
        this.position = position.clone();
        this.direction = direction;
        this.quantity = quantity;
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


    // Indices pares = tipo de dados
    // Indices impares = dados
    @Override
    public Object[] getEncodeData() {
        Object[] data = new Object[8];
        data[0] = int.class; data[1] = position[0];
        data[2] = int.class; data[3] = position[1];
        data[4] = float.class; data[5] = direction;
        data[6] = int.class; data[7] = quantity;
        return data;
    }
}
