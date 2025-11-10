package comms.telemetry;

import core.missions.Coordinate;

public class TelemetryAnaliseSample extends MissionTelemetry {
    public Coordinate coord;
    public int quantity;
    public double radiusInM;

    TelemetryAnaliseSample(Coordinate coord, int quantity, double radiusInM){
        this.coord = coord;
        this.quantity = quantity;
        this.radiusInM = radiusInM;
    }
}