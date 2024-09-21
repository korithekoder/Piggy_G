package net.tiimzee.piggyg.command.notifications;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileWriter;

import static java.lang.System.out;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofGeneralSettingWithJson;

public class SetMailChannel extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("setmailchannel")) return;

        final long GUILD_ID = event.getGuild().getIdLong();
        final long CHANNEL_ID = event.getOption("channel").getAsChannel().getIdLong();
        final File MAIL_CHANNEL_FILE = new File(ofGeneralSettingWithJson("mailchannel", GUILD_ID));
        final String DATA = "{\n  \"id\": " + CHANNEL_ID + "\n}";

        try {
            final FileWriter WRITER = new FileWriter(MAIL_CHANNEL_FILE);
            WRITER.write(DATA);
            WRITER.close();
            event.reply("Cool, the mail channel been set, dawg").queue();
        } catch (Exception e) {
            out.println(e);
        }
    }
}
