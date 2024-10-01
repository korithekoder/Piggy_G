package net.tiimzee.piggyg.command.notifications;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.io.File;
import java.time.Duration;

import static net.tiimzee.piggyg.resource.ResourceDirectory.ofGeneralSettingWithJson;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofSysSetting;
import static net.tiimzee.piggyg.resource.ResourceObtainer.getFileContent;

/**
 * Discord command used for sending a report of another user to a channel set by the admins.
 */
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

            File reportTimeoutFile = new File(ofSysSetting(event.getGuild().getIdLong(), "reporttimeout.json"));

            if (reportTimeoutFile.exists()) {
                JSONObject data = new JSONObject(getFileContent(reportTimeoutFile));
                switch ((String) data.get("timetype")) {
                    case "s" -> event.getGuild().timeoutFor(event.getOption("user").getAsUser(), Duration.ofSeconds((int) data.get("time"))).queue();
                    case "m" -> event.getGuild().timeoutFor(event.getOption("user").getAsUser(), Duration.ofMinutes((int) data.get("time"))).queue();
                    case "h" -> event.getGuild().timeoutFor(event.getOption("user").getAsUser(), Duration.ofHours((int) data.get("time"))).queue();
                    case "d" -> event.getGuild().timeoutFor(event.getOption("user").getAsUser(), Duration.ofDays((int) data.get("time"))).queue();
                }
            }

            event.reply("'Kay gang, the report has been sent").queue();
        } else {
            event.reply("Sorry bruv, but the admins haven't set a reports channel yet :person_facepalming:").queue();
        }
    }
}
