package core.missions;

import comms.Encoder;
import comms.packets.TLVPacket;
import comms.telemetry.MissionTelemetry;
import comms.telemetry.TelemetryAnaliseSample;
import core.Rover;
import core.missions.common.MissionType;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Random;


public class AnaliseSampleMission extends Mission {

    private final String sample_ID;
    private int resultSize = -1;
    private byte[] result;
    private int counter = 0;
    private int bytesAnalized = 0;

    public AnaliseSampleMission(String sample_ID) {
        super();
        this.type = MissionType.ANALYSE_SAMPLE;
        this.sample_ID = sample_ID;
    }

    public AnaliseSampleMission(MissionType type, ByteBuffer buf) {
        super(type, buf);
        this.sample_ID = Encoder.decodeString(buf);
    }

    @Override
    public MissionTelemetry getTelemetry() {
        return new TelemetryAnaliseSample(type, this);
    }


    // Ao executar missão, já tem de ser garantido que o rover tem a sample
    @Override
    public boolean executeMission(Rover rover) {
        Random rand = new Random();
        if (resultSize == -1){
            resultSize = rand.nextInt(64);
            result = new byte[resultSize];
        }
        int bytesToAnalyze = rand.nextInt(resultSize / 10);
        for (int i = 0; i < bytesToAnalyze; i++) {
            result[counter++] = (byte)rand.nextInt(255);
            if (counter == resultSize) return true;
        }
        return counter == resultSize;
    }


    public String getSample_ID() {
        return this.sample_ID;
    }

    @Override
    public TLVPacket getEncodeData() {
        TLVPacket packet = super.getEncodeData();
        packet.writeString(this.sample_ID);
        return packet;
    }

    @Override
    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("Mission ").append(this.id).append(" -> ");
        var1.append("SampleID ").append(this.sample_ID);
        return var1.toString();
    }

    public byte[] getResult(){
        return result;
    }
}