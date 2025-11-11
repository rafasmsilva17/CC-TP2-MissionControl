package core.missions;

public class Sample {
    private static int sample_Id_Counter = 1;
    private int id;

    public Sample(){
        this.id = sample_Id_Counter;
        sample_Id_Counter++;
    }
}
