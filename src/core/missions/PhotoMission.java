package core.missions;


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
        int numElementos = buf.get();

        try{
            if((int)buf.get() != 4)
                throw new IncorrectFieldSizeException("Incorrect Size for [position X]");
            position[0] = buf.getInt();
            if((int)buf.get() != 4)
                throw new IncorrectFieldSizeException("Incorrect Size for [position Y]");
            position[1] = buf.getInt();
            if((int)buf.get() != 4)
                throw new IncorrectFieldSizeException("Incorrect Size for [direction]");
            direction = buf.getFloat();
            if((int)buf.get() != 4)
                throw new IncorrectFieldSizeException("Incorrect Size for [quantity]");
            quantity = buf.getInt();

            int idSize = (int)buf.get();
            if (idSize < 0)
                throw new IncorrectFieldSizeException("Invalid Size for [Mission ID]");

            StringBuilder idBuild = new StringBuilder();
            for(int i = 0; i < idSize; i++){
                idBuild.append(buf.getChar());
            }
            id = idBuild.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
