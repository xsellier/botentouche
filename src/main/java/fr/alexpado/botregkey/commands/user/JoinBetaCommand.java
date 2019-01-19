package fr.alexpado.botregkey.commands.user;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;
import net.dv8tion.jda.core.entities.Role;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class JoinBetaCommand extends Command {

    public JoinBetaCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "joinbeta";
    }

    @Override
    public String getDescription() {
        return "Join the beta testers list";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> aliases = new ArrayList<>();
        aliases.add("jb");
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

            if(event.getMember().getRoles().stream().anyMatch(memberRole -> memberRole.equals(role)))  {
                event.getChannel().sendMessage("You're already a beta tester.").queue();
                return;
            }

            event.getGuild().getController().addRolesToMember(event.getMember(), role).queue(aVoid -> {
                event.getChannel().sendMessage("You're now a beta tester ! :) Make sure to enable DM for this server to be able to receive your beta key in the future.").queue();
            });

        } catch (Exception e) {
            event.getChannel().sendMessage("Something went wrong. Please contact the developer.").queue();
            e.printStackTrace();
        }
    }
}
