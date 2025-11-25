package core.missions;

import comms.Encoder;
import comms.packets.common.TLVPacket;
import comms.telemetry.MissionTelemetry;
import comms.telemetry.TelemetryDepositSample; 
import core.rover.Rover;
import core.missions.common.Coordinate;
import core.missions.common.MissionType;
import core.missions.common.Priority;

import java.nio.ByteBuffer;

public class DepositSampleMission extends Mission {

    private Coordinate position;
    private String sampleId; 
    private int depositTimeCounter = 0;
    private static final int DEPOSIT_DURATION = 5; 

  
    public DepositSampleMission(Coordinate position, String sampleId, int maxDuration, Priority p) {
        super(p, maxDuration);
        this.type = MissionType.DEPOSIT_SAMPLE; 
        this.position = position;
        this.sampleId = sampleId;
    }


    public DepositSampleMission(MissionType type, ByteBuffer buf) {
        super(type, buf);
        this.position = Encoder.decodeCoordinate(buf);
        this.sampleId = Encoder.decodeString(buf); 
    }

    @Override
    public MissionTelemetry getTelemetry() {
        return new TelemetryDepositSample(type, this);
    }

    @Override
    public boolean executeMission(Rover rover) {
        // 1. Fase de Movimento: Se ainda não chegou, move-se
        if (!roverArrived) {
            roverArrived = rover.moveTowards(position);
            return false;
        } 
        // 2. Fase de Execução: Chegou ao local
        else {
            rover.workingStatus(); // Indica que está a trabalhar
            
            // Simula o tempo de descarregar a amostra
            if (depositTimeCounter < DEPOSIT_DURATION) {
                depositTimeCounter++;
                return false;
            } else {
                // Ação concluída: remove a amostra do inventário do Rover (lógica hipotética)
                // rover.removeSample(this.sampleId); 
                return true; // Missão terminada
            }
        }
    }

    public Coordinate getPosition() {
        return this.position;
    }
    
    public String getSampleId() {
        return this.sampleId;
    }

    @Override
    public TLVPacket getEncodeData() {
        // Usa o método da superclasse e adiciona os dados específicos
        TLVPacket packet = super.getEncodeData();
        packet.writeCoordinate(position);
        packet.writeString(sampleId); // Escreve o ID da amostra
        return packet;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mission ").append(this.id).append(" -> ");
        sb.append("Deposit Sample ID: ").append(this.sampleId);
        sb.append(" at ( ").append(this.position.getLatitude())
          .append(" , ").append(this.position.getLongitude()).append(" )");
        return sb.toString();
    }
}
