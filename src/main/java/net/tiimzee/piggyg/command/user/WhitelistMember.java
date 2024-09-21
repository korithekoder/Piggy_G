package net.tiimzee.piggyg.command.user;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;

import static net.tiimzee.piggyg.resource.ResourceCreator.addFile;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofGuildWhitelistMemberWithJson;
import static net.tiimzee.piggyg.resource.ResourceObtainer.isWhitelistEnabled;

public class WhitelistMember extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("whitelist")) return;

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
