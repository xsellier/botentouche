package fr.alexpado.botregkey.commands.owner;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;

public class CountEnabledCommand extends Command {

    public CountEnabledCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "countenabled";
    }

    @Override
    public String getDescription() {
        return "Enable or disable the count command.";
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());

            if(event.getArgs().size() != 2) {
                event.getChannel().sendMessage("Invalid args count. Syntax : `countenabled [true/false]`").queue();
                return;
            }

            switch (event.getArgs().get(1)) {
                case "false":
                    configuration.put("config.count", false);
                    configuration.save();
                    event.getChannel().sendMessage("You have disabled the use of the `count` command for everyone (even you). However, as a server owner, you can use the command `stats`.").queue();
                    break;
                case "true":
                    configuration.put("config.count", true);
                    configuration.save();
                    event.getChannel().sendMessage("You have enabled the use of the `count` command for everyone. You can use `stats` as server owner.").queue();
                    break;
                default:
                    event.getChannel().sendMessage("Invalid syntax. Syntax : `countenabled [true/false]`").queue();
                    break;
            }

        } catch (Exception e) {
            event.getChannel().sendMessage("Something went wrong. Please contact the developer.").queue();
            e.printStackTrace();
        }
    }
}
