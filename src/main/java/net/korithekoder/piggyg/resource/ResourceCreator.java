package net.korithekoder.piggyg.resource;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import static java.lang.System.out;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofMember;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;

/**
 * Class used for creating files and folders with ease
 */
public class ResourceCreator {

    /**
     * Creates a folder in the user's system
     * @param folderDir The directory to make the folder in
     */
    public static void addFolder(String folderDir) {
        File guildDir = new File(folderDir);
        if (guildDir.mkdir());
    }

    /**
     * Create a file with ease, rather than creating a file object
     * Mainly, it's just easier for me to not make objects over and over
     * :3
     * @param fileDir Directory to make the file in
     * @param content The data and content that the file will contain
     */
    public static void addFile(String fileDir, String content) {
        try {
            File newFile = new File(fileDir);
            try {
                if (newFile.createNewFile() || !newFile.createNewFile()) {
                    FileWriter writer = new FileWriter(newFile);
                    writer.write(content);
                    writer.close();
                }
            } catch (Exception e) {
                out.println(e);
            }
        } catch(Exception e){
            out.println(e);
        }
    }

    /**
     * Creates an entire new server directory in Piggy_G's AppData folder.
     * @param event Event object to be passed down
     */
    public static void addServerDirectory(GuildJoinEvent event) {

        JDA client = event.getJDA();
        final long NEW_GUILD_ID = event.getGuild().getIdLong();

        /*
         * Add all of the folders and files for the
         * new server
         */
        addFolder(ofServer(NEW_GUILD_ID));
        addFolder(ofServer(NEW_GUILD_ID, "logs"));
        addFolder(ofServer(NEW_GUILD_ID, "settings"));
        addFolder(ofServer(NEW_GUILD_ID, "settings\\sys"));
        addFolder(ofServer(NEW_GUILD_ID, "settings\\sys\\setstrikes"));
        addFolder(ofServer(NEW_GUILD_ID, "settings\\general"));
        addFolder(ofServer(NEW_GUILD_ID, "settings\\censoredwords"));
        addFolder(ofServer(NEW_GUILD_ID, "settings\\whitelist"));
        addFolder(ofServer(NEW_GUILD_ID, "memory"));
        addFolder(ofServer(NEW_GUILD_ID, "memory\\members"));
        addFolder(ofServer(NEW_GUILD_ID, "memory\\joinattempts"));

        for (Member member : event.getGuild().getMembers()) {
            if (!member.getUser().isBot()) {
                addFolder(ofMember(member.getIdLong(), event.getGuild().getIdLong()));
                addFolder(ofMember(member.getIdLong(), event.getGuild().getIdLong(), "strikes"));

                addFile(
                    ofMember(member.getIdLong(), event.getGuild().getIdLong(), "strikes\\count.json"),
                    "{\n  \"count\": 0\n}"
                );
            }
        }

        /*
         * Registers the bot's commands
         */
        registerCommands(client.getGuildById(NEW_GUILD_ID));
    }
    
    /**
     * Creates an entire new server directory in Piggy_G's AppData folder.
     * This version is mainly used for when Piggy_G detects that a server's directory
     * is missing and needs to create one.
     * @param guildID ID of the guild
     * @param members List of members from the guild
     * @param guild Guild as an object
     */
    public static void addServerDirectory(long guildID, List<Member> members, Guild guild) {

        /*
         * Add all of the folders and files for the
         * new server
         */
        addFolder(ofServer(guildID));
        addFolder(ofServer(guildID, "logs"));
        addFolder(ofServer(guildID, "settings"));
        addFolder(ofServer(guildID, "settings\\sys"));
        addFolder(ofServer(guildID, "settings\\sys\\setstrikes"));
        addFolder(ofServer(guildID, "settings\\general"));
        addFolder(ofServer(guildID, "settings\\censoredwords"));
        addFolder(ofServer(guildID, "settings\\whitelist"));
        addFolder(ofServer(guildID, "memory"));
        addFolder(ofServer(guildID, "memory\\members"));
        addFolder(ofServer(guildID, "memory\\joinattempts"));

        for (Member member : members) {
            if (!member.getUser().isBot()) {
                addFolder(ofMember(member.getIdLong(), guildID));
                addFolder(ofMember(member.getIdLong(), guildID, "strikes"));

                addFile(
                    ofMember(member.getIdLong(), guildID, "strikes\\count.json"),
                    "{\n  \"count\": 0\n}"
                );
            }
        }

        /*
         * Registers the bot's commands
         */
        registerCommands(guild);
    }

    /**
     * Register all of Piggy_G's commands on a server
     * @param guild The guild object used to upsert the commands
     */
    public static void registerCommands(Guild guild) {

        /**
         * Upsert troll command
         */
        guild.upsertCommand("troll", "Send someone on the server a DM (without telling them you sent it)").addOptions(
            new OptionData(OptionType.USER, "user", "The user you want to troll", true),
            new OptionData(OptionType.STRING, "message", "The funny message you want to send", true),
            new OptionData(OptionType.ATTACHMENT, "attachment", "OPTIONAL: Add any attachment to the funny message", false),
            new OptionData(OptionType.BOOLEAN, "anonymous", "OPTIONAL: Do not make the bot reply when the troll was sent", false)
        ).queue();

        /**
         * Upsert addcensoredword command
         */
        guild.upsertCommand("addcensoredword", "Adds a censored word that is not allowed to be said").addOptions(
            new OptionData(OptionType.STRING, "word", "Word to be censored", true).setMaxLength(2000),
            new OptionData(OptionType.INTEGER, "addstrikes", "Adds how many strikes to the user's count (default value is 1, max is 9999999999999)", false).setMinValue(1).setMaxValue(9999999999999L)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        /**
         * Upsert setstrikecount command
         */
        guild.upsertCommand("setstrikecount", "Sets the maximum strikes a user can get before they are banned").addOptions(
            new OptionData(OptionType.INTEGER, "count", "Count to be set (max is 9999999999999)", true).setMinValue(0).setMaxValue(9999999999999L)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        /**
         * Upsert removecensoredword command
         */
        guild.upsertCommand("removecensoredword", "Removes an existing censored word").addOptions(
            new OptionData(OptionType.STRING, "word", "Word to be marked for removal", true).setMaxLength(2000)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        /**
         * Upsert setmailchannel command
         */
        guild.upsertCommand("setmailchannel", "Sets the channel admins receive mail and messages").addOptions(
            new OptionData(OptionType.CHANNEL, "channel", "The channel to be set", true)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL)).queue();

        /**
         * Upsert sendmodmail command
         */
        guild.upsertCommand("sendmodmail", "Send mail to the set mail channel").addOptions(
            new OptionData(OptionType.STRING, "message", "Mail to be sent", true).setMaxLength(2000)
        ).queue();

        /**
         * Upsert setreportchannel command
         */
        guild.upsertCommand("setreportchannel", "Sets the channel admins receive user reports").addOptions(
            new OptionData(OptionType.CHANNEL, "channel", "The channel to be set", true)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL)).queue();

        /**
         * Upsert report command
         */
        guild.upsertCommand("report", "Report someone on this server. The admins will receive a report (if they set the channel)").addOptions(
            new OptionData(OptionType.USER, "user", "User to report", true),
            new OptionData(OptionType.STRING, "reason", "Reason why user is being reported (please keep this part around one word)", true).setMaxLength(200),
            new OptionData(OptionType.STRING, "description", "Details on what happened", true)
        ).queue();

        /**
         * Upsert settimeoutstrike command
         */
        guild.upsertCommand("settimeoutstrike", "Sets a strike to be a server timeout (for a set time)").addOptions(
            new OptionData(OptionType.INTEGER, "strike", "Strike to be set as a timeout", true).setMinValue(1).setMaxValue(9999999999999L),
            new OptionData(OptionType.STRING, "type", "s=Seconds, m=Minutes, h=Hours, d=Days", true).setMinLength(1).setMaxLength(1),
            new OptionData(OptionType.INTEGER, "time", "Amount of time to be set", true).setMinValue(1).setMaxValue(9999999999999L)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)).queue();

        /**
         * Upsert setkickstrike command
         */
        guild.upsertCommand("setkickstrike", "Sets a strike to be a server kick").addOptions(
            new OptionData(OptionType.INTEGER, "strike", "Strike to be set as a kick", true).setMinValue(1).setMaxValue(9999999999999L)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)).queue();

        /**
         * Upsert permaban command
         */
        guild.upsertCommand("permaban", "Permanently bans a user (rather than the 7 days limit)").addOptions(
            new OptionData(OptionType.USER, "user", "User to permanently ban from this server", true)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)).queue();

        /**
         * Upsert unpermaban command
         */
        guild.upsertCommand("unpermaban", "Revokes the permaban from a permanently banned user").addOptions(
            new OptionData(OptionType.USER, "user", "User to revoke permaban from this server", true)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)).queue();

        /**
         * Upsert obtaintrolllogs command
         */
        guild.upsertCommand("obtaintrolllogs", "Gets the .txt troll logs").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        /**
         * Upsert obtaindisplaynamelogs command
         */
        guild.upsertCommand("obtaindisplaynamelogs", "Gets the .txt display name change logs").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        /**
         * Upsert obtainvoicechannellogs command
         */
        // guild.upsertCommand("obtainvoicechannellogs", "Gets the .txt voice channel logs when people join/leave").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        /**
         * Upsert enablewhitelist command
         */
        guild.upsertCommand("enablewhitelist", "ENABLES the whitelist system").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        /**
         * Upsert disablewhitelist command
         */
        guild.upsertCommand("disablewhitelist", "DISABLES the whitelist system").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();

        /**
         * Upsert whitelist command
         */
        guild.upsertCommand("whitelist", "Add a member to the whitelist").addOptions(
            new OptionData(OptionType.STRING, "user_id", "User to whitelist to the server", true)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)).queue();

        /**
         * Upsert blacklist command
         */
        guild.upsertCommand("blacklist", "Blacklist a member from the server").addOptions(
            new OptionData(OptionType.USER, "user", "User to blacklist", true),
            new OptionData(OptionType.STRING, "reason", "Reason for blacklist", false)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)).queue();

        /**
         * Upsert setreporttimeout command
         */
        guild.upsertCommand("setreporttimeout", "Set the timeout for when a user is reported using the /report command").addOptions(
            new OptionData(OptionType.STRING, "type", "s=Seconds, m=Minutes, h=Hours, d=Days", true).setMinLength(1).setMaxLength(1),
            new OptionData(OptionType.INTEGER, "time", "Amount of time to be set", true).setMinValue(1).setMaxValue(9999999999999L)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)).queue();
    }
}
