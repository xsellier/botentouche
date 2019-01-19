package fr.alexpado.botregkey.commands.owner;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;

public class SetBetaRoleCommand extends Command {

    public SetBetaRoleCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "setbetarole";
    }

    @Override
    public String getDescription() {
        return "Define the beta role for your server";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> aliases = new ArrayList<>();
        aliases.add("sbr");
        return aliases;
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        if(event.getArgs().size() != 2) {
            event.getChannel().sendMessage("Invalid args count. Syntax : `setbetarole [roleId]`. You can get the role id by using the `listrole` command").queue();
            return;
        }

        String roleId = event.getArgs().get(1);
        Role role = null;
        try {
            role = event.getGuild().getRoleById(roleId);
        } catch (Exception e) {
            event.getChannel().sendMessage("Invalid id. Please be sure that the id provided is correct.").queue();
            return;
        }

        if(role == null) {
            event.getChannel().sendMessage("Can't find this role. Please be sure that the id provided is correct.").queue();
            return;
        }

        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());
            configuration.put("config.role", roleId);
            configuration.save();
            event.getChannel().sendMessage("Role defined ! :ok_hand:").queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("Something went wrong. Please contact the developer.").queue();
            e.printStackTrace();
        }
    }
}
