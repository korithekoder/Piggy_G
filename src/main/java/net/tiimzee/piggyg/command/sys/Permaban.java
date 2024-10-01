package net.tiimzee.piggyg.command.sys;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static net.tiimzee.piggyg.resource.ResourceCreator.addFile;
import static net.tiimzee.piggyg.resource.ResourceCreator.addFolder;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofSysSetting;

/**
 * Discord command used for permabanning a user from the guild
 */
public class Permaban extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("permaban")) return;

        if (!new File(ofSysSetting(event.getGuild().getIdLong(), "permabans")).exists()) {
            addFolder(ofSysSetting(event.getGuild().getIdLong(), "permabans"));
        }

        addFile(
                    ofSysSetting(event.getGuild().getIdLong(), "permabans\\" + event.getOption("user").getAsUser().getIdLong() + ".json"),
                    "{}"
        );

        event.getGuild().ban(event.getOption("user").getAsUser(), 7, TimeUnit.DAYS).reason("Permabanned by a mod (imagine LMAO)").queue();
        event.reply("Kay' gang, the user been permabanned").queue();
    }
}
