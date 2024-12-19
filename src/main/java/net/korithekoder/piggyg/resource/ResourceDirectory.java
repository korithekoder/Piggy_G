package net.korithekoder.piggyg.resource;

/**
 * Core class used for shortening directories, rather than just hardcoding them
 * over and over again. Mainly it just takes up less time and also makes it easier to read
 */
public class ResourceDirectory {

    // Used for when you don't need a specific part of a directory (in the "resources" folder)

    public static String ofResource(String trailingDir) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\" + trailingDir;
    }

    public static String ofServer(long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID;
    }

    public static String ofServer(long guildID, String trailingDir) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\" + trailingDir;
    }

    public static String ofMember(long memberID, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\memory\\members\\" + memberID;
    }

    public static String ofMember(long memberID, long guildID, String trailingDir) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\memory\\members\\" + memberID + "\\" + trailingDir;
    }

    public static String ofPermabannedMember(long memberID, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\sys\\permabans\\" + memberID;
    }

    public static String ofPermabannedMemberWithJson(long memberID, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\sys\\permabans\\" + memberID + ".json";
    }

    public static String ofCensoredWord(String word, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\censoredwords\\" + word;
    }

    public static String ofCensoredWordWithJson(String word, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\censoredwords\\" + word + ".json";
    }

    public static String ofSetting(long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\";
    }

    public static String ofSetting(String trailingDir, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\" + trailingDir;
    }

    public static String ofGeneralSetting(String setting, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\general\\" + setting;
    }

    public static String ofGeneralSettingWithJson(String setting, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\general\\" + setting + ".json";
    }

    public static String ofGuildWhitelist(long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\whitelist";
    }

    public static String ofGuildWhitelistMember(long memberID, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\whitelist\\" + memberID;
    }

    public static String ofGuildWhitelistMemberWithJson(long memberID, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\whitelist\\" + memberID + ".json";
    }

    public static String ofSetStrike(int strike, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\sys\\setstrikes\\" + strike;
    }

    public static String ofSetStrikeWithJson(int strike, long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\sys\\setstrikes\\" + strike + ".json";
    }

    public static String ofSysSetting(long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\sys";
    }

    public static String ofSysSetting(long guildID, String trailingDir) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\settings\\sys\\" + trailingDir;
    }

    public static String ofMemory(long guildID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\memory";
    }

    public static String ofMemory(long guildID, String trailingDir) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + guildID + "\\memory\\" + trailingDir;
    }
}
