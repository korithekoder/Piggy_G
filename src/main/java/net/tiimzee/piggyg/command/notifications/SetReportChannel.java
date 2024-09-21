package net.tiimzee.piggyg.command.notifications;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileWriter;

import static java.lang.System.out;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofGeneralSettingWithJson;

public class SetReportChannel extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("setreportchannel")) return;

        final long GUILD_ID = event.getGuild().getIdLong();
        final long CHANNEL_ID = event.getOption("channel").getAsChannel().getIdLong();
        final File REPORT_CHANNEL_FILE = new File(ofGeneralSettingWithJson("reportchannel", GUILD_ID));
        final String DATA = "{\n  \"id\": " + CHANNEL_ID + "\n}";

        try {
            final FileWriter WRITER = new FileWriter(REPORT_CHANNEL_FILE);
            WRITER.write(DATA);
            WRITER.close();
            event.reply("Cool, the report channel been set, dawg").queue();
        } catch (Exception e) {
            out.println(e);
        }
    }
}
