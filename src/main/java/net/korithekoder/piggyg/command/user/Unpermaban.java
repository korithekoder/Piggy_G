package net.korithekoder.piggyg.command.user;

import java.io.File;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofSysSetting;

/**
 * Discord command used for un-permabanning a user from the guild
 */
public class Unpermaban extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("unpermaban")) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

        File permabannedUserFile = new File(ofSysSetting(event.getGuild().getIdLong(), "permabans\\" + event.getOption("user").getAsUser().getIdLong() + ".json"));

        if (permabannedUserFile.exists()) {
            try {
                if (permabannedUserFile.delete()) {
                    event.getGuild().unban(event.getOption("user").getAsUser()).queue();
                }
                event.reply("'Ight fam, the user can join yo server now").queue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            event.reply("That user ain't permabanned bruv :man_shrugging:").queue();
        }
    }
}
