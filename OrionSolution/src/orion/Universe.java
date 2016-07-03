package orion;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ptasha on 09/11/14.
 */
public class Universe {
    private int money = 50;

    private final ArrayList<Planet> planets = new ArrayList<>();

    private final ResearchLine[] researchProgress = new ResearchLine[]{};

    private ResearchLine research;

    private int science;

    public void addMoney(int money) {
        this.money += money;
    }

    public void addFood(int food) {
    }

    public void addScience(int science) {
        this.science += science;
    }

    public static void main(String[] args) {
        Universe universe = new Universe();
        universe.addPlanet(new Planet(12));

    }

    private void addPlanet(Planet planet) {
        planets.add(planet);
    }

    public Collection<Universe> makeDecisions() {
        return null; // TODO
    }

    public void turn() {

    }
}
