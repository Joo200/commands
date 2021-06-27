package co.aikar.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ACFPaperAdventureManager extends ACFAdventureManager<ChatColor> {
    private MiniMessage miniMessage;

    public ACFPaperAdventureManager(Plugin plugin, CommandManager<?, ?, ? extends ChatColor, ?, ?, ?> manager) {
        super(manager);

        this.miniMessage = MiniMessage.get();
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public void setMiniMessage(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public void sendMessage(CommandIssuer issuer, MessageFormatter<ChatColor> formatter, String message) {
        if (formatter != null) {
            message = "<color:" + formatter.getDefaultColor().name().toLowerCase() + ">" + message;
            for (int i = 1; i <= formatter.getColors().size(); ++i) {
                String colorname = formatter.getColor(i).name().toLowerCase();
                message = message.replace("<c" + i + ">", "<color:" + colorname + ">");
                message = message.replace("</c" + i + ">", "</color:" + colorname + ">");
            }
        }
        CommandSender sender = issuer.getIssuer();
        sender.sendMessage(miniMessage.parse(message));
    }
}

