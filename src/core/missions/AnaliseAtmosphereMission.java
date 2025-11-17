package core.missions;

import comms.Encoder;
import comms.packets.TLVPacket;
import comms.telemetry.MissionTelemetry;
import comms.telemetry.TelemetryAnaliseAtmo;
import core.Rover;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;
import core.missions.common.Priority;

import java.nio.ByteBuffer;
import java.util.Random;


public class AnaliseAtmosphereMission extends Mission {

    private Coordinate position;

    private int resultSize = -1;
    private byte[] result;
    private int counter = 0;

    public AnaliseAtmosphereMission(Coordinate position, int maxDuration, Priority p){
        super(p, maxDuration);
        this.type = MissionType.ANALYSE_ATMO;
        this.position = position;
    }

    public AnaliseAtmosphereMission(MissionType type, ByteBuffer buf) {
        super(type, buf);
        this.position = Encoder.decodeCoordinate(buf);
    }

    @Override
    public MissionTelemetry getTelemetry() {
        return new TelemetryAnaliseAtmo(type, this);
    }

    @Override
    public boolean executeMission(Rover rover) {
        if (!roverArrived) roverArrived = rover.moveTowards(position);
        else {
            Random rand = new Random();
            if (resultSize == -1) {
                resultSize = rand.nextInt(10,256);
                result = new byte[resultSize];
            }
            int bytesToAnalyze = rand.nextInt(1,resultSize/5);
            for (int i = 0; i < bytesToAnalyze; i++){
                result[counter++] = (byte)rand.nextInt(255);
                if (counter == resultSize) return true;
            }
            return counter == resultSize;
        }
        return false;
    }


    public Coordinate getPosition() {
        return this.position;
    }

    public void setPosition(Coordinate var1) {
        this.position = var1;
    }

    public byte[] getResult(){
        return result;
    }

    @Override
    public TLVPacket getEncodeData() {
        TLVPacket packet = super.getEncodeData();
        packet.writeCoordinate(position);
        return packet;
    }

    @Override
    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("Mission ").append(this.id).append(" -> ");
        var1.append("Position ( ").append(this.position.getLatitude()).append(" , ").append(this.position.getLongitude()).append(" ) | ");
        return var1.toString();
    }
}