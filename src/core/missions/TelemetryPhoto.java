package core.missions;

public class TelemetryPhoto extends MissionTelemetry{
    public Coordinate coord;
    public double direction;
    public int quantity;

    TelemetryPhoto(Coordinate coord, double direction, int quantity){
        this.coord = coord;
        this.direction = direction;
        this.quantity = quantity;
    }
}
