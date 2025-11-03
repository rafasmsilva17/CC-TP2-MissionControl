package core.missions;

public class VideoMission extends Mission{
    private int[] position;
    private int direction;
    private int duration;

    public VideoMission(int[] position, int direction, int duration){
        super();
        this.type = MissionType.VIDEO;
        this.position = position.clone();
        this.direction = direction;
        this.duration = duration;
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position.clone();
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDuration() { return duration; }

    public void setDuration(int duration) { this.duration = duration; }

    @Override
    public Object[] getEncodeData() {
        Object[] data = new Object[8];
        data[0] = int.class;    data[1] = position[0];
        data[2] = int.class;    data[3] = position[1];
        data[4] = int.class;    data[5] = direction;
        data[6] = int.class;    data[7] = duration;
        return data;
    }
}
