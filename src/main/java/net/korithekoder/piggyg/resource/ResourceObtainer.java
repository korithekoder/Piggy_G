package net.korithekoder.piggyg.resource;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Supplier;

import static java.lang.System.out;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.*;

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

        File permabansFolder = new File(ofSysSetting(guildID, "permabans"));
        File[] permabannedUsers = permabansFolder.listFiles();
        ArrayList<Long> permabannedUsersAl = new ArrayList<>();

        if (permabannedUsers != null) {
            for (File userFile : permabannedUsers) {
                if (userFile.isFile()) {
                    permabannedUsersAl.add(getNameWithoutExtensionAsLong(userFile.getName()));
                }
            }
        } else {
            return false;
        }

        for (long userName : permabannedUsersAl) {
            if (user.getIdLong() == userName) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a particullar user on a server is whitelisted
     * @param user User to check if whitelisted
     * @param guildID ID of the guild to check the member in
     * @return boolean
     */
    public static boolean isUserWhitelisted(User user, final long GUILD_ID) {
        File whitelistFolder = new File(ofGuildWhitelist(GUILD_ID));
        File[] whitelistedUsers = whitelistFolder.listFiles();
        ArrayList<Long> whitelistedUsersAl = new ArrayList<>();

        if (whitelistedUsers != null) {
            for (File userFile : whitelistedUsers) {
                if (userFile.isFile()) {
                    whitelistedUsersAl.add(getNameWithoutExtensionAsLong(userFile.getName()));
                }
            }
        } else {
            return true;
        }

        for (long userName : whitelistedUsersAl) {
            if (user.getIdLong() == userName) {
                return true;
            }
        }

        return false;
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
}
