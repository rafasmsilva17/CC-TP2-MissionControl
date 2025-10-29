package core.missions;

public class VideoMission extends Mission{
    private int[] position;
    private int direction;
    private int duration;

    public VideoMission(int[] position, int direction, int quantity){
        super();
        this.type = MissionType.VIDEO;
        this.position = position.clone();
        this.direction = direction;
        this.duration= duration;
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

    public int getQuantity() {
        return duration;
    }

    public void setQuantity(int quantity) {
        this.duration = quantity;
    }

    public int[] getMissionLinkData(){
        return new int[]{position[0], position[1], direction, duration};
    }

}
