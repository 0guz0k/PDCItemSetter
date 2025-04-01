package dev.oguzok.pDCItemSetter.utils;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    public static void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;

        if (sender instanceof Player player) parseMessage(player, message);
        else {
            String textPart = message.split(";")[0];
            sender.sendMessage(toHex(textPart));
        }
    }

    public static void parseMessage(Player player, String s) {
        if (s == null || !s.contains(";")) return;

        String[] parts = s.split(";");
        String message = parts[0];

        if (parts[1].equals("action_bar")) player.sendActionBar(toHex(message));
        else player.sendMessage(toHex(message));
    }

    private static final Pattern hexPattern = Pattern.compile("#[0-9A-Fa-f]{6}");
    private static final LegacyComponentSerializer legacyAmpersand = LegacyComponentSerializer.legacyAmpersand();

    public static TextComponent convertHex(String input) {
        Matcher matcher = hexPattern.matcher(input);

        while (matcher.find()) {
            String hexColor = matcher.group();
            if (hexColor.startsWith("&#")) input = input.replace(hexColor, "&" + hexColor);
        }

        return legacyAmpersand.deserialize(input);
    }

    public static String toHex(String input) {
        return LegacyComponentSerializer.legacySection().serialize(convertHex(input));
    }

    public static List<String> toHexList(List<String> input) {
        return input.stream().map(line -> LegacyComponentSerializer.legacySection().serialize(convertHex(line))).toList();
    }
}
