package co.aikar.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

public class VelocityConditionContext extends ConditionContext {
    VelocityConditionContext(CommandIssuer issuer, String config) {
        super(issuer, config);
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
