package core.missions;

import comms.TLVPacket;

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
    public TLVPacket getEncodeData() {
        return null;
    }
}
