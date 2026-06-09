package cc.cassian.rrv.api.overlay;

public record ButtonData(int x, int y, boolean visible) {
    public static ButtonData DISABLED = new ButtonData(0,0,false);
}
