package patrician.save.objects;

/**
 * Created on 11/04/15.
 *
 * @author ptasha
 */
public enum ShipType {
    Snaikka(10),
    Crayer(16),
    Cog(30),
    Hulk(24);

    private final int sailorsSpace;

    ShipType(int sailorsSpace) {
        this.sailorsSpace = sailorsSpace;
    }

    public int getSailorsSpace() {
        return sailorsSpace;
    }
}
