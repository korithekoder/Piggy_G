package net.tiimzee.piggyg.event.message;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static net.tiimzee.piggyg.resource.ResourceCreator.addFile;
import static net.tiimzee.piggyg.resource.ResourceCreator.addFolder;
import static net.tiimzee.piggyg.resource.ResourceDirectory.*;
import static net.tiimzee.piggyg.resource.ResourceObtainer.*;

public class MessageEventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;  // Ignores the message if the author is a bot
        if (event.getMessage().isWebhookMessage()) return;

        if (isWhitelistEnabled(event.getGuild().getIdLong())) {
            if (!isUserWhitelisted(event.getAuthor(), event.getGuild().getIdLong())) {
                event.getGuild().kick(event.getAuthor()).reason("Fuck off, pigga (you ain't whitelisted)").queue();
                return;
            }
        }
        checkForCensoredWords(event.getMessage().getContentRaw(), event.getGuild().getIdLong(), event.getGuild(), event.getAuthor(), event);
    }

    private static void checkForCensoredWords(String message, long guildID, Guild guild, User user, MessageReceivedEvent event) {
        // Gets the folder of censored words

        File folder = new File(ofServer(guildID, "settings\\censoredwords"));
        File[] censoredWords = folder.listFiles();
        ArrayList<String> alCensoredWords = new ArrayList<>();

        // Gets the words and adds them to the ArrayList<String>
        // above (without the file extension)

        if (censoredWords != null) {
            for (File censoredWord : censoredWords) {
                if (censoredWord.isFile()) {
                    alCensoredWords.add(getNameWithoutExtension(censoredWord.getName()));
                }
            }
        }

        File strikeCountFile = new File(ofGeneralSettingWithJson("strikecount", guildID));

        // Creates the detected member a folder if it doesn't exist

        if (!new File(ofMember(user.getIdLong(), guildID)).exists()) {
            addFolder(ofMember(user.getIdLong(), event.getGuild().getIdLong()));
            addFolder(ofMember(user.getIdLong(), event.getGuild().getIdLong(), "strikes"));

            addFile(
                    ofMember(user.getIdLong(), event.getGuild().getIdLong(), "strikes\\count.json"),
                    "{\n  \"count\": 0\n}"
            );
        }

        if (strikeCountFile.exists() && !event.getMember().isOwner() && !isUserBanned(user, guildID)) {
            for (String word : alCensoredWords) {
                if (message.toLowerCase().contains(word)) {
                    try {

                        // Get the word they said

                        JSONObject currentWord = new JSONObject(getFileContent(new File(ofCensoredWordWithJson(word, guildID))));

                        // Get the current strike count the user has, and then change it based on how bad the word
                        // is set to be (with the "level" key)

                        JSONObject currentStrikeCountJson = new JSONObject(getFileContent(new File(ofMember(user.getIdLong(), guildID, "strikes\\count.json"))));
                        int newStrikeCount = (int) currentStrikeCountJson.get("count") + (int) currentWord.get("level");
                        File onStrikeFile = new File(ofSetStrikeWithJson(newStrikeCount, guildID));
                        File setStrikeCount = new File(ofGeneralSettingWithJson("strikecount", guildID));

                        // Update the changes

                        JSONObject setStrikeCountJson = new JSONObject(getFileContent(setStrikeCount));
                        FileWriter writer = new FileWriter(ofMember(user.getIdLong(), guildID, "strikes\\count.json"));
                        writer.write("{\n  \"count\": " + newStrikeCount + "\n}");
                        writer.close();

                        if (!(newStrikeCount >= (int) setStrikeCountJson.get("count"))) {
                            if (onStrikeFile.exists()) {
                                JSONObject jsonData = new JSONObject(getFileContent(onStrikeFile));

                                if (jsonData.get("striketype").equals("t")) {
                                    switch ((String) jsonData.get("timetype")) {
                                        case "s" -> guild.timeoutFor(user, Duration.ofSeconds((int) jsonData.get("time"))).queue();
                                        case "m" -> guild.timeoutFor(user, Duration.ofMinutes((int) jsonData.get("time"))).queue();
                                        case "h" -> guild.timeoutFor(user, Duration.ofHours((int) jsonData.get("time"))).queue();
                                        case "d" -> guild.timeoutFor(user, Duration.ofDays((int) jsonData.get("time"))).queue();
                                    }
                                    event.getMessage().reply(
                                            "'Ight fool, you can't be saying that kind of stuff\n" +
                                                    "# You have been timed out for " + jsonData.get("time") + " " + returnFullTimeoutType((String) jsonData.get("timetype")) + "\n" +
                                                    "# Reason\n" +
                                                    "Said censored word: ***\"" + word + "\"***"
                                    ).queue();
                                } else if (jsonData.get("striketype").equals("k")) {
                                    guild.kick(user).reason("Said unwanted language, fool").queue();
                                } else {
                                    event.getMessage().reply("""
                                            # Hey fam, you're not allowed to say that.
                                            This just a warning, but just know that you might
                                            get timed out, kicked, or even banned.
                                            """
                                    ).queue();
                                }
                            } else {
                                event.getMessage().reply("""
                                    # Hey fam, you're not allowed to say that.
                                    This just a warning, but just know that you might
                                    get timed out, kicked, or even banned.
                                    """
                                ).queue();
                            }
                        } else {
                            guild.ban(user, 7, TimeUnit.DAYS).reason("Too much inappropriate lang', gang").queue();

                            while (new File(ofMember(user.getIdLong(), guildID)).exists()) {
                                try {
                                    deleteDirectory(Path.of(ofMember(user.getIdLong(), guildID)));
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } else if (isUserBanned(user, guildID)) {
            event.getGuild().ban(user, 7, TimeUnit.DAYS).reason("Permabanned, cuz").queue();

            while (new File(ofMember(user.getIdLong(), guildID)).exists()) {
                try {
                    deleteDirectory(Path.of(ofMember(user.getIdLong(), guildID)));
                } catch (Exception ignored) {
                }
            }
        }
    }
}
