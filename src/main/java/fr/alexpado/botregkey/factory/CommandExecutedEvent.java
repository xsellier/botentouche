package fr.alexpado.botregkey.factory;

import fr.alexpado.botregkey.Bot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class CommandExecutedEvent {

    private GuildMessageReceivedEvent event;
    private Bot bot;
    private String label;
    private List<String> args;

    CommandExecutedEvent(Bot bot, GuildMessageReceivedEvent event, String label) {
        this.bot = bot;
        this.event = event;
        this.args = Arrays.asList(event.getMessage().getContentRaw().split(" "));
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public TextChannel getChannel() {
        return event.getChannel();
    }

    public User getAuthor() {
        return event.getAuthor();
    }

    public Member getMember() {
        return event.getMember();
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public JDA getJDA() {
        return event.getJDA();
    }

    public User getSelfUser() {
        return event.getJDA().getSelfUser();
    }

    public Member getSelfMember() {
        return event.getGuild().getSelfMember();
    }

    public Bot getBot() {
        return bot;
    }

    public List<String> getArgs() {
        return args;
    }

}
