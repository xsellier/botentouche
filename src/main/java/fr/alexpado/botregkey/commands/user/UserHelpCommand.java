package fr.alexpado.botregkey.commands.user;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;
import fr.alexpado.botregkey.factory.CommandManager;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;

public class UserHelpCommand extends Command {

    public UserHelpCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Get some helpful help :eyes:";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> aliases = new ArrayList<>();
        aliases.add("?");
        aliases.add("h");
        return aliases;
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        CommandManager userCommands = event.getBot().getUserCommands();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.CYAN);
        builder.setTitle("Command list");

        userCommands.getCommands().forEach(command -> {
            builder.addField(command.getLabel(), command.getDescription(), false);
        });

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
