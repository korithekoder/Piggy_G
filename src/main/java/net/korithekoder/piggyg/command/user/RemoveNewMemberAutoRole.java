package net.korithekoder.piggyg.command.user;

import java.io.File;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofSetting;

public class RemoveNewMemberAutoRole extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!(event.getName().equals("removenewmemberautorole"))) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

        File roleFile = new File(ofSetting("newmemberroles\\" + event.getOption("role").getAsRole().getIdLong() + ".json", event.getGuild().getIdLong()));

        if (roleFile.exists()) {
            if (roleFile.delete()) {
                event.reply("'Ight gang, the new auto role has been obliterated").queue();
            } else {
                event.reply("Sorry fam, but the auto role couldn't be removed :sob:").queue();
            }
        } else {
            event.reply("My brother in Christ, this role doesn't exist :man_facepaling:").queue();
        }
    }
}
