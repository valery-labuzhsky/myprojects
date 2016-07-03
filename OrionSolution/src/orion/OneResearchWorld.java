package orion;

/**
 * Created by ptasha on 15/11/14.
 */
public class OneResearchWorld {
    private final Universe universe;
    private final Planet planet;

    public OneResearchWorld() {
        this.universe = new Universe();
        this.planet = new Planet(12);
    }

    public void researchNBuild(Building building) {
        research(building);
        tune(building);
        build(building);
    }

    private void research(Building building) {
        // TODO
    }

    private void tune(Building building) {
//        int gain = getBuildingResearchGain(building);

        // TODO
    }

    private void build(Building building) {
        // TODO
    }

    public static void main(String[] args) {
        OneResearchWorld world = new OneResearchWorld();
        world.plan();
    }

    private void plan() {
//        Building.BUILDINGS
    }
}
