package net.korithekoder.piggyg.command.user;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;

import static net.korithekoder.piggyg.resource.ResourceCreator.addFile;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofGuildWhitelistMemberWithJson;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;
import static net.korithekoder.piggyg.resource.ResourceObtainer.isWhitelistEnabled;

/**
 * Discord command used for whitelisting a member on a guild.
 * IMPORTANT: This command can only be used properly if Developer Mode is enabled, since
 * you can't copy a user's ID without the option turned on.
 * 
 * You can turn this option on by going to Settings > Advanced > Developer Mode.
 */
public class WhitelistMember extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("whitelist")) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

        final File MEMBER_TO_WHITELIST = new File(ofGuildWhitelistMemberWithJson(Long.parseLong(event.getOption("user_id").getAsString()), event.getGuild().getIdLong()));

        if (isWhitelistEnabled(event.getGuild().getIdLong())) {
            if (!MEMBER_TO_WHITELIST.exists()) {
                addFile(
                        ofGuildWhitelistMemberWithJson(event.getOption("user_id").getAsLong(), event.getGuild().getIdLong()),
                        "{}"
                );
                event.reply("'Aight gang, the user been whitelisted").queue();
            } else {
                event.reply("Gang, that member is already whitelisted :man_facepalming:").queue();
            }
        } else {
            event.reply("Dawg...\nYou can't whitelist someone if the whitelist isn't enabled :man_facepalming:").queue();
        }
    }
}
