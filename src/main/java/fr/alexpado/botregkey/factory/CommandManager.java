package fr.alexpado.botregkey.factory;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Optional;

public class CommandManager {

    public enum ExecuteResponse {
        IGNORED(true), EXECUTED(false), NOTFOUND(false);

        public boolean shouldContinue;
        ExecuteResponse(boolean b) {
            this.shouldContinue = b;
        }
    }

    private String prefix;

    private ArrayList<Command> commands = new ArrayList<>();

    public CommandManager(String prefix) {
        this.prefix = prefix;
    }

    private Command getCommand(String label) {
        Optional<Command> commandOpt = this.commands.stream().filter(command -> command.getLabel().equals(label) || command.getAliases().contains(label)).findFirst();
        return commandOpt.orElse(null);
    }

    public void registerCommand(Command command) {
        if(this.getCommand(command.getLabel()) == null) {
            this.commands.add(command);
        }
    }

    public ExecuteResponse execute(GuildMessageReceivedEvent event) {
        String messageLabel = event.getMessage().getContentRaw().split(" ")[0].replace(this.prefix, "");
        if(event.getMessage().getContentRaw().startsWith(this.prefix)) {
            Command command = this.getCommand(messageLabel);
            if(command != null) {
                CommandExecutedEvent commandExecutedEvent = new CommandExecutedEvent(command.getBot(), event, messageLabel);
                command.execute(commandExecutedEvent);
                return ExecuteResponse.EXECUTED;
            }
            event.getChannel().sendMessage("Command not found").queue();
            return ExecuteResponse.NOTFOUND;
        }
        return ExecuteResponse.IGNORED;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public String getPrefix() {
        return prefix;
    }
}
