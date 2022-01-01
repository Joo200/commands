/*
 * Copyright (c) 2016-2019 Daniel Ennis (Aikar) - MIT License
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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.help.GenericCommandHelpTopic;

import java.util.ArrayList;
import java.util.List;

public class ACFBukkitHelpTopic extends GenericCommandHelpTopic {

    public ACFBukkitHelpTopic(BukkitCommandManager manager, BukkitRootCommand command) {
        super(command);

        List<String> messages = new ArrayList<>();
        BukkitCommandIssuer captureIssuer = new BukkitCommandIssuer(manager, Bukkit.getConsoleSender()) {
            @Override
            public void sendMessage(Component component) {
                messages.add(LegacyComponentSerializer.builder().build().serialize(component));
            }
        };
        manager.generateCommandHelp(captureIssuer, command).showHelp(captureIssuer);
        this.fullText = ACFUtil.join(messages, "\n");
    }
}
