package comms.rovertelemetry;

import core.missions.common.Coordinate;
import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.List;

public class RoverTelemetry {
    private int roverID;
    private int battery;
    private Coordinate coord;
    private float currSpeed;
    private List<Integer> missionBuffer = new ArrayList<>();
    private int currMission;
    private String status;

    public List<Attr> getAttributes(){
        List<Attr> attributes = new ArrayList<>();
        return attributes;
    }
}
