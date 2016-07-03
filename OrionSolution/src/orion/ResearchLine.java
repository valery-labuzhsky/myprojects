package orion;

/**
 * Created by ptasha on 09/11/14.
 */
public enum ResearchLine {
    CONSTRUCTION(new ResearchStep(80, "Anti-Missile Rockets", "Fighter Bays")),

    CHEMISTRY(
            new ResearchStep(50, "Nuclear Missile", "Standard Fuel Cells", "Titanium Armor")),

    COMPUTERS(new ResearchStep(50, "Electronic Computer")),

    PHYSICS(new ResearchStep(50, "Laser Cannon", "Laser Rifle", "Space Scanner")),

    POWER(
            new ResearchStep(50, "Freighters", "Nuclear Bomb" ,"Nuclear Drive")),

    SOCIOLOGY(new ResearchStep(150, "Space Academy")),

    BIOLOGY(
            new ResearchStep(80, Constants.BIOSPHERES, Constants.HYDROPONIC_FARM)),

    FORCE_FIELDS(new ResearchStep(250, "Class I Shield", "Ecm Jammer", "Mass Driver"));

    private final ResearchStep[] line;

    ResearchLine(ResearchStep... line) {
        this.line = line;
    }

    public static class Constants {
        public static final String HYDROPONIC_FARM = "Hydroponic Farm";
        public static final String BIOSPHERES = "Biospheres";
    }
}
