package fr.alexpado.botregkey.commands.user;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;
import net.dv8tion.jda.core.entities.Role;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

public class QuitBetaCommand extends Command {

    public QuitBetaCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "quitbeta";
    }

    @Override
    public String getDescription() {
        return "Leave the beta testers list";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> aliases = new ArrayList<>();
        aliases.add("lb");
        aliases.add("qb");
        return aliases;
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());

            if(!configuration.has("config.role")) {
                event.getChannel().sendMessage("This bot is not configured yet.").queue();
                return;
            }

            Role role = event.getGuild().getRoleById(configuration.getString("config.role"));
            if(role == null) {
                event.getChannel().sendMessage("Something went wrong when retreiving the beta role. The server owner may need to reconfigure the bot.").queue();
                return;
            }

            if(!configuration.has("config.enabled") || !configuration.getBoolean("config.enabled")) {
                event.getChannel().sendMessage("The beta is closed for now. Check again later.").queue();
                return;
            }

            if(event.getMember().getRoles().stream().noneMatch(memberRole -> memberRole.equals(role)))  {
                event.getChannel().sendMessage("You're not a beta tester.").queue();
                return;
            }

            if(configuration.has("data.testers." + event.getAuthor().getId() + ".key")) {
                event.getChannel().sendMessage("You can't exit the beta testers list anymore.").queue();
                return;
            }

            event.getGuild().getController().removeRolesFromMember(event.getMember(), role).queue(aVoid -> {
                event.getChannel().sendMessage("You're not a beta tester anymore.").queue();
            });

        } catch (Exception e) {
            event.getChannel().sendMessage("Something went wrong. Please contact the developer.").queue();
            e.printStackTrace();
        }
    }
}