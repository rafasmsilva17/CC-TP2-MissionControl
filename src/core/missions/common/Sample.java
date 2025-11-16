package core.missions.common;

import java.util.Objects;

public class Sample {
    private static int sample_Id_Counter = 1;
    private String id;

    public Sample(int roverID){
        this.id = roverID + "-" + sample_Id_Counter;
        sample_Id_Counter++;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Sample sample = (Sample) o;
        return Objects.equals(id, sample.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
