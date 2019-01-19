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
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class DistributeBetaKeysCommand extends Command {

    public DistributeBetaKeysCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "distributebetakeys";
    }

    @Override
    public String getDescription() {
        return "Distribute registered beta key to your fellow beta testers";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> aliases = new ArrayList<>();
        aliases.add("dbk");
        return aliases;
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

            JSONArray keys = configuration.getJSONArray("data.keys");
            BlockingDeque<String> usableKeys = new LinkedBlockingDeque<>();

            for (int i = 0; i < keys.length(); i++) {
                if(!event.getBot().isKeyInUse(configuration, keys.getString(i))) {
                    usableKeys.offer(keys.getString(i));
                }
            }
            testers = testers.stream().filter(member -> !event.getBot().checkUserHaveKey(configuration, member)).collect(Collectors.toList());

            if(testers.size() > usableKeys.size()) {
                event.getChannel().sendMessage("You can't do that. There is more beta testers than beta keys. Please register some more beta keys and try again.\nNumber of keys missing: " + (testers.size() - usableKeys.size())).queue();
                return;
            }

            for (Member tester : testers) {
                String key = usableKeys.poll();
                configuration.put("data.testers." + tester.getUser().getId() + ".key", key);
                if(configuration.has("message")) {
                    tester.getUser().openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage(configuration.getString("message").replace("{key}", key)).queue();
                    });
                }else{
                    tester.getUser().openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage(event.getGuild().getOwner().getEffectiveName() + " has released the beta of his game ! Here is your key : " + key).queue();
                    });
                }
            }

            configuration.save();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
