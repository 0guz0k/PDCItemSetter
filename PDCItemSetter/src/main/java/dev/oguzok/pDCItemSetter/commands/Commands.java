package dev.oguzok.pDCItemSetter.commands;

import dev.oguzok.pDCItemSetter.PDCItemSetter;
import dev.oguzok.pDCItemSetter.utils.MessageUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String str, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("pdcitemsetter.use")) {
            MessageUtils.sendMessage(sender, PDCItemSetter.getInstance().getConfig().getString("messages.no_permission"));
            return true;
        }
        if (!(sender instanceof Player player)) {
            MessageUtils.sendMessage(sender, PDCItemSetter.getInstance().getConfig().getString("messages.only_player_can_use_command"));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.toHexList(PDCItemSetter.getInstance().getConfig().getStringList("messages.usage")).forEach(line -> player.sendMessage(MessageUtils.toHex(line)));
            return true;
        }

        ItemStack i = player.getInventory().getItemInMainHand();
        ItemMeta m = i.getItemMeta();
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                PDCItemSetter.getInstance().reloadConfig();
                MessageUtils.sendMessage(player, PDCItemSetter.getInstance().getConfig().getString("messages.reload"));
                return true;
            }

            case "clear" -> {
                if (i.isEmpty()) {
                    MessageUtils.sendMessage(player, PDCItemSetter.getInstance().getConfig().getString("messages.item_in_hand_empty"));
                    return true;
                }

                PersistentDataContainer pdc = m.getPersistentDataContainer();
                if (pdc.isEmpty()) {
                    MessageUtils.sendMessage(player, PDCItemSetter.getInstance().getConfig().getString("messages.pdc_empty"));
                    return true;
                }

                for (NamespacedKey key : pdc.getKeys()) pdc.remove(key);
                i.setItemMeta(m);
                MessageUtils.sendMessage(player, PDCItemSetter.getInstance().getConfig().getString("messages.pdc_cleared").replace("%item%", m.getItemName()));
                return true;
            }

            case "set" -> {
                if (args.length < 4) {
                    MessageUtils.toHexList(PDCItemSetter.getInstance().getConfig().getStringList("messages.usage"))
                            .forEach(line -> player.sendMessage(MessageUtils.toHex(line)));
                    return true;
                }

                if (i.isEmpty()) {
                    MessageUtils.sendMessage(player, PDCItemSetter.getInstance().getConfig().getString("messages.item_in_hand_empty"));
                    return true;
                }

                String namespace = args[1];
                String valueKey = args[2];
                String value = args[3];

                if (!namespace.matches("[a-z0-9._-]+") || !valueKey.matches("[a-z0-9._-]+")) {
                    MessageUtils.sendMessage(player, PDCItemSetter.getInstance().getConfig().getString("messages.invalid_regex"));
                    return true;
                }

                m.getPersistentDataContainer().set(new NamespacedKey(namespace, valueKey), PersistentDataType.STRING, value);
                i.setItemMeta(m);
                MessageUtils.sendMessage(player, PDCItemSetter.getInstance().getConfig().getString("messages.pdc_added").replace("%item%", m.getItemName()));
                return true;
            }

            default -> {
                MessageUtils.toHexList(PDCItemSetter.getInstance().getConfig().getStringList("messages.usage")).forEach(line -> player.sendMessage(MessageUtils.toHex(line)));
                return true;
            }
        }
    }

    @Override public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String str, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("pdcitemsetter.use")) return List.of();
        if (args.length == 1) return new ArrayList<>(Arrays.asList("clear", "set")).stream().filter(line -> line.startsWith(args[0])).toList();
        return List.of();
    }
}
