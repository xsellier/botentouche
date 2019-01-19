package fr.alexpado.botregkey.commands.owner;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;

import java.util.ArrayList;

public class RetrieveServerConfigCommand extends Command {

    public RetrieveServerConfigCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "retrieveserverconfig";
    }

    @Override
    public String getDescription() {
        return "Get the config file of your server.";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> aliases = super.getAliases();
        aliases.add("rsc");
        return aliases;
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());
            event.getChannel().sendFile(configuration.getFile(), "serverconfig.json").queue();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
