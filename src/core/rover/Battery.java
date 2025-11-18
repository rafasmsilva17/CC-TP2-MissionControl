package core.rover;

public class Battery {
    private int MAX_CAPACITY = 100;
    private int charge = MAX_CAPACITY;

    private final int consumptionPerSecond = 1;
    private long lastTick;
    boolean charging = false;

    Battery(){
        this.lastTick = System.currentTimeMillis();
    }

    Battery(int maxCapacity, int charge){
        this.MAX_CAPACITY = maxCapacity;
        this.charge = charge;
    }

    public int getMaxCapacity(){ return this.MAX_CAPACITY; }
    public int getCharge(){ return this.charge; }

    public void setCharge(int newCharge){ this.charge = newCharge; }

    // retorna o estado de carregamento do rover
    public boolean tick(){
        long timeDiff = System.currentTimeMillis() - lastTick;
        if (charging){
            if ((double) timeDiff / 1000 >= 0.5){
                charge += consumptionPerSecond;
                lastTick = System.currentTimeMillis();
            }
        } else {
            if (timeDiff / 1000 >= 1){
                charge -= consumptionPerSecond;
                lastTick = System.currentTimeMillis();
            }
        }

        if (charge <= 5){
            charging = true;
            System.out.println("[STATUS] STOPPING TO CHARGE BATTERY");
        } else if (charge == MAX_CAPACITY) charging = false;

        return charging;
    }

    public boolean charging(){
        return charging;
    }
}
