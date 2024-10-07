package net.korithekoder.piggyg.command.user;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileWriter;

import static net.korithekoder.piggyg.resource.ResourceCreator.addFile;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofGeneralSettingWithJson;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;

/**
 * Discord command used for disabling the whitelist system on a guild
 */
public class DisableWhitelist extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("disablewhitelist")) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

        final File WHITELIST_SETTING = new File(ofGeneralSettingWithJson("iswhitelistenabled", event.getGuild().getIdLong()));

        if (!WHITELIST_SETTING.exists()) {
            addFile(
                ofGeneralSettingWithJson("iswhitelistenabled", event.getGuild().getIdLong()),
                "{\n  \"value\": false\n}"
            );
        } else {
            try {
                FileWriter writer = new FileWriter(WHITELIST_SETTING);
                writer.write("{\n  \"value\": false\n}");
                writer.close();
            } catch (Exception ignored) {
            }
        }

        event.reply("'Kay gang, da whitelist iz disabled").queue();
    }
}
