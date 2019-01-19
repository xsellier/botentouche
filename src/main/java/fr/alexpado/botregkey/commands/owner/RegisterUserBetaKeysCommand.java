package fr.alexpado.botregkey.commands.owner;

import fr.alexpado.botregkey.Bot;
import fr.alexpado.botregkey.Configuration;
import fr.alexpado.botregkey.factory.Command;
import fr.alexpado.botregkey.factory.CommandExecutedEvent;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RegisterUserBetaKeysCommand extends Command {

    public RegisterUserBetaKeysCommand(Bot bot) {
        super(bot);
    }

    @Override
    public String getLabel() {
        return "registeruserbetakeys";
    }

    @Override
    public String getDescription() {
        return "Register beta keys already distributed (sent manually)";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> aliases = new ArrayList<>();
        aliases.add("rubk");
        return aliases;
    }

    @Override
    public void execute(CommandExecutedEvent event) {
        try {
            Configuration configuration = event.getBot().getServerConfiguration(event.getGuild());

            if(event.getMessage().getAttachments().size() != 1) {
                event.getChannel().sendMessage("None or multiple file attached. Please send only one file.").queue();
                return;
            }

            Message.Attachment attachment = event.getMessage().getAttachments().get(0);
            List<String> keys = new BufferedReader(new InputStreamReader(attachment.getInputStream())).lines().collect(Collectors.toList());

            List<List<String>> notProcessed = new ArrayList<>();

            keys.forEach(key -> {
                List<String> keyInfo = Arrays.asList(key.split(" "));
                List<Member> members = event.getGuild().getMembersByName(String.join(" ", keyInfo.subList(1, keyInfo.size())), false);

                if(members.size() == 0 || members.size() > 1) {
                    notProcessed.add(keyInfo);
                }else{
                    configuration.put("data.testers." + members.get(0).getUser().getId() + ".name", members.get(0).getUser().getName());
                    configuration.put("data.testers." + members.get(0).getUser().getId() + ".key", keyInfo.get(0));
                }
            });

            configuration.save();

            if(notProcessed.size() > 0) {
                Configuration tmp = new Configuration("tmp" + event.getAuthor().getId());
                for (List<String> strings : notProcessed) {
                    tmp.put(strings.get(1), strings.get(0));
                }
                tmp.save();

                MessageBuilder builder = new MessageBuilder("I've registered a maximum of user keys. But I can't register some of them (probably these users aren't on your server or they changed their name). Here is a list : ");
                event.getChannel().sendFile(tmp.getFile(), "not-registered.json", builder.build()).queue(message1 -> {
                    tmp.getFile().delete();
                    event.getMessage().delete().queue();
                });
            }else{
                event.getChannel().sendMessage("Done ! :ok_hand:").queue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
