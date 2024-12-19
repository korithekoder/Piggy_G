package net.korithekoder.piggyg.resource;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import static java.lang.System.out;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.json.JSONObject;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import static net.korithekoder.piggyg.resource.ResourceCreator.addFile;
import static net.korithekoder.piggyg.resource.ResourceCreator.addFolder;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofGeneralSettingWithJson;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofGuildWhitelist;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofMember;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofMemory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofSysSetting;

/**
 * Core class for obtaining data from files and folders
 */
public class ResourceObtainer {

    /**
     * Used for removing the file extention from a file's name.
     * @param file The directory of the file to obtain
     * @return String
     */
    public static String getNameWithoutExtension(String file) {
        int dotIndex = file.lastIndexOf('.');
        return (dotIndex == -1) ? file : file.substring(0, dotIndex);
    }

    /**
     * Used for removing the file extention from a file's name.
     * This is mainly used for when you want to get a file where its
     * name is a member's ID, for example.
     * @param file The directory of the file to obtain
     * @return long
     */
    public static long getNameWithoutExtensionAsLong(String file) {
        int dotIndex = file.lastIndexOf('.');
        return (dotIndex == -1) ? Long.parseLong(file) : Long.parseLong(file.substring(0, dotIndex));
    }

    /**
     * Get the content/data of a file
     * @param file The directory of the file to obtain data from
     * @param makeFancy (OPTIONAL) Returns the fancy version of the files data if true.
     * @return String
     */
    public static String getFileContent(File file) {
        Supplier<String> data = () -> {
            String output = "";
            try {
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    output += sc.nextLine();
                }
                sc.close();
            } catch (Exception e) {
                out.println(e);
            }
            return output;
        };
        return data.get();
    }

    public static String getFileContent(File file, boolean makeFancy) {
        Supplier<String> data = () -> {
            String output = "";
            try {
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    if (makeFancy) {
                        output += sc.nextLine();
                    } else {
                        output += sc.nextLine() + "\n";
                    }
                }
                sc.close();
            } catch (Exception e) {
                out.println(e);
            }
            return output;
        };
        return data.get();
    }

    /**
     * Used for when the user gets timed out.
     * Only used once, but I put it in here anyways because why not :P
     * @param type 
     * @return
     */
    public static String returnFullTimeoutType(char type) {
        String toReturn = "";
        switch (type) {
            case 's' -> toReturn = "second(s)";
            case 'm' -> toReturn = "minute(s)";
            case 'h' -> toReturn = "hour(s)";
            case 'd' -> toReturn = "day(s)";
        }
        return toReturn;
    }

    /**
     * Deletes a folder and it's contents
     * @param path The path to the folder to delete
     * @throws IOException
     */
    public static void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectory(entry);
                }
            }
        }
        Files.delete(path);
    }

    /**
     * Checks if a particullar user on a server is permabanned
     * @param user User to check if permabanned
     * @param guildID ID of the guild to check the member in
     * @return boolean
     */
    public static boolean isUserBanned(User user, long guildID, Guild guild) {

        if (!new File(ofServer(guild.getIdLong())).exists()) addServerDirectory(guild.getIdLong(), guild.getMembers(), guild);

        ArrayList<Long> permabannedUsers = getFilesNamesAsLong(ofSysSetting(guildID, "permabans"));

        if (permabannedUsers == null) {
            return false;
        }

        for (long userName : permabannedUsers) {
            if (user.getIdLong() == userName) {
                return true;
            }
        }

        return false;
    }

    public static void checkIsMemberWhitelisted(Guild guild, User user, File userJoinAttempts) {

        ArrayList<Long> whitelistedUsers = getFilesNamesAsLong(ofGuildWhitelist(guild.getIdLong()));
        boolean isUserWhitelisted = false;

        if (whitelistedUsers == null) {
            isUserWhitelisted = true;
        }

        for (long userId : whitelistedUsers) {
            if (user.getIdLong() == userId) {
                isUserWhitelisted = true;
            }
        }

        if (!isUserBanned(user, guild.getIdLong(), guild)) {
            if (isWhitelistEnabled(guild.getIdLong())) {
                if (isUserWhitelisted) {
                    addFolder(ofMember(user.getIdLong(), guild.getIdLong()));
                    addFolder(ofMember(user.getIdLong(), guild.getIdLong(), "strikes"));

                    if (!new File(ofMember(user.getIdLong(), guild.getIdLong(), "strikes\\count.json")).exists()) {
                        addFile(
                            ofMember(user.getIdLong(), guild.getIdLong(), "strikes\\count.json"),
                            "{\n  \"count\": 0\n}"
                        );
                    }
                } else {
                    guild.kick(user).queue();

                    JSONObject data = new JSONObject(getFileContent(userJoinAttempts));
                    int count = data.getInt("count");
                    count++;

                    switch (count) {
                        case 1 ->
                        user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("""
                        # Hey man, I can't let you through, dawg
                        You ain't whitelisted
                        """)).queue();
                        case 2 ->
                        user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("""
                        # Uhh, have I seen you before?
                        Hippity hop your way on out of here.
                        You still ain't whitelisted dawg :man_facepalming:
                        """)).queue();
                        case 3 ->
                        user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("""
                        # Man, I get it, you wanna join the server
                        ...but you ain't whitelisted.
                        Get yo ass up on out of here, before you
                        become a nationally known opp. :boom::gun:
                        Thank you.
                        """)).queue();
                        case 4 ->
                        user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("""
                        # ***Stop trying***
                        I ain't gonn' let you through until you get whitelisted.
                        If you really wanna join the server, then ask a mod,
                        an admin. ***Don't come to me***.
                        ...you fucking ***dumbass***. :man_facepalming:
                        """)).queue();
                        case 5 -> {
                            user.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("""
                            # ***This is yo fifth time***
                            Fuck off pigga :rage::middle_finger:
                            (you know why)
                            """)).queue();

                            if (!new File(ofSysSetting(guild.getIdLong(), "permabans")).exists()) {
                                addFolder(ofSysSetting(guild.getIdLong(), "permabans"));
                            }

                            addFile(
                                ofSysSetting(guild.getIdLong(), "permabans\\" + user.getIdLong() + ".json"),
                                "{}"
                            );

                            guild.ban(user, 7, TimeUnit.DAYS).reason("Permabanned, cuz").queue();
                        }
                    }

                    try {
                        FileWriter writer = new FileWriter(userJoinAttempts);
                        writer.write("{\n  \"count\": " + count + "\n}");
                        writer.close();
                    } catch (Exception ignored) {
                    }

                    while (new File(ofMember(guild.getIdLong(), guild.getIdLong())).exists()) {
                        try {
                            deleteDirectory(Path.of(ofMember(guild.getIdLong(), guild.getIdLong())));
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        } else {
            guild.ban(user, 7, TimeUnit.DAYS).reason("Permabanned, cuz").queue();

            while (new File(ofMember(user.getIdLong(), guild.getIdLong())).exists()) {
                try {
                    deleteDirectory(Path.of(ofMember(user.getIdLong(), guild.getIdLong())));
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Checks if the whitelist system is enabled on a guild
     * @param guildID ID of the guild
     * @return boolean
     */
    public static boolean isWhitelistEnabled(long guildID) {
        final Supplier<Boolean> IS_WHITELIST_ENABLED = () -> {
            final File WHITELIST_SETTING = new File(ofGeneralSettingWithJson("iswhitelistenabled", guildID));
            if (WHITELIST_SETTING.exists()) {
                final JSONObject data = new JSONObject(getFileContent(WHITELIST_SETTING));
                return (boolean) data.get("value");
            } else {
                return false;
            }
        };
        return IS_WHITELIST_ENABLED.get();
    }

    /**
     * Get the amount of lines in a file
     * @param file File to get the # of lines from
     * @return int
     * @throws IOException
     */
    public static int getFileLines(File file) throws IOException {
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(file));
            while ((reader.readLine()) != null);
            return reader.getLineNumber();
        } catch (Exception ex) {
            return -1;
        } finally {
            if (reader != null) reader.close();
        }
    }

    public static ArrayList<File> getFilesFromFolder(String directory) {
        if (!(new File(directory).listFiles() == null)) {
            return new ArrayList<>(List.of(new File(directory).listFiles()));
        } else {
            return new ArrayList<>();
        }
    }

    public static ArrayList<Long> getFilesNamesAsLong(String directory) {
        ArrayList<File> files = getFilesFromFolder(directory);
        ArrayList<Long> toReturn = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    toReturn.add(getNameWithoutExtensionAsLong(file.getName()));
                }
            }
            return toReturn;
        } else {
            return null;
        }
    }

    public static File getUserJoinAttemptsFile(long guildId, long userId) {
        File userJoinAttempts = new File(ofMemory(guildId, "joinattempts\\" + userId + ".json"));
        if (!userJoinAttempts.exists()) {
            addFile(
                ofMemory(guildId, "joinattempts\\" + userId + ".json"),
                """
                {
                  "count": 0
                }
                """
            );
        }
        return userJoinAttempts;
    }
}
