package net.tiimzee.piggyg.command.lang;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;

import static net.tiimzee.piggyg.resource.ResourceDirectory.ofCensoredWordWithJson;

/**
 * Discord command for removing a censored word from a guild
 */
public class RemoveCensoredWord extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("removecensoredword")) return;

        final long GUILD_ID = event.getGuild().getIdLong();
        final String DIR = ofCensoredWordWithJson(event.getOption("word").getAsString().toLowerCase(), GUILD_ID);
        final File censoredWordFile = new File(DIR);

        if (censoredWordFile.exists()) {
            if (censoredWordFile.delete()) {
                event.reply("'Ight gang, the word ***\"" + event.getOption("word").getAsString().toLowerCase() + "\"*** was successfully squashed to a pulp").queue();
            }
        } else {
            event.reply("Sorry fool, but the word ***\"" + event.getOption("word").getAsString().toLowerCase() + "\"*** wasn't found").queue();
        }
    }
}
