package comms.telemetry;

import core.missions.VideoMission;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;

public class TelemetryVideo extends MissionTelemetry {
    public Coordinate coord;
    public int direction;
    public int duration; //Em segundos

    TelemetryVideo(Coordinate coord, int direction, int duration){
        this.coord = coord;
        this.direction = direction;
        this.duration = duration;
    }

    public TelemetryVideo(MissionType type, VideoMission mission) {
        super(type, mission);
        this.coord = mission.getPosition();
        this.direction = mission.getDirection();
        this.duration = mission.getDuration();
    }
}
