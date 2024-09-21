package net.tiimzee.piggyg.resource;

public class ResourceDirectory {

    // Used for when you don't need a specific part of a directory (in the "resources" folder)

    public static String ofResource(final String DIR) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\" + DIR;
    }

    public static String ofServer(final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID;
    }

    public static String ofServer(final long GUILD_ID, String TRAILING_DIR) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\" + TRAILING_DIR;
    }

    public static String ofMember(final long MEMBER_ID, final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\memory\\members\\" + MEMBER_ID;
    }

    public static String ofMember(final long MEMBER_ID, final long GUILD_ID, final String TRAILING_DIR) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\memory\\members\\" + MEMBER_ID + "\\" + TRAILING_DIR;
    }

    public static String ofCensoredWord(final String WORD, final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\settings\\censoredwords\\" + WORD;
    }

    public static String ofCensoredWordWithJson(final String WORD, final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\settings\\censoredwords\\" + WORD + ".json";
    }

    public static String ofGeneralSetting(final String SETTING, final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\settings\\general\\" + SETTING;
    }

    public static String ofGeneralSettingWithJson(final String SETTING, final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\settings\\general\\" + SETTING + ".json";
    }

    public static String ofGuildWhitelist(final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\settings\\whitelist";
    }

    public static String ofGuildWhitelistMember(final long MEMBER_ID, final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\settings\\whitelist\\" + MEMBER_ID;
    }

    public static String ofGuildWhitelistMemberWithJson(final long MEMBER_ID, final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\settings\\whitelist\\" + MEMBER_ID + ".json";
    }

    public static String ofSetStrike(final int STRIKE, final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\settings\\sys\\setstrikes\\" + STRIKE;
    }

    public static String ofSetStrikeWithJson(final int STRIKE, final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\settings\\sys\\setstrikes\\" + STRIKE + ".json";
    }

    public static String ofSysSetting(final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\settings\\sys";
    }

    public static String ofSysSetting(final long GUILD_ID, final String TRAILING_DIR) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\settings\\sys\\" + TRAILING_DIR;
    }

    public static String ofMemory(final long GUILD_ID) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\memory";
    }

    public static String ofMemory(final long GUILD_ID, final String TRAILING_DIR) {
        return "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers\\" + GUILD_ID + "\\memory\\" + TRAILING_DIR;
    }
}
