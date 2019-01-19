package fr.alexpado.botregkey.commands.user;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;
import net.dv8tion.jda.core.entities.Role;

public class CountBetaTestersCommand extends Command {

    public CountBetaTestersCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "count";
    }

    @Override
    public String getDescription() {
        return "Get the number of beta testers on this server";
    }

    @Override
    public boolean isEnabled(CommandExecutedEvent event) {
        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());

            return configuration.has("config.count") && configuration.getBoolean("config.count");
        } catch (Exception e) {
            event.getChannel().sendMessage("Something went wrong. Please contact the developer.").queue();
            e.printStackTrace();

            // Enable it by default
            return true;
        }
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());

            if(not isEnabled(event)) {
                event.getChannel().sendMessage("The count command is not allowed on this server.").queue();
                return;
            }

            if(!configuration.has("config.role")) {
                event.getChannel().sendMessage("I'm not configured yet.").queue();
                return;
            }

            String roleId = configuration.getString("config.role");

            Role role = event.getGuild().getRoleById(roleId);
            if(role == null) {
                event.getChannel().sendMessage("Something went wrong when retreiving the beta role. The server owner may need to reconfigure the bot.").queue();
                return;
            }

            int count = event.getGuild().getMembersWithRoles(role).size();
            event.getChannel().sendMessage("There is " + count + " beta testers here.").queue();

        } catch (Exception e) {
            event.getChannel().sendMessage("Something went wrong. Please contact the developer.").queue();
            e.printStackTrace();
        }
    }
}
