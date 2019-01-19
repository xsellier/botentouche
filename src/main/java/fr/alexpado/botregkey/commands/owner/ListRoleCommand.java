package fr.alexpado.botregkey.commands.owner;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class ListRoleCommand extends Command {

    public ListRoleCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "listrole";
    }

    @Override
    public String getDescription() {
        return "List the role of your server. Useful for the setbetarole command.";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> aliases = super.getAliases();
        aliases.add("lr");
        return aliases;
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        List<Role> roles = event.getGuild().getRoles();
        StringBuilder builder = new StringBuilder();
        builder.append("```");
        roles.forEach(role -> builder.append(role.getId()).append(" : ").append(role.getName()).append("\n"));
        builder.append("```");

        event.getChannel().sendMessage(builder.toString()).queue();
    }
}
