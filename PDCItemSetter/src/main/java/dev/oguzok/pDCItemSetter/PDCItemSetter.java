package dev.oguzok.pDCItemSetter;

import dev.oguzok.pDCItemSetter.commands.Commands;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PDCItemSetter extends JavaPlugin {

    private static PDCItemSetter instance;

    @Override public void onEnable() {
        instance = this;
        saveDefaultConfig();

        Objects.requireNonNull(getCommand("pdcitemsetter")).setExecutor(new Commands());
    }

    public static PDCItemSetter getInstance() {
        return instance;
    }
}
