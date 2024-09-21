package net.tiimzee.piggyg.resource;

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
import static net.tiimzee.piggyg.resource.ResourceDirectory.*;

public class ResourceObtainer {

    /**
     * Used for getting rid of the file extension so that way,
     * things like censored words can be detected (i.e, instead of `word.json`,
     * it will be `word` instead).
     */
    public static String getNameWithoutExtension(String file) {
        int dotIndex = file.lastIndexOf('.');
        return (dotIndex == -1) ? file : file.substring(0, dotIndex);
    }

    public static long getNameWithoutExtensionAsLong(String file) {
        int dotIndex = file.lastIndexOf('.');
        return (dotIndex == -1) ? Long.parseLong(file) : Long.parseLong(file.substring(0, dotIndex));
    }

    public static String getFileContent(File file) {
        Supplier<String> data = () -> {
            String output = "";
            try {
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    output += sc.nextLine();
                }
            } catch (Exception e) {
                out.println(e);
            }
            return output;
        };
        return data.get();
    }

    public static String returnFullTimeoutType(String type) {
        String toReturn = "";
        switch (type) {
            case "s" -> toReturn = "second(s)";
            case "m" -> toReturn = "minute(s)";
            case "h" -> toReturn = "hour(s)";
            case "d" -> toReturn = "day(s)";
        }
        return toReturn;
    }

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

    public static boolean isUserBanned(User user, final long GUILD_ID) {
        File permabansFolder = new File(ofSysSetting(GUILD_ID, "permabans"));
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
            return false;
        }

        for (long userName : whitelistedUsersAl) {
            if (user.getIdLong() == userName) {
                return true;
            }
        }

        return false;
    }

    public static boolean isWhitelistEnabled(final long GUILD_ID) {
        final Supplier<Boolean> IS_WHITELIST_ENABLED = () -> {
            final File WHITELIST_SETTING = new File(ofGeneralSettingWithJson("iswhitelistenabled", GUILD_ID));
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
     * Get the amount of lines
     * in a file
     */
    public static int getFileLines(File aFile) throws IOException {
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(aFile));
            while ((reader.readLine()) != null);
            return reader.getLineNumber();
        } catch (Exception ex) {
            return -1;
        } finally {
            if(reader != null)
                reader.close();
        }
    }
}
