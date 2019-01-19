package fr.alexpado.botregkey;

import fr.alexpado.botregkey.commands.OwnerCommandManager;
import fr.alexpado.botregkey.commands.owner.*;
import fr.alexpado.botregkey.commands.user.*;
import fr.alexpado.botregkey.factory.CommandManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) {
        try {
            new Bot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Configuration botConfiguration;

    private HashMap<String, Configuration> serversConfiguration = new HashMap<>();

    private CommandManager userCommands = new CommandManager("-");
    private OwnerCommandManager ownerCommands = new OwnerCommandManager("!");

    public Bot() throws Exception {
        this.botConfiguration = new Configuration("config");

        this.userCommands.registerCommand(new CountBetaTestersCommand(this));
        this.userCommands.registerCommand(new UserHelpCommand(this));
        this.userCommands.registerCommand(new JoinBetaCommand(this));
        this.userCommands.registerCommand(new QuitBetaCommand(this));
        this.userCommands.registerCommand(new GetKeyCommand(this));

        this.ownerCommands.registerCommand(new BetaAutoKeyCommand(this));
        this.ownerCommands.registerCommand(new BetaOpenedCommand(this));
        this.ownerCommands.registerCommand(new CountEnabledCommand(this));
        this.ownerCommands.registerCommand(new DistributeBetaKeysCommand(this));
        this.ownerCommands.registerCommand(new ListRoleCommand(this));
        this.ownerCommands.registerCommand(new OwnerHelpCommand(this));
        this.ownerCommands.registerCommand(new RegisterBetaKeysCommand(this));
        this.ownerCommands.registerCommand(new RegisterUserBetaKeysCommand(this));
        this.ownerCommands.registerCommand(new RetrieveServerConfigCommand(this));
        this.ownerCommands.registerCommand(new SetBetaRoleCommand(this));
        this.ownerCommands.registerCommand(new SetMessageCommand(this));
        this.ownerCommands.registerCommand(new StatsCommand(this));

        this.login(this.botConfiguration.getString("token"));
    }

    private JDA login(String token) throws LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(token);
        builder.addEventListener(this);
        return builder.build();
    }

    public Configuration getBotConfiguration() {
        return botConfiguration;
    }

    public Configuration getServerConfiguration(Guild guild) throws Exception {
        if(this.serversConfiguration.containsKey(guild.getId())) {
            return this.serversConfiguration.get(guild.getId());
        }
        Configuration serverConfiguration = new Configuration(guild.getId());
        this.serversConfiguration.put(guild.getId(), serverConfiguration);
        return serverConfiguration;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(!this.userCommands.execute(event).shouldContinue) return;
        this.ownerCommands.execute(event);
    }

    public CommandManager getOwnerCommands() {
        return ownerCommands;
    }

    public CommandManager getUserCommands() {
        return userCommands;
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        try {
            Configuration configuration = this.getServerConfiguration(event.getGuild());
            configuration.put("data.testers." + event.getUser().getId() + ".name", event.getUser().getName());
            configuration.put("data.testers." + event.getUser().getId() + ".hasRole", true);

            if(!configuration.has("data.testers." + event.getUser().getId() + ".key")) {
                if(configuration.has("config.autokey") && configuration.getBoolean("config.autokey")) {

                    JSONArray array = configuration.getJSONArray("data.keys");

                    for (int i = 0; i < array.length(); i++) {
                        if(!this.isKeyInUse(configuration, array.getString(i))) {
                            configuration.put("data.testers." + event.getUser().getId() + ".key", array.getString(i));
                            break;
                        }
                    }

                    if(configuration.has("message")) {
                        event.getUser().openPrivateChannel().queue(privateChannel -> {
                            privateChannel.sendMessage(configuration.getString("message").replace("{key}", configuration.getString("data.testers." + event.getUser().getId() + ".key"))).queue();
                        });
                    }else{
                        event.getUser().openPrivateChannel().queue(privateChannel -> {
                            privateChannel.sendMessage(event.getGuild().getOwner().getEffectiveName() + " has released the beta of his game ! Here is your key : " + configuration.getString("data.testers." + event.getUser().getId() + ".key")).queue();
                        });
                    }
                }
            }
            configuration.save();
        }catch (Exception e) {
            e.printStackTrace();
        }
        super.onGuildMemberRoleAdd(event);
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        try {
            Configuration configuration = this.getServerConfiguration(event.getGuild());
            configuration.put("data.testers." + event.getUser().getId() + ".hasRole", false);
            configuration.save();
        }catch (Exception e) {
            e.printStackTrace();
        }
        super.onGuildMemberRoleRemove(event);
    }


    public boolean isKeyInUse(Configuration configuration, String key) {
        if(!configuration.has("data.testers")) return false;
        JSONObject testers = configuration.getJSONObject("data.testers");
        for (String uId : testers.keySet()) {
            JSONObject tester = testers.getJSONObject(uId);
            if(tester.has("key") && tester.getString("key").equals(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkUserHaveKey(Configuration configuration, Member member) {
        JSONObject testers = configuration.getJSONObject("data.testers");
        return testers.getJSONObject(member.getUser().getId()).has("key");
    }

}
