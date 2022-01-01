package co.aikar.commands.format;

import co.aikar.commands.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.parser.ParsingException;
import net.kyori.adventure.text.minimessage.parser.node.TagPart;
import net.kyori.adventure.text.minimessage.transformation.Transformation;
import net.kyori.adventure.text.minimessage.transformation.TransformationRegistry;
import net.kyori.adventure.text.minimessage.transformation.TransformationType;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class MessageFormatter extends Transformation {
    public static Map<String, TextColor> INFO_REPLACEMENTS = new HashMap<>();
    public static Map<String, TextColor> ERROR_REPLACEMENTS = new HashMap<>();
    public static Map<String, TextColor> HELP_REPLACEMENTS = new HashMap<>();
    public static Map<String, TextColor> SYNTAX_REPLACEMENTS = new HashMap<>();

    public static Map<String, TextColor> DEFAULT = new HashMap<>();

    static {
        INFO_REPLACEMENTS.put("c1", NamedTextColor.BLUE);
        INFO_REPLACEMENTS.put("c2", NamedTextColor.DARK_GREEN);
        INFO_REPLACEMENTS.put("c3", NamedTextColor.GREEN);
        DEFAULT = INFO_REPLACEMENTS;

        ERROR_REPLACEMENTS.put("c1", NamedTextColor.RED);
        ERROR_REPLACEMENTS.put("c2", NamedTextColor.YELLOW);
        ERROR_REPLACEMENTS.put("c3", NamedTextColor.RED);

        HELP_REPLACEMENTS.put("c1", NamedTextColor.AQUA);
        HELP_REPLACEMENTS.put("c2", NamedTextColor.GREEN);
        HELP_REPLACEMENTS.put("c3", NamedTextColor.YELLOW);

        SYNTAX_REPLACEMENTS.put("c1", NamedTextColor.YELLOW);
        SYNTAX_REPLACEMENTS.put("c2", NamedTextColor.GREEN);
        SYNTAX_REPLACEMENTS.put("c3", NamedTextColor.WHITE);
    }

    public static TransformationRegistry getRegistryOf(Map<String, TextColor> map) {
        return TransformationRegistry.builder().add(TransformationType.transformationType(
                name -> MessageFormatter.canParse(name, map),
                (name, args) -> MessageFormatter.create(map, name, args))).build();
    }

    /**
     * Get if this transformation can handle the provided tag name.
     *
     * @param name tag name to test
     * @return if this transformation is applicable
     * @since 4.10.0
     */
    public static boolean canParse(final String name, Map<String, TextColor> colors) {
        return colors.containsKey(name);
    }


    /**
     * Create a new color name.
     *
     * @param name the tag name
     * @param args the tag arguments
     * @return a new transformation
     * @since 4.10.0
     */
    public static MessageFormatter create(final Map<String, TextColor> colors, final String name, final List<TagPart> args) {
        final TextColor color = colors.get(name);

        if (color == null) {
            throw new ParsingException("Don't know how to turn '" + name + "' into a color", args);
        }

        return new MessageFormatter(color);
    }

    private TextColor color;

    private MessageFormatter(final TextColor color) {
        this.color = color;
    }

    @Override
    public Component apply() {
        return Component.empty().color(this.color);
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("color", this.color));
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || this.getClass() != other.getClass()) return false;
        final MessageFormatter that = (MessageFormatter) other;
        return Objects.equals(this.color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.color);
    }
}
