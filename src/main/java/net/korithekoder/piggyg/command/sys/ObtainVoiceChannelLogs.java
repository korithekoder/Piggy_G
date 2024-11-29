package net.korithekoder.piggyg.command.sys;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;

/**
 * Discord command used for obtaining all of the logs when users
 * join and leave voice channels.
 * 
 * NOTE: THIS DOES NOT WORK.
 */
public class ObtainVoiceChannelLogs extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("obtainvoicechannellogs")) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

        if (new File(ofServer(event.getGuild().getIdLong(), "logs\\voice_channel_logs.txt")).exists()) {
            event.replyFiles(FileUpload.fromData(new File(ofServer(event.getGuild().getIdLong(), "logs\\voice_channel_logs.txt")))).queue();
        } else {
            event.reply("Hmmm, seems like no one joined voice channels yet...").queue();
        }
    }
}
