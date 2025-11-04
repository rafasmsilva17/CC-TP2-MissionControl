package core;

import core.missions.Mission;
import core.missions.Sample;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Rover {
    private static int id_counter = 0;
    private int id;
    private Battery battery = new Battery();
    private PriorityQueue<Mission> priorityQueue = new PriorityQueue<>(); //necessario criar comparator ?
    private Set<Sample> collectedSamples; //amostras atualmente no rover

}
