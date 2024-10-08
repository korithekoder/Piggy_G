package net.korithekoder.piggyg.command.user;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;

import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofGuildWhitelistMember;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;
import static net.korithekoder.piggyg.resource.ResourceObtainer.isWhitelistEnabled;

/**
 * Discord command for blacklisting a member from the server
 */
public class BlacklistMember extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("blacklist")) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

        if (isWhitelistEnabled(event.getGuild().getIdLong())) {
            File memberFile = new File(ofGuildWhitelistMember(event.getOption("user").getAsUser().getIdLong(), event.getGuild().getIdLong()));

            while (memberFile.delete());

            event.getGuild().kick(event.getOption("user").getAsUser()).queue();
            event.reply("Aight' gang, the user has been blacklisted").queue();
        } else {
            event.reply("# Pigga...\nYou can't blacklist someone if the whitelist isn't even on, man :man_facepalming:").queue();
        }
    }
}
