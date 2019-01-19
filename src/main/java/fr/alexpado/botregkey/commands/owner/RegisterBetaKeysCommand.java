package fr.alexpado.botregkey.commands.owner;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;
import net.dv8tion.jda.core.entities.Message;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegisterBetaKeysCommand extends Command {

    public RegisterBetaKeysCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "registerbetakeys";
    }

    @Override
    public String getDescription() {
        return "Register all your beta keys available";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> aliases = super.getAliases();
        aliases.add("rbk");
        return aliases;
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());

            if(event.getMessage().getAttachments().size() != 1) {
                event.getChannel().sendMessage("None or multiple file attached. Please send only one file.").queue();
                return;
            }

            Message.Attachment attachment = event.getMessage().getAttachments().get(0);

            List<String> keys = new BufferedReader(new InputStreamReader(attachment.getInputStream())).lines().collect(Collectors.toList());
            JSONArray array = new JSONArray();
            if(event.getArgs().indexOf("--replace") == -1) {
                if(configuration.has("data.keys")) {
                    array = configuration.getJSONArray("data.keys");
                }
            }

            List<String> existingKeys = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                existingKeys.add(array.getString(i));
            }

            int process = 0;
            int added = 0;
            int inUse = 0;

            for (String key : keys) {
                process++;
                if(event.getBot().isKeyInUse(configuration, key)) {
                    inUse++;
                }
                if(!existingKeys.contains(key)) {
                    added++;
                    array.put(key);
                    existingKeys.add(key);
                }

            }

            configuration.put("data.keys", array);
            configuration.save();
            event.getChannel().sendMessage(String.format("Your keys has been registered.\nProcessed : %s\nAdded : %s\nAlready In Use : %s", process+"", added+"", inUse+"")).queue();
            event.getMessage().delete().queue();
        }catch (Exception e) {
            event.getChannel().sendMessage("Something went wrong. Please contact the developer.").queue();
            e.printStackTrace();
        }
    }
}
