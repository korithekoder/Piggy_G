package net.tiimzee.piggyg.command.lang;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.util.function.Supplier;

import static java.lang.System.out;
import static net.tiimzee.piggyg.resource.ResourceDirectory.ofCensoredWordWithJson;

public class AddCensoredWord extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) throws JSONException {
        if (!event.getName().equals("addcensoredword")) return;

        try {
            final long GUILD_ID = event.getGuild().getIdLong();
            final Supplier<Integer> addStrikes = () -> {
                if (event.getOption("addstrikes") == null) return 1;
                else return event.getOption("addstrikes").getAsInt();
            };
            final String DIR = ofCensoredWordWithJson(event.getOption("word").getAsString().toLowerCase(), GUILD_ID);
            final File censoredWordFile = new File(DIR);

            if (!censoredWordFile.exists()) {
                try {
                    FileWriter writer = new FileWriter(DIR);
                    writer.write("{\n  \"level\": " + addStrikes.get() + ",\n  \"content\": \"" + event.getOption("word").getAsString().toLowerCase() + "\"\n}");
                    writer.close();
                    event.reply("Word ***\"" + event.getOption("word").getAsString().toLowerCase() + "\"*** has been successfully censored").queue();
                } catch (Exception e) {
                    out.println(e);
                }
            } else {
                event.reply("Sorry gang, but this word is already censored :man_shrugging:").queue();
            }
        } catch (Exception e) {
            event.reply("Sorry, can't censor that word fam").queue();
        }
    }
}
