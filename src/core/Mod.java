package core;

public class Mod extends mindustry.mod.Mod {
    public static String name = "BomberMod".toLowerCase();

    public Mod(){

    }

    @Override
    public void loadContent() {
        ContentLoader.load();
    }
}
