package orion;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ptasha on 09/11/14.
 */
public class Planet {
    private final int maxPopulation;

    private final Set<Building> buildings = new HashSet<>();

    private int population;

    private int babies;

    private int farmers;
    private int workers;
    private int scientists;

    private int production;

    public Planet(int maxPopulation) {
        this.maxPopulation = maxPopulation;
    }
}
