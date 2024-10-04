package net.tiimzee.piggyg.command.notifications;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.lang.System.out;
import static net.tiimzee.piggyg.resource.ResourceCreator.addFile;
import static net.tiimzee.piggyg.resource.ResourceCreator.addFolder;
import static net.tiimzee.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.tiimzee.piggyg.resource.ResourceDirectory.*;
import static net.tiimzee.piggyg.resource.ResourceObtainer.getNameWithoutExtension;
import static net.tiimzee.piggyg.resource.ResourceObtainer.getFileContent;
import static net.tiimzee.piggyg.resource.ResourceObtainer.returnFullTimeoutType;

/**
 * Discord command used for sending "mail" to a channel set by the admins
 * of the server.
 */
public class SendModMail extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("sendmodmail")) return;
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());
        
        checkForCensoredWords(event.getOption("message").getAsString(), event.getGuild().getIdLong(), event.getGuild(), event.getUser(), event);
        final File MAIL_CHANNEL_ID = new File(ofGeneralSettingWithJson("mailchannel", event.getGuild().getIdLong()));

        if (MAIL_CHANNEL_ID.exists()) {
            final Supplier<String> DATA = () -> {
                String output = "";
                try {
                    Scanner sc = new Scanner(MAIL_CHANNEL_ID);
                    while (sc.hasNextLine()) {
                        output += sc.nextLine();
                    }
                    sc.close();
                } catch (Exception e) {
                    out.println(e);
                }
                return output;
            };

            long ID = (Long) new JSONObject(DATA.get()).get("id");

            event.getGuild().getTextChannelById(ID).sendMessage("# User *\"<@" + event.getUser().getIdLong() + ">\"* sent you this message dawg:\n" + event.getOption("message").getAsString()).queue();
            event.reply("'Kay gang, the mail has been sent").queue();
        } else {
            event.reply("Sorry bruv, but the admins haven't set a mail channel yet :person_facepalming:").queue();
        }
    }

    private static void checkForCensoredWords(String message, long guildID, Guild guild, User user, SlashCommandInteractionEvent event) {
        File folder = new File(ofServer(guildID, "settings\\censoredwords"));
        File[] censoredWords = folder.listFiles();
        ArrayList<String> alCensoredWords = new ArrayList<>();

        if (censoredWords != null) {
            for (File censoredWord : censoredWords) {
                if (censoredWord.isFile()) {
                    alCensoredWords.add(getNameWithoutExtension(censoredWord.getName()));
                }
            }
        }

        File strikeCountFile = new File(ofGeneralSettingWithJson("strikecount", guildID));

        if (strikeCountFile.exists() && !event.getMember().isOwner()) {
            for (String word : alCensoredWords) {
                if (message.toLowerCase().contains(word)) {
                    try {

                        if (!new File(ofMember(user.getIdLong(), guildID)).exists()) {
                            addFolder(ofMember(user.getIdLong(), event.getGuild().getIdLong()));
                            addFolder(ofMember(user.getIdLong(), event.getGuild().getIdLong(), "strikes"));

                            addFile(
                                    ofMember(user.getIdLong(), event.getGuild().getIdLong(), "strikes\\count.json"),
                                    "{\n  \"count\": 0\n}"
                            );
                        }

                        // Get the word they said

                        JSONObject currentWord = new JSONObject(getFileContent(new File(ofCensoredWordWithJson(word, guildID))));

                        // Get the current strike count the user has, and then change it based on how bad the word
                        // is set to be (with the "level" key)

                        JSONObject currentStrikeCountJson = new JSONObject(getFileContent(new File(ofMember(user.getIdLong(), guildID, "strikes\\count.json"))));
                        int newStrikeCount = (int) currentStrikeCountJson.get("count") + (int) currentWord.get("level");
                        File onStrikeFile = new File(ofSetStrike(newStrikeCount, guildID));
                        File setStrikeCount = new File(ofGeneralSettingWithJson("strikecount", guildID));

                        // Update the changes

                        JSONObject setStrikeCountJson = new JSONObject(getFileContent(setStrikeCount));
                        FileWriter writer = new FileWriter(ofMember(user.getIdLong(), guildID, "strikes\\count.json"));
                        writer.write("{\n  \"count\": " + newStrikeCount + "\n}");
                        writer.close();

                        if (!(newStrikeCount == (int) setStrikeCountJson.get("count"))) {
                            if (onStrikeFile.exists()) {
                                JSONObject jsonData = new JSONObject(getFileContent(onStrikeFile));

                                if (jsonData.get("striketype").equals("t")) {
                                    switch ((String) jsonData.get("timetype")) {
                                        case "s" -> guild.timeoutFor(user, Duration.ofSeconds((int) jsonData.get("time"))).queue();
                                        case "m" -> guild.timeoutFor(user, Duration.ofMinutes((int) jsonData.get("time"))).queue();
                                        case "h" -> guild.timeoutFor(user, Duration.ofHours((int) jsonData.get("time"))).queue();
                                        case "d" -> guild.timeoutFor(user, Duration.ofDays((int) jsonData.get("time"))).queue();
                                    }
                                    event.reply(
                                            "'Ight fool, you can't be saying that kind of stuff\n" +
                                            "# You have been timed out for " + jsonData.get("time") + " " + returnFullTimeoutType((char) jsonData.get("timetype")) + "\n" +
                                            "# Reason\n" +
                                            "Said censored word: " + word
                                    ).queue();
                                } else {
                                    event.reply("""
                                            # Hey fam, you're not allowed to say that.
                                            This just a warning, but just know that you might
                                            get timed out, kicked, or even banned.
                                            """
                                    ).queue();
                                }
                            } else {
                                event.reply("""
                                    # Hey fam, you're not allowed to say that.
                                    This just a warning, but just know that you might
                                    get timed out, kicked, or even banned.
                                    """
                                ).queue();
                            }
                        } else {
                            guild.ban(user, 7, TimeUnit.DAYS).reason("Said too many censored words").queue();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}
