package net.tiimzee.piggyg.command.sys;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;

import static net.tiimzee.piggyg.resource.ResourceDirectory.ofSysSetting;

/**
 * Discord command used for un-permabanning a user from the guild
 */
public class Unpermaban extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("unpermaban")) return;

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
