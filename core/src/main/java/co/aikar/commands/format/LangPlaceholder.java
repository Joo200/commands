package co.aikar.commands.format;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.CommandManager;
import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class LangPlaceholder implements TagResolver {
    public static final String LANG = "lang";

    private final CommandManager<?,?,?,?> manager;
    private final CommandIssuer issuer;
    private final Locale locale;

    public LangPlaceholder(CommandManager<?,?,?,?> manager, CommandIssuer issuer) {
        this.manager = manager;
        this.issuer = issuer;
        this.locale = null;
    }

    public LangPlaceholder(CommandManager<?,?,?,?> manager, Locale locale) {
        this.manager = manager;
        this.locale = locale;
        this.issuer = null;
    }

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        if (!has(name)) {
            return null;
        }
        String arg = arguments.popOr("No valid argument found.").value();
        String value;
        if (arg.startsWith("%")) {
            value = manager.getCommandReplacements().getReplacement(arg.substring(1));
        } else if (issuer != null) {
            value = manager.getMessage(issuer, MessageKey.of(arg));
        } else if (locale != null) {
            value = manager.getMessage(locale, MessageKey.of(arg));
        } else {
            value = manager.getMessage(manager.getLocales().getDefaultLocale(), MessageKey.of(arg));
        }
        return Tag.preProcessParsed(value);
    }

    @Override
    public boolean has(@NotNull String name) {
        return LANG.equals(name);
    }

    public static TagResolver lang(CommandIssuer issuer, String tag, MessageKeyProvider provider) {
        return TagResolver.resolver(tag, (argumentQueue, context) ->
                Tag.preProcessParsed(issuer.getManager().getMessage(issuer, provider)));
    }
}