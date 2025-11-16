package core.missions;

import comms.Encoder;
import comms.packets.TLVPacket;
import comms.telemetry.MissionTelemetry;
import comms.telemetry.TelemetryGetSample;
import core.Rover;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;
import core.missions.common.Priority;
import core.missions.common.Sample;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GetSampleMission extends Mission{
    private Coordinate position;
    private int quantity;
    private float radius;

    private boolean roverArrived = false;
    private float currentSampleProgress = 0;
    private Coordinate currentSamplePosition = null;
    private boolean roverArrivedAtSample = false;
    private final List<Sample> samplesCollected = new ArrayList<>();


    public GetSampleMission(Coordinate position, int quantity, float radius, int maxDuration, Priority p){
        super(p, maxDuration);
        this.type = MissionType.GET_SAMPLE;
        this.position = position;
        this.quantity = quantity;
        this.radius = radius;
    }

    public GetSampleMission(MissionType type, ByteBuffer buf){
        super(type, buf);
        position = Encoder.decodeCoordinate(buf);
        quantity = Encoder.decodeInt(buf);
        radius = Encoder.decodeFloat(buf);
    }


    @Override
    public MissionTelemetry getTelemetry() {
        return new TelemetryGetSample(type, this);
    }

    /// TODO Funcao está error prone
    /// Precisa de ter clamping ao calcular posicao da amostra
    /// Precisa que a nova posicao da amostra tbm possa ser negativo (senao apenas pode aumentar á posicao)
    @Override
    public boolean executeMission(Rover rover) {
        if (!roverArrived) roverArrived = rover.moveTowards(position);
        else {
            Random rand = new Random();
            if (currentSamplePosition == null){
                currentSamplePosition =
                        new Coordinate(position.getLatitude() + rand.nextFloat(radius),
                                position.getLongitude() + rand.nextFloat(radius));
            }
            if (!roverArrivedAtSample){
                roverArrivedAtSample = rover.moveTowards(currentSamplePosition);
            } else {
                currentSampleProgress += rand.nextInt(15);
                if (currentSampleProgress >= 100){
                    Sample newSample = new Sample(rover.getId());
                    rover.addSample(newSample);
                    samplesCollected.add(newSample);
                    currentSampleProgress = 0;
                }
                if (samplesCollected.size() == quantity){
                    finish();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public TLVPacket getEncodeData() {
        TLVPacket packet = super.getEncodeData();

        packet.writeCoordinate(position);
        packet.writeInt(quantity);
        packet.writeFloat(radius);
        return packet;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getSamplesDone(){ return samplesCollected.size(); }

    public List<String> getCollectedSamplesIDS(){
        return new ArrayList<>(samplesCollected.stream().map(Sample::getId).toList());
    }
}
