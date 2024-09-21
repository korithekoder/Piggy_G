package net.tiimzee.piggyg.command.sys;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

import static net.tiimzee.piggyg.resource.ResourceDirectory.ofServer;

public class ObtainDisplayNameLogs extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("obtaindisplaynamelogs")) return;

        if (new File(ofServer(event.getGuild().getIdLong(), "logs\\display_name_changes.txt")).exists()) {
            event.replyFiles(FileUpload.fromData(new File(ofServer(event.getGuild().getIdLong(), "logs\\display_name_changes.txt")))).queue();
        } else {
            event.reply("Hmmm, seems like no changed their name when I was around yet...").queue();
        }
    }
}
