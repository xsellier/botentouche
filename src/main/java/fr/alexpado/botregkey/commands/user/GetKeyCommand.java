package fr.alexpado.botregkey.commands.user;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;

import org.json.JSONArray;

public class GetKeyCommand extends Command {

    public GetKeyCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "getkey";
    }

    @Override
    public String getDescription() {
        return "Get your beta key ( only if you're supposed to have one ;) )";
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());

            tryToFetchAKey(configuration, event);

            if(configuration.has("data.testers." + event.getAuthor().getId() + ".key")) {
                final String message;

                if(configuration.has("message")) {
                    message = configuration.getString("message").replace("{key}", configuration.getString("data.testers." + event.getAuthor().getId() + ".key"));
                } else {
                    message = event.getGuild().getOwner().getEffectiveName() + " has released the beta of his game ! Here is your key : " + configuration.getString("data.testers." + event.getAuthor().getId() + ".key");
                }

                event.getAuthor().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage(message).queue();
                });
            }else{
                event.getChannel().sendMessage("Sorry, there is no beta key available.").queue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tryToFetchAKey(Configuration configuration, CommandExecutedEvent event) {
        if(!configuration.has("data.testers." + event.getAuthor().getId() + ".key") &&
           configuration.has("config.autokey") && configuration.getBoolean("config.autokey")) {
            JSONArray array = configuration.getJSONArray("data.keys");

            for (int i = 0; i < array.length(); i++) {
                if(!getBot().isKeyInUse(configuration, array.getString(i))) {
                    configuration.put("data.testers." + event.getAuthor().getId() + ".key", array.getString(i));
                    break;
                }
            }
        }
    }
}
