package net.korithekoder.piggyg.command.user;

import java.io.File;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static net.korithekoder.piggyg.resource.ResourceCreator.addFile;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofSetting;

public class AddNewMemberAutoRole extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!(event.getName().equals("addnewmemberautorole"))) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

        File newRoleFile = new File(ofSetting("newmemberroles\\" + event.getOption("role").getAsRole().getIdLong() + ".json", event.getGuild().getIdLong()));

        if (!newRoleFile.exists()) {
            addFile(
                ofSetting("newmemberroles\\" + event.getOption("role").getAsRole().getIdLong() + ".json", event.getGuild().getIdLong()), 
                "{}"
            );
            event.reply("'Ight gang, the new auto role has been registered for new gang members to get when they join da crib").queue();
        } else {
            event.reply("My brother in Christ, this role has already been added, dumbass :man_facepalming:").queue();
        }
    }
}
