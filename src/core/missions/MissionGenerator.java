package core.missions;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.random.*;

import core.missions.common.Coordinate;
import core.missions.common.MissionType;
import core.missions.common.Priority;


public class MissionGenerator {
    private Random rd = new Random();
    MissionType[] types = MissionType.values();
    List<MissionType> typesL = new ArrayList<>(Arrays.asList(types));
    Priority[] priorities = Priority.values();

    private int maxMissionDuration = 600;
    private int maxPhotos = 10;
    private int maxDegreeDir = 360;
    private int maxVideoDur = 30;
    private int maxSamples = 5;
    private float maxRadius = 30;

    public Mission generateRandomMisson(){
        typesL.remove(MissionType.ANALYSE_SAMPLE);
        typesL.remove(MissionType.DEPOSIT_SAMPLE);
        int missionTypeIndex = rd.nextInt(typesL.size());
        MissionType mt = typesL.get(missionTypeIndex);
        int priorityIndex = rd.nextInt(priorities.length);
        Priority p = priorities[priorityIndex];
        int maxMissionDur = rd.nextInt(300, maxMissionDuration);
        Mission result = null;
        switch (mt) {
            case PHOTO:
                float pm_lat = rd.nextFloat(Coordinate.minLat, Coordinate.maxLat);
                float pm_lon = rd.nextFloat(Coordinate.minLon, Coordinate.maxLon);
                Coordinate pm_Coordinate = new Coordinate(pm_lat, pm_lon);
                int pm_direction = rd.nextInt(maxDegreeDir);
                int quantity = rd.nextInt(maxPhotos);
                
                result = new PhotoMission(pm_Coordinate, pm_direction, quantity, maxMissionDur, p);
                break;
            case VIDEO:
                float vm_lat = rd.nextFloat(Coordinate.minLat, Coordinate.maxLat);
                float vm_lon = rd.nextFloat(Coordinate.minLon, Coordinate.maxLon);
                Coordinate vm_Coordinate = new Coordinate(vm_lat, vm_lon);
                int vm_direction = rd.nextInt(maxDegreeDir);
                int vm_dur = rd.nextInt(maxVideoDur);

                result = new VideoMission(vm_Coordinate, vm_direction, vm_dur, maxMissionDur, p);
                
                break;

            case GET_SAMPLE:
                float gsm_lat = rd.nextFloat(Coordinate.minLat, Coordinate.maxLat);
                float gsm_lon = rd.nextFloat(Coordinate.minLon, Coordinate.maxLon);
                Coordinate gsm_Coordinate = new Coordinate(gsm_lat, gsm_lon);
                int gsm_quantity = rd.nextInt(maxSamples);
                float gsm_radius = rd.nextFloat(0, maxRadius);
                

                result = new GetSampleMission(gsm_Coordinate, gsm_quantity, gsm_radius, maxMissionDur, p);
                break;
                
            case ANALYSE_ATMO:
                float aam_lat = rd.nextFloat(Coordinate.minLat, Coordinate.maxLat);
                float aam_lon = rd.nextFloat(Coordinate.minLon, Coordinate.maxLon);
                Coordinate aam_Coordinate = new Coordinate(aam_lat, aam_lon);
                
                result = new AnaliseAtmosphereMission(aam_Coordinate, maxMissionDur, p);
                break;
        
            default:
                System.out.println("[MOTHERSHIP]: Request for mission, using invalid mission type.");
                break;
        }
        return result;
    }
}
