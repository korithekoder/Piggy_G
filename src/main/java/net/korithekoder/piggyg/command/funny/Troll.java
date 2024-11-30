package net.korithekoder.piggyg.command.funny;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.lang.System.out;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;

/**
 * Discord command used for sending goofy ahh DMs to people on the server
 */
public class Troll extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("troll")) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());
        
        if (event.getOption("message").getAsString().length() > 2000) {
            event.reply("Sorry fam, but that message too big...\nlike your ***MO-***").queue();
            return;
        }
        User user = event.getOption("user").getAsUser();
        File logFile = new File(ofServer(event.getGuild().getIdLong(), "logs\\trolls.txt"));

        try {
            if (logFile.createNewFile());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm:ss");
            FileWriter writer = new FileWriter(logFile, true);
            writer.write("[LOG][" + dtf.format(LocalDate.now()) + "..." + dtf2.format(LocalTime.now()) + "][@" + event.getUser().getGlobalName() + " TO @" + event.getOption("user").getAsUser().getGlobalName() + "][MESSAGE]: " + event.getOption("message").getAsString() + "\n");
            try {
                writer.write("[LOG][" + dtf.format(LocalDate.now()) + "..." + dtf2.format(LocalTime.now()) + "][@" + event.getUser().getGlobalName() + " TO @" + event.getOption("user").getAsUser().getGlobalName() + "][ATTACHMENT]: " + event.getOption("attachment").getAsAttachment().getProxyUrl() + "\n");
            } catch (Exception e) {
                writer.write("[LOG][" + dtf.format(LocalDate.now()) + "..." + dtf2.format(LocalTime.now()) + "][@" + event.getUser().getGlobalName() + " TO @" + event.getOption("user").getAsUser().getGlobalName() + "][ATTACHMENT]: *none*\n");
            }
            writer.close();
        } catch (Exception e) {
            out.println(e);
        }

        try {
            OptionMapping message = event.getOption("message");
            OptionMapping anonymous = event.getOption("anonymous");
            user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(message.getAsString())).queue();
            user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(event.getOption("attachment").getAsAttachment().getProxyUrl())).queue();
            try {
                if (!(anonymous.getAsBoolean()) || anonymous.equals(null)) event.reply("'Kay gang, the user has been trolled").queue();
            } catch (Exception e) {
                event.reply("'Kay gang, the user has been trolled").queue();
            }
        } catch (Exception e) {
            event.reply("Sorry fam, but the troll didn't send :sob::skull:").queue();
        }
    }
}
