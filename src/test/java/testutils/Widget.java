package testutils;

public class Widget {

    private final boolean purple;
    private final boolean shiny;

    public Widget(final boolean purple, final boolean shiny) {
        this.purple = purple;
        this.shiny = shiny;
    }

    public boolean isPurple() {
        return purple;
    }

    public boolean isShiny() {
        return shiny;
    }
}

