package fr.alexpado.botregkey.commands.owner;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;

public class BetaOpenedCommand extends Command {

    public BetaOpenedCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "betaopened";
    }

    @Override
    public String getDescription() {
        return "Disable or enable the joinbeta & quitbeta commands";
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());

            if(event.getArgs().size() != 2) {
                event.getChannel().sendMessage("Invalid args count. Syntax : `betaopened [true/false]`").queue();
                return;
            }

            switch (event.getArgs().get(1)) {
                case "false":
                    configuration.put("config.enabled", false);
                    configuration.save();
                    event.getChannel().sendMessage("The beta tester list is now frozen. Nobody can join and leave the beta testers list.").queue();
                    break;
                case "true":
                    configuration.put("config.enabled", true);
                    configuration.save();
                    event.getChannel().sendMessage("The beta tester list is now unfrozen. Everyone can join and leave the beta testers list.").queue();
                    break;
                default:
                    event.getChannel().sendMessage("Invalid syntax. Syntax : `betaopened [true/false]`").queue();
                    break;
            }

        } catch (Exception e) {
            event.getChannel().sendMessage("Something went wrong. Please contact the developer.").queue();
            e.printStackTrace();
        }
    }
}
