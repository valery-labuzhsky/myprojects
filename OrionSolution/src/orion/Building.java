package orion;

/**
 * Created by ptasha on 09/11/14.
 */
public class Building {
    public static final Building COLONY_BASE = new Building(200, 5);
    public static final Building HYDROPONIC_FARM = new Building(60, 2, ResearchLine.Constants.HYDROPONIC_FARM);
    public static final Building BIOSPHERES = new Building();

    public static final Building[] BUILDINGS = new Building[] {
            COLONY_BASE
    };

    private final int cost;
    private final int maintenance;
    private String[] research;

    public Building(int cost, int maintenance, String... research) {
        this.cost = cost;
        this.maintenance = maintenance;
        this.research = research;
    }
}
