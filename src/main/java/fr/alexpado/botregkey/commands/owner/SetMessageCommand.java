package fr.alexpado.botregkey.commands.owner;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;

public class SetMessageCommand extends Command {

    public SetMessageCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "setmessage";
    }

    @Override
    public String getDescription() {
        return "Set the message that will be sent when using `distributebetakeys`";
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        String message = String.join(" ", event.getArgs().subList(1, event.getArgs().size()));
        if(message.contains("{key}")) {
            try {
                Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());
                configuration.put("message", message);
                configuration.save();
                event.getChannel().sendMessage("Your message has been defined ! :ok_hand:").queue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            event.getChannel().sendMessage("You have to write `{key}` somewhere in your message, that will be replaced by the beta key.").queue();
        }
    }
}
