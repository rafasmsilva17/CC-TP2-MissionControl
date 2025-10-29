package core;

public class Battery {
    private int MAX_CAPACITY = 100;
    private int charge = MAX_CAPACITY;

    Battery(){}

    Battery(Battery originBattery){
        this.MAX_CAPACITY = originBattery.getMaxCapacity();
    }

    Battery(int maxCapacity, int charge){
        this.MAX_CAPACITY = maxCapacity;
        this.charge = charge;
    }

    // Getters //
    public int getMaxCapacity(){ return this.MAX_CAPACITY; }
    public int getCharge(){ return this.charge; }

    // Setters //
    public void setCharge(int newCharge){ this.charge = newCharge; }


    public int addCharge(int amount){
        charge += amount;
        return charge;
    }

    public int removeCharge(int amount){
        charge -= amount;
        return charge;
    }

    public void fullCharge(){ charge = MAX_CAPACITY; }
}
