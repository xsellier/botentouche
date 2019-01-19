package fr.alexpado.botregkey.commands.user;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;

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

            if(configuration.has("data.testers." + event.getAuthor().getId() + ".key")) {
                if(configuration.has("message")) {
                    event.getAuthor().openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage(configuration.getString("message").replace("{key}", configuration.getString("data.testers." + event.getAuthor().getId() + ".key"))).queue();
                    });
                }else{
                    event.getAuthor().openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage(event.getGuild().getOwner().getEffectiveName() + " has released the beta of his game ! Here is your key : " + configuration.getString("data.testers." + event.getAuthor().getId() + ".key")).queue();
                    });
                }
            }else{
                event.getChannel().sendMessage("You don't have any key to retrieve.").queue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
