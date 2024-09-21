package net.tiimzee.piggyg.command.notifications;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.io.File;
import java.util.Scanner;
import java.util.function.Supplier;

import static java.lang.System.out;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofGeneralSettingWithJson;
import static net.tiimzee.piggyg.resource.ResourceObtainer.getFileContent;

public class SendReport extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("report")) return;
        final File REPORT_CHANNEL_FILE = new File(ofGeneralSettingWithJson("reportchannel", event.getGuild().getIdLong()));

        if (REPORT_CHANNEL_FILE.exists()) {
            final String DATA = getFileContent(REPORT_CHANNEL_FILE);

            long ID = (long) new JSONObject(DATA).get("id");

            event.getGuild().getTextChannelById(ID).sendMessage(
                    "# User <@" + event.getUser().getIdLong() + "> made a report on blud <@" + event.getOption("user").getAsUser().getIdLong() + ">:\n" +
                    "# ***Reason:*** \"" + event.getOption("reason").getAsString() + "\"\n" +
                    "# Details\n" +
                    event.getOption("description").getAsString()
            ).queue();
            event.reply("'Kay gang, the report has been sent").queue();
        } else {
            event.reply("Sorry bruv, but the admins haven't set a reports channel yet :person_facepalming:").queue();
        }
    }
}
