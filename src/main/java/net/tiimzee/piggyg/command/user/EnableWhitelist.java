package net.tiimzee.piggyg.command.user;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileWriter;

import static net.tiimzee.piggyg.resource.ResourceCreator.addFile;
import static net.tiimzee.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofGeneralSettingWithJson;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofGuildWhitelistMemberWithJson;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofServer;

/**
 * Discord command used for enabling the whitelist system on a guild
 */
public class EnableWhitelist extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("enablewhitelist")) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

        for (Member member : event.getGuild().getMembers()) {
            addFile(
                ofGuildWhitelistMemberWithJson(member.getIdLong(), event.getGuild().getIdLong()),
                "{}"
            );
        }

        final File WHITELIST_SETTING = new File(ofGeneralSettingWithJson("iswhitelistenabled", event.getGuild().getIdLong()));

        if (!WHITELIST_SETTING.exists()) {
            addFile(
                    ofGeneralSettingWithJson("iswhitelistenabled", event.getGuild().getIdLong()),
                    "{\n  \"value\": true\n}"
            );
        } else {
            try {
                FileWriter writer = new FileWriter(WHITELIST_SETTING);
                writer.write("{\n  \"value\": true\n}");
                writer.close();
            } catch (Exception ignored) {
            }
        }

        event.reply("'Kay gang, da whitelist iz enabled").queue();
    }
}
