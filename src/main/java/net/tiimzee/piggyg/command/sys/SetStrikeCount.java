package net.tiimzee.piggyg.command.sys;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileWriter;

import static java.lang.System.out;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofGeneralSettingWithJson;

public class SetStrikeCount extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("setstrikecount")) return;
        final long GUILD_ID = event.getGuild().getIdLong();
        final String DIR = ofGeneralSettingWithJson("strikecount", GUILD_ID);
        final File STRIKE_COUNT_FILE = new File(DIR);
        final int STRIKE_COUNT = event.getOption("count").getAsInt();
        final String JSON_DATA = "{\n  \"count\": " + STRIKE_COUNT + "\n}";

        try {
            final FileWriter WRITER = new FileWriter(STRIKE_COUNT_FILE);
            WRITER.write(JSON_DATA);
            WRITER.close();
            event.reply("'Ight fam, the num' been set").queue();
        } catch (Exception e) {
            out.println(e);
        }
    }
}
