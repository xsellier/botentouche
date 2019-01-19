package fr.alexpado.botregkey.factory;

import fr.alexpado.botregkey.Bot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.ArrayList;

public abstract class Command {

    private Bot bot;

    public Command(Bot bot) {
        this.bot = bot;
    }

    public Bot getBot() {
        return bot;
    }

    public abstract String getLabel();
    public abstract String getDescription();

    public ArrayList<String> getAliases() {
        return new ArrayList<>();
    }

    public abstract void execute(CommandExecutedEvent event);

    private void innerEmbed(Message message, String text, Color color) {
        message.editMessage(
                new EmbedBuilder()
                        .setDescription(text)
                        .setColor(color)
                        .build()
        ).queue();
    }

    private void innerEmbed(Message message, String text, Color color, String... additional) {
        message.editMessage(
                new EmbedBuilder()
                        .setDescription(String.format(text, additional))
                        .setColor(color)
                        .build()
        ).queue();
    }

    protected void sendError(Message message, String text) {
        this.innerEmbed(message, text, Color.RED);
    }

    protected void sendError(Message message, String text, String... additional) {
        this.innerEmbed(message, text, Color.RED, additional);
    }

    protected void sendInfo(Message message, String text) {
        this.innerEmbed(message, text, Color.CYAN);
    }

    protected void sendInfo(Message message, String text, String... additionnal) {
        this.innerEmbed(message, text, Color.CYAN, additionnal);
    }

    protected void sendWarn(Message message, String text) {
        this.innerEmbed(message, text, Color.ORANGE);
    }

    protected void sendWarn(Message message, String text, String... additionnal) {
        this.innerEmbed(message, text, Color.ORANGE, additionnal);
    }

}
