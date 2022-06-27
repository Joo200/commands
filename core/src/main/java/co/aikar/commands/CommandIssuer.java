/*
 * Copyright (c) 2016-2017 Daniel Ennis (Aikar) - MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package co.aikar.commands;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Predicate;

public class CommandIssuer implements ForwardingAudience {
    private final Audience audience;
    private final CommandManager<?,?,?> manager;
    private final boolean isPlayer;
    private final Predicate<String> hasPermission;

    public CommandIssuer(CommandManager<?,?,?> manager, Audience audience, boolean isPlayer, Predicate<String> hasPermission) {
        this.manager = manager;
        this.audience = audience;
        this.isPlayer = isPlayer;
        this.hasPermission = hasPermission;
    }

    /**
     * Is this issue a player, or server/console sender
     * @return
     */
    public boolean isPlayer() {
        return this.isPlayer;
    }

    /**
     * @return the unique id of that issuer
     */
    public UUID getUniqueId() {
        return audience.get(Identity.UUID).orElse(null);
    }

    /**
     * @return the display name of the issuer
     */
    public String getUsername() {
        return audience.get(Identity.NAME).orElse(null);
    }

    public CommandManager<?, ?, ?> getManager() {
        return manager;
    }

    /**
     * Has permission node
     * @param permission
     * @return
     */
    public boolean hasPermission(String permission) {
        return hasPermission.test(permission);
    }

    public void sendError(MessageKeyProvider key, TagResolver... replacements) {
        sendMessage(MessageType.ERROR, key.getMessageKey(), replacements);
    }
    public void sendSyntax(MessageKeyProvider key, TagResolver... replacements) {
        sendMessage(MessageType.SYNTAX, key.getMessageKey(), replacements);
    }
    public void sendInfo(MessageKeyProvider key, TagResolver... replacements) {
        sendMessage(MessageType.INFO, key.getMessageKey(), replacements);
    }
    public void sendError(MessageKey key, TagResolver... replacements) {
        sendMessage(MessageType.ERROR, key, replacements);
    }
    public void sendSyntax(MessageKey key, TagResolver... replacements) {
        sendMessage(MessageType.SYNTAX, key, replacements);
    }
    public void sendInfo(MessageKey key, TagResolver... replacements) {
        sendMessage(MessageType.INFO, key, replacements);
    }
    public void sendMessage(MessageType type, MessageKeyProvider key, TagResolver... replacements) {
        sendMessage(type, key.getMessageKey(), replacements);
    }
    public void sendMessage(MessageType type, MessageKey key, TagResolver... replacements) {
        manager.sendMessage(this, type, key, replacements);
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return Collections.singleton(audience);
    }

    public @NotNull Audience audience() {
        return audience;
    }
}
