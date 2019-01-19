package fr.alexpado.botregkey.commands;

import fr.alexpado.botregkey.factory.CommandManager;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class OwnerCommandManager extends CommandManager {

    public OwnerCommandManager(String prefix) {
        super(prefix);
    }

    @Override
    public ExecuteResponse execute(GuildMessageReceivedEvent event) {
        if(!event.getMember().isOwner() && event.getMessage().getContentRaw().startsWith(this.getPrefix())) {
            event.getChannel().sendMessage("You're not allowed to use this command.").queue();
            return ExecuteResponse.IGNORED;
        }
        return super.execute(event);
    }
}
