package net.korithekoder.piggyg.command.sys;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileWriter;

import static java.lang.System.out;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofSysSetting;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;

/**
 * Discord command used for setting a strike with the kick type
 */
public class SetKickStrike extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("setkickstrike")) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

        File strikeFile = new File(ofSysSetting(event.getGuild().getIdLong(), "setstrikes\\" + event.getOption("strike").getAsInt() + ".json"));

        try {
            FileWriter writer = new FileWriter(strikeFile);
            writer.write(
                """
                {
                "striketype": "k"
                }"""
            );
            writer.close();
            event.reply("'Ight dawg, the strike has been put to it's standards").queue();
        } catch (Exception e) {
            out.println(e);
        }
    }
}
