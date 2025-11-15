package core.missions;

import comms.Encoder;
import comms.packets.TLVPacket;
import comms.telemetry.MissionTelemetry;
import core.Rover;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;

import java.nio.ByteBuffer;


public class AnaliseAtmosphereMission extends Mission {

    private Coordinate position;
    private int duration;   


    public AnaliseAtmosphereMission(Coordinate var1, int var2) {
        // Você precisará adicionar ANALISE_ATMOSPHERE ao seu enum MissionType
        this.type = MissionType.ANALYSE_ATMO;
        this.position = var1;
        this.duration = var2;
    }

    /**
     * Construtor para decodificar a missão a partir de um ByteBuffer.
     */
    public AnaliseAtmosphereMission(MissionType type, ByteBuffer var1) {
        super(type, var1);
        this.position = Encoder.decodeCoordinate(var1);
        this.duration = Encoder.decodeInt(var1);
    }

    @Override
    public MissionTelemetry getTelemetry() {
        return null;
    }

    @Override
    public boolean executeMission(Rover rover) {
        return false;
    }


    public Coordinate getPosition() {
        return this.position;
    }

    public void setPosition(Coordinate var1) {
        this.position = var1;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int var1) {
        this.duration = var1;
    }


    @Override
    public TLVPacket getEncodeData() {
        TLVPacket var1 = new TLVPacket();
        var1.writeByte((byte)this.type.toInt());
        var1.writeString(this.id);
        var1.writeCoordinate(this.position);
        var1.writeInt(this.duration);
        return var1;
    }

    @Override
    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("Mission ").append(this.id).append(" -> ");
        var1.append("Position ( ").append(this.position.getLatitude()).append(" , ").append(this.position.getLongitude()).append(" ) | ");
        var1.append("Duration ").append(this.duration);
        return var1.toString();
    }
}