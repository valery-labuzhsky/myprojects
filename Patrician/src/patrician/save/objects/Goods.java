package patrician.save.objects;

/**
 * Created on 10/03/15.
 *
 * @author ptasha
 */
public enum Goods {
    GRAIN(2000),
    MEAT(2000),
    FISH(2000),
    BEER(200),
    SALT(200),
    HONEY(200),
    SPICES(200),
    WINE(200),
    CLOTH(200),
    SKINS(200),
    WHALE_OIL(200),
    TIMBER(2000),
    IRON_GOODS(200),
    LEATHER(200),
    WOOL(2000),
    PITCH(200),
    PIG_IRON(2000),
    HEMP(2000),
    POTTERY(200),
    BRICKS(2000),
    SWORD(10),
    BOW(10),
    HIDDEN3(10),
    HIDDEN4(10);

    private final int unitSize;

    Goods(int unitSize) {
        this.unitSize = unitSize;
    }

    public int getUnitSize() {
        return unitSize;
    }
}
