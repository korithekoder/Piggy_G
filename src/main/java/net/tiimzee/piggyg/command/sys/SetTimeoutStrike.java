package net.tiimzee.piggyg.command.sys;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileWriter;

import static java.lang.System.out;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofSysSetting;

/**
 * Discord command used for setting a strike with the timeout type
 */
public class SetTimeoutStrike extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("settimeoutstrike")) return;

        File strikeFile = new File(ofSysSetting(event.getGuild().getIdLong(), "setstrikes\\" + event.getOption("strike").getAsInt() + ".json"));

        try {
            if (event.getOption("type").getAsString().equals("s") || event.getOption("type").getAsString().equals("m") || event.getOption("type").getAsString().equals("h") || event.getOption("type").getAsString().equals("d")) {
                FileWriter writer = new FileWriter(strikeFile);
                writer.write(
                        "{\n" +
                                "  \"time\": " + event.getOption("time").getAsInt() + ",\n" +
                                "  \"timetype\": \"" + event.getOption("type").getAsString() + "\",\n" +
                                "  \"striketype\": \"t\"\n" +
                                "}"
                );
                writer.close();
                event.reply("'Ight dawg, the strike has been put to it's standards").queue();
            } else {
                event.reply("""
                        Bozo, you need to select the right strike type :man_facepalming:
                        Types are:
                        * s=Seconds
                        * m=Minutes
                        * h=Hours
                        * d=Days
                        """).queue();
            }
        } catch (Exception e) {
            out.println(e);
        }
    }
}
