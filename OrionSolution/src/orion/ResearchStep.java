package orion;

/**
 * Created by ptasha on 09/11/14.
 */
public class ResearchStep {
    private final String[] researches;
    private final int cost;

    public ResearchStep(int cost, String... researches) {
        this.cost = cost;
        this.researches = researches;
    }
}
