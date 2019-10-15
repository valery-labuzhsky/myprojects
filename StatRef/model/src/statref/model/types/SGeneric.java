package statref.model.types;

public class SGeneric extends SType {
    private final String name;

    public SGeneric(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
