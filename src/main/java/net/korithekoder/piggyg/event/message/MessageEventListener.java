package net.korithekoder.piggyg.event.message;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static net.korithekoder.piggyg.resource.ResourceCreator.addFile;
import static net.korithekoder.piggyg.resource.ResourceCreator.addFolder;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofCensoredWordWithJson;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofGeneralSettingWithJson;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofMember;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofSetStrikeWithJson;
import static net.korithekoder.piggyg.resource.ResourceObtainer.checkIsMemberWhitelisted;
import static net.korithekoder.piggyg.resource.ResourceObtainer.deleteDirectory;
import static net.korithekoder.piggyg.resource.ResourceObtainer.getFileContent;
import static net.korithekoder.piggyg.resource.ResourceObtainer.getNameWithoutExtension;
import static net.korithekoder.piggyg.resource.ResourceObtainer.getUserJoinAttemptsFile;
import static net.korithekoder.piggyg.resource.ResourceObtainer.isUserBanned;
import static net.korithekoder.piggyg.resource.ResourceObtainer.returnFullTimeoutType;

/**
 * Core class for general message listening events
 */
public class MessageEventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

        checkIsMemberWhitelisted(event.getGuild(), event.getAuthor(), getUserJoinAttemptsFile(event.getGuild().getIdLong(), event.getAuthor().getIdLong()));

        if (event.getAuthor().isBot()) return;  // Ignores the message if the author is a bot
        if (event.getMessage().isWebhookMessage()) return;  // Ignores the message if it's a webhook message

        checkForCensoredWords(event.getMessage().getContentRaw(), event.getGuild().getIdLong(), event.getGuild(), event.getAuthor(), event);
    }

    /**
     * Checks if the message inputted contains words that are censored on the guild
     * @param message The message to check for censored words
     * @param guildID The guild's ID to obtain the censored words
     * @param guild The guild (as an object)
     * @param user The user (as an object)
     * @param event The event (as an object)
     */
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

        if (strikeCountFile.exists() && !event.getMember().isOwner() && !isUserBanned(user, guildID, guild)) {
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
                                            "# You have been timed out for " + jsonData.get("time") + " " + returnFullTimeoutType((char) jsonData.get("timetype")) + "\n" +
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
        } else if (isUserBanned(user, guildID, guild)) {
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
