package fr.alexpado.botregkey.commands.owner;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;

public class BetaAutoKeyCommand extends Command {

    public BetaAutoKeyCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "betaautokey";
    }

    @Override
    public String getDescription() {
        return "Disable/enable the auto give for your beta keys.";
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());

            if(event.getArgs().size() != 2) {
                event.getChannel().sendMessage("Invalid args count. Syntax : `betaautokey [true/false]`").queue();
                return;
            }

            switch (event.getArgs().get(1)) {
                case "false":
                    configuration.put("config.autokey", false);
                    configuration.save();
                    event.getChannel().sendMessage("New beta testers will no longer receive their beta key after joining the beta testers list.").queue();
                    break;
                case "true":
                    configuration.put("config.autokey", true);
                    configuration.save();
                    event.getChannel().sendMessage("New beta testers will receive their beta key after joining the beta testers list.").queue();
                    break;
                default:
                    event.getChannel().sendMessage("Invalid syntax. Syntax : `betaautokey [true/false]`").queue();
                    break;
            }

        } catch (Exception e) {
            event.getChannel().sendMessage("Something went wrong. Please contact the developer.").queue();
            e.printStackTrace();
        }
    }

}
