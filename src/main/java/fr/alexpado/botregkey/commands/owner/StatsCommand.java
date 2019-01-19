package fr.alexpado.botregkey.commands.owner;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatsCommand extends Command {

    public StatsCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "stats";
    }

    @Override
    public String getDescription() {
        return "Get Some useful stats";
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());

            if(!configuration.has("config.role")) {
                event.getChannel().sendMessage("Please configure the beta role first.").queue();
                return;
            }

            Role role = event.getGuild().getRoleById(configuration.getString("config.role"));
            if(role == null) {
                event.getChannel().sendMessage("Please configure the beta role first. The one defined is no longer valid. (role not found)").queue();
                return;
            }

            List<Member> testers = event.getGuild().getMembersWithRoles(role);


            JSONArray keys = configuration.has("data.keys") ? configuration.getJSONArray("data.keys") : new JSONArray();
            List<String> usableKeys = new ArrayList<>();

            for (int i = 0; i < keys.length(); i++) {
                if(!event.getBot().isKeyInUse(configuration, keys.getString(i))) {
                    usableKeys.add(keys.getString(i));
                }
            }

            StringBuilder builder = new StringBuilder();
            builder.append("Here is some cool stats : \n```");
            builder.append("Number of beta testers         ").append(testers.size()).append("\n");
            builder.append("Total number of keys           ").append(keys.length()).append("\n");
            builder.append("Number of keys available       ").append(usableKeys.size()).append("\n");
            builder.append("```\n").append("Thanks for using me ! :D");

            event.getChannel().sendMessage(builder.toString()).queue();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
