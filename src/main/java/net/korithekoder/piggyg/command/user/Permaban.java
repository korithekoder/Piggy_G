package net.korithekoder.piggyg.command.user;

import java.io.File;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static net.korithekoder.piggyg.resource.ResourceCreator.addFile;
import static net.korithekoder.piggyg.resource.ResourceCreator.addFolder;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofSysSetting;

/**
 * Discord command used for permabanning a user from the guild
 */
public class Permaban extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("permaban")) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

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
