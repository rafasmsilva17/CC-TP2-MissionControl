package comms.telemetry;

import core.missions.Coordinate;

public class TelemetryVideo extends MissionTelemetry {
    public Coordinate coord;
    public double direction;
    public int duration; //Em segundos

    TelemetryVideo(Coordinate coord, double direction, int duration){
        this.coord = coord;
        this.direction = direction;
        this.duration = duration;
    }
}
