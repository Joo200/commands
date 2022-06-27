package co.aikar.commands;

import java.util.List;
import java.util.Map;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

public class VelocityCommandExecutionContext extends CommandExecutionContext {

    VelocityCommandExecutionContext(RegisteredCommand cmd, CommandParameter param, CommandIssuer sender, List<String> args, int index, Map<String, Object> passedArgs) {
        super(cmd, param, sender, args, index, passedArgs);
    }

    public CommandSource getSender() {
        if (this.getIssuer().audience() instanceof CommandSource source) return source;
        return null;
    }

    public Player getPlayer() {
        if (this.getIssuer().audience() instanceof Player source) return source;
        return null;
    }
}
