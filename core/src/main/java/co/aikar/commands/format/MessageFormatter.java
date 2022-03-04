package co.aikar.commands.format;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MessageFormatter implements TagResolver {
    public static MessageFormatter INFO_REPLACEMENTS = new MessageFormatter(Map.of(
            "c1", NamedTextColor.BLUE,
            "c2", NamedTextColor.DARK_GREEN,
            "c3", NamedTextColor.GREEN
    ));
    public static MessageFormatter DEFAULT = INFO_REPLACEMENTS;
    public static MessageFormatter ERROR_REPLACEMENTS = new MessageFormatter(Map.of(
            "c1", NamedTextColor.RED,
            "c2", NamedTextColor.YELLOW,
            "c3", NamedTextColor.RED
    ));
    public static MessageFormatter HELP_REPLACEMENTS  = new MessageFormatter(Map.of(
            "c1", NamedTextColor.AQUA,
            "c2", NamedTextColor.GREEN,
            "c3", NamedTextColor.YELLOW
    ));
    public static MessageFormatter SYNTAX_REPLACEMENTS = new MessageFormatter(Map.of(
            "c1", NamedTextColor.YELLOW,
            "c2", NamedTextColor.GREEN,
            "c3", NamedTextColor.WHITE
    ));

    private Map<String, TextColor> colors;

    private MessageFormatter(final Map<String, TextColor> colors) {
        this.colors = colors;
    }

    public void setColors(Map<String, TextColor> colors) {
        this.colors = colors;
    }

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        if (!this.has(name)) return null;

        TextColor color = colors.get(name);
        return Tag.styling(color);
    }

    @Override
    public boolean has(@NotNull String name) {
        return colors.containsKey(name);
    }
}
