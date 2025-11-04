package core.missions;

public class TelemetryGetSample extends MissionTelemetry{
    public Coordinate coord;
    public int quantity;
    public double radiusInM;

    TelemetryGetSample(Coordinate coord, int quantity, double radiusInM){
        this.coord = coord;
        this.quantity = quantity;
        this.radiusInM = radiusInM;
    }
}
