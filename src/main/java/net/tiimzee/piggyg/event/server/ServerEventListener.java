package net.tiimzee.piggyg.event.server;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.lang.System.out;
import static net.tiimzee.piggyg.resource.ResourceCreator.addFile;
import static net.tiimzee.piggyg.resource.ResourceCreator.addFolder;
import static net.tiimzee.piggyg.resource.ResourceDirectory.*;
import static net.tiimzee.piggyg.resource.ResourceObtainer.*;

/**
 * Core class for general server event listening events
 */
public class ServerEventListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {

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

        final Guild COMMAND_REGISTER = client.getGuildById(NEW_GUILD_ID);
        assert COMMAND_REGISTER != null;

        COMMAND_REGISTER.upsertCommand("troll", "Send someone a DM (without telling them you sent it)").addOptions(
            new OptionData(OptionType.MENTIONABLE, "user", "The user you want to troll", true),
            new OptionData(OptionType.STRING, "message", "The funny message you want to send", true).setMaxLength(2000)
        ).queue();
        COMMAND_REGISTER.upsertCommand("addcensoredword", "Adds a censored word that is not allowed to be said").addOptions(
            new OptionData(OptionType.STRING, "word", "Word to be censored", true).setMaxLength(2000),
            new OptionData(OptionType.INTEGER, "addstrikes", "Adds how many strikes to the user's count (default value is 1, max is 9999999999999)", false).setMinValue(1).setMaxValue(9999999999999L)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        COMMAND_REGISTER.upsertCommand("setstrikecount", "Sets the maximum strikes a user can get before they are banned").addOptions(
            new OptionData(OptionType.INTEGER, "count", "Count to be set (max is 9999999999999)", true).setMinValue(0).setMaxValue(9999999999999L)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        COMMAND_REGISTER.upsertCommand("removecensoredword", "Removes an existing censored word").addOptions(
            new OptionData(OptionType.STRING, "word", "Word to be marked for removal", true).setMaxLength(2000)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        COMMAND_REGISTER.upsertCommand("setmailchannel", "Sets the channel admins receive mail and messages").addOptions(
            new OptionData(OptionType.CHANNEL, "channel", "The channel to be set", true)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL)).queue();
        COMMAND_REGISTER.upsertCommand("sendmodmail", "Send mail to the set mail channel").addOptions(
            new OptionData(OptionType.STRING, "message", "Mail to be sent", true).setMaxLength(2000)
        ).queue();
        COMMAND_REGISTER.upsertCommand("setreportchannel", "Sets the channel admins receive user reports").addOptions(
            new OptionData(OptionType.CHANNEL, "channel", "The channel to be set", true)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL)).queue();
        COMMAND_REGISTER.upsertCommand("report", "Report someone on this server. The admins will receive a report (if they set the channel)").addOptions(
            new OptionData(OptionType.USER, "user", "User to report", true),
            new OptionData(OptionType.STRING, "reason", "Reason why user is being reported (please keep this part around one word)", true).setMaxLength(200),
            new OptionData(OptionType.STRING, "description", "Details on what happened", true)
        ).queue();
        COMMAND_REGISTER.upsertCommand("settimeoutstrike", "Sets a strike to be a server timeout (for a set time)").addOptions(
            new OptionData(OptionType.INTEGER, "strike", "Strike to be set as a timeout", true).setMinValue(1).setMaxValue(9999999999999L),
            new OptionData(OptionType.STRING, "type", "s=Seconds, m=Minutes, h=Hours, d=Days", true).setMinLength(1).setMaxLength(1),
            new OptionData(OptionType.INTEGER, "time", "Amount of time to be set", true).setMinValue(1).setMaxValue(9999999999999L)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)).queue();
        COMMAND_REGISTER.upsertCommand("setkickstrike", "Sets a strike to be a server kick").addOptions(
            new OptionData(OptionType.INTEGER, "strike", "Strike to be set as a kick", true).setMinValue(1).setMaxValue(9999999999999L)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)).queue();
        COMMAND_REGISTER.upsertCommand("permaban", "Permanently bans a user (rather than the 7 days limit)").addOptions(
            new OptionData(OptionType.USER, "user", "User to permanently ban from this server", true)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)).queue();
        COMMAND_REGISTER.upsertCommand("unpermaban", "Revokes the permaban from a permanently banned user").addOptions(
            new OptionData(OptionType.USER, "user", "User to revoke permaban from this server", true)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)).queue();
        COMMAND_REGISTER.upsertCommand("obtaintrolllogs", "Gets the .txt troll logs").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        COMMAND_REGISTER.upsertCommand("obtaindisplaynamelogs", "Gets the .txt display name change logs").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        COMMAND_REGISTER.upsertCommand("enablewhitelist", "ENABLES the whitelist system").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        COMMAND_REGISTER.upsertCommand("disablewhitelist", "DISABLES the whitelist system").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        COMMAND_REGISTER.upsertCommand("whitelist", "Add a member to the whitelist").addOptions(
            new OptionData(OptionType.STRING, "user_id", "User to whitelist to the server", true)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).queue();
        COMMAND_REGISTER.upsertCommand("setreporttimeout", "Set the timeout for when a user is reported using the /report command").addOptions(
            new OptionData(OptionType.STRING, "type", "s=Seconds, m=Minutes, h=Hours, d=Days", true).setMinLength(1).setMaxLength(1),
            new OptionData(OptionType.INTEGER, "time", "Amount of time to be set", true).setMinValue(1).setMaxValue(9999999999999L)
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)).queue();

    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        File userJoinAttempts = new File(ofMemory(event.getGuild().getIdLong(), "joinattempts\\" + event.getUser().getIdLong() + ".json"));
        if (!userJoinAttempts.exists()) {
            addFile(
                    ofMemory(event.getGuild().getIdLong(), "joinattempts\\" + event.getUser().getIdLong() + ".json"),
                    """
                    {
                      "count": 0
                    }
                    """
            );
        }

        if (!isUserBanned(event.getUser(), event.getGuild().getIdLong())) {
            if (isUserWhitelisted(event.getUser(), event.getGuild().getIdLong()) && isWhitelistEnabled(event.getGuild().getIdLong())) {
                addFolder(ofMember(event.getUser().getIdLong(), event.getGuild().getIdLong()));
                addFolder(ofMember(event.getUser().getIdLong(), event.getGuild().getIdLong(), "strikes"));

                if (!new File(ofMember(event.getUser().getIdLong(), event.getGuild().getIdLong(), "strikes\\count.json")).exists()) {
                    addFile(
                            ofMember(event.getUser().getIdLong(), event.getGuild().getIdLong(), "strikes\\count.json"),
                            "{\n  \"count\": 0\n}"
                    );
                }
            } else {
                event.getGuild().kick(event.getUser()).queue();

                JSONObject data = new JSONObject(getFileContent(userJoinAttempts));
                int count = data.getInt("count");
                count++;

                switch (count) {
                    case 1 -> event.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("""
                    # Hey man, I can't let you through, dawg
                    You ain't whitelisted
                    """)).queue();
                    case 2 -> event.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("""
                    # Uhh, have I seen you before?
                    Hippity hop your way on out of here.
                    You still ain't whitelisted dawg :man_facepalming:
                    """)).queue();
                    case 3 -> event.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("""
                    # Man, I get it, you wanna join the server
                    ...but you ain't whitelisted.
                    Get yo ass up on out of here, before you
                    become a nationally known opp. :boom::gun:
                    Thank you.
                    """)).queue();
                    case 4 -> event.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("""
                    # ***Stop trying***
                    I ain't gonn' let you through until you get whitelisted.
                    If you really wanna join the server, then ask a mod,
                    an admin. ***Don't come to me***.
                    ...you fucking ***dumbass***. :man_facepalming:
                    """)).queue();
                    case 5 -> {
                        event.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("""
                        # ***This is yo fifth time***
                        Fuck off pigga :rage::middle_finger:
                        (you know why)
                        """)).queue();

                        if (!new File(ofSysSetting(event.getGuild().getIdLong(), "permabans")).exists()) {
                            addFolder(ofSysSetting(event.getGuild().getIdLong(), "permabans"));
                        }
                
                        addFile(
                            ofSysSetting(event.getGuild().getIdLong(), "permabans\\" + event.getUser().getIdLong() + ".json"),
                            "{}"
                        );

                        event.getGuild().ban(event.getUser(), 7, TimeUnit.DAYS).reason("Permabanned, cuz").queue();
                    }
                }

                try {
                    FileWriter writer = new FileWriter(userJoinAttempts);
                    writer.write("{\n  \"count\": " + count + "\n}");
                    writer.close();
                } catch (Exception ignored) {
                }

                while (new File(ofMember(event.getUser().getIdLong(), event.getGuild().getIdLong())).exists()) {
                    try {
                        deleteDirectory(Path.of(ofMember(event.getUser().getIdLong(), event.getGuild().getIdLong())));
                    } catch (Exception ignored) {
                    }
                }
            }
        } else {
            event.getGuild().ban(event.getUser(), 7, TimeUnit.DAYS).reason("Permabanned, cuz").queue();

            while (new File(ofMember(event.getUser().getIdLong(), event.getGuild().getIdLong())).exists()) {
                try {
                    deleteDirectory(Path.of(ofMember(event.getUser().getIdLong(), event.getGuild().getIdLong())));
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        event.getGuild()
                .retrieveAuditLogs()
                .queueAfter(1, TimeUnit.SECONDS, (logs) -> {  // Wait a second for discord to populate the logs properly
                    boolean isBan = false, isKick = false;
                    for (AuditLogEntry log : logs) {
                        if (log.getTargetIdLong() == event.getUser().getIdLong()) {
                            isBan = log.getType() == ActionType.BAN;
                            isKick = log.getType() == ActionType.KICK;
                            break;
                        }
                    }

                    // Remove the member from the whitelist (if it's enabled)

                    if (isWhitelistEnabled(event.getGuild().getIdLong())) {
                        if (isKick || isBan) {
                            File memberWhitelistFile = new File(ofGuildWhitelistMemberWithJson(event.getMember().getIdLong(), event.getGuild().getIdLong()));
                            while (memberWhitelistFile.exists()) {
                                if (memberWhitelistFile.delete());
                            }
                        }
                    }

                    if (isBan) {
                        event.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("""
                            Yo yo yo, what's good piggy gang.
                            So I heard some:
                            * **BOOTY SNIFFIN'**,
                            * **OPPOSITION**
                            * **"I LIKE TO TEXT MY *DISCORD KITTEN*™ :nerd:"**
                            lookin' ahh mufucka, is messin' with the barn.
                            *So*...
                            This is your *only* warnin'...
                            My name... is ***Piggy_G***,
                            I'm a real gangster,
                            I stay on all fours (no homo),
                            and if you mess with the barn, *man*,
                            I will attack yo farm.
                            
                            So get yo *"SuPeR sAiYaN :nerd:"*, Discord © gamin'!
                            "Gurl, *S L A Y*" sayin',
                            # offa my turf.
                            """)).queue();
                        while (new File(ofMember(event.getMember().getIdLong(), event.getGuild().getIdLong())).exists()) {
                            try {
                                deleteDirectory(Path.of(ofMember(event.getMember().getIdLong(), event.getGuild().getIdLong())));
                            } catch (Exception ignored) {
                            }
                        }
                    }
                });
    }

    @Override
    public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
        File logFile = new File(ofServer(event.getGuild().getIdLong(), "logs\\display_name_changes.txt"));

        try {
            if (logFile.createNewFile());
            Supplier<String> oldDisplayName = () -> {
                if (event.getOldNickname() == null) {
                    return event.getUser().getGlobalName();
                } else {
                    return event.getOldNickname();
                }
            };
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm:ss");
            FileWriter writer = new FileWriter(logFile, true);
            writer.write("[LOG][" + dtf.format(LocalDate.now()) + "..." + dtf2.format(LocalTime.now()) + "][@" + event.getUser().getGlobalName() + "]: \"" + oldDisplayName.get() + "\" -> \"" + event.getNewNickname() + "\"\n");
            writer.close();
        } catch (Exception e) {
            out.println(e);
        }
    }
}
