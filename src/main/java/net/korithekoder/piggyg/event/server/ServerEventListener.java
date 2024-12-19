package net.korithekoder.piggyg.event.server;

import java.io.File;
import java.io.FileWriter;
import static java.lang.System.out;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateNameEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateRolesEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofGuildWhitelistMemberWithJson;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofMember;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofMemory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofServer;
import static net.korithekoder.piggyg.resource.ResourceDirectory.ofSetting;
import static net.korithekoder.piggyg.resource.ResourceObtainer.checkIsMemberWhitelisted;
import static net.korithekoder.piggyg.resource.ResourceObtainer.deleteDirectory;
import static net.korithekoder.piggyg.resource.ResourceObtainer.getFilesNamesAsLong;
import static net.korithekoder.piggyg.resource.ResourceObtainer.getUserJoinAttemptsFile;
import static net.korithekoder.piggyg.resource.ResourceObtainer.isWhitelistEnabled;

/**
 * Core class for general server event listening events
 */
public class ServerEventListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        addServerDirectory(event);
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        while (new File(ofServer(event.getGuild().getIdLong())).exists()) {
            try {
                deleteDirectory(Path.of(ofServer(event.getGuild().getIdLong())));
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) {
            addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());
        }

        for (long roleId : getFilesNamesAsLong(ofSetting("newmemberroles", event.getGuild().getIdLong()))) {
            event.getGuild().addRoleToMember(event.getMember().getUser(), event.getGuild().getRoleById(roleId)).queue();
        }

        checkIsMemberWhitelisted(event.getGuild(), event.getUser(), getUserJoinAttemptsFile(event.getGuild().getIdLong(), event.getUser().getIdLong()));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        checkIsMemberWhitelisted(event.getGuild(), event.getUser(), getUserJoinAttemptsFile(event.getGuild().getIdLong(), event.getUser().getIdLong()));
    }

    @Override
    public void onEmojiAdded(EmojiAddedEvent event) {
        checkIsMemberWhitelisted(event.getGuild(), event.getEmoji().getOwner(), getUserJoinAttemptsFile(event.getGuild().getIdLong(), event.getEmoji().getOwner().getIdLong()));
    }

    @Override
    public void onEmojiRemoved(EmojiRemovedEvent event) {
        checkIsMemberWhitelisted(event.getGuild(), event.getEmoji().getOwner(), getUserJoinAttemptsFile(event.getGuild().getIdLong(), event.getEmoji().getOwner().getIdLong()));
    }

    @Override
    public void onEmojiUpdateName(EmojiUpdateNameEvent event) {
        checkIsMemberWhitelisted(event.getGuild(), event.getEmoji().getOwner(), getUserJoinAttemptsFile(event.getGuild().getIdLong(), event.getEmoji().getOwner().getIdLong()));
    }

    @Override
    public void onEmojiUpdateRoles(EmojiUpdateRolesEvent event) {
        checkIsMemberWhitelisted(event.getGuild(), event.getEmoji().getOwner(), getUserJoinAttemptsFile(event.getGuild().getIdLong(), event.getEmoji().getOwner().getIdLong()));
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) {
            addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());
        }

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

        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) {
            addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());
        }

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
        checkIsMemberWhitelisted(event.getGuild(), event.getUser(), getUserJoinAttemptsFile(event.getGuild().getIdLong(), event.getUser().getIdLong()));
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) {
            addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());
        }

        File logFile = new File(ofServer(event.getGuild().getIdLong(), "logs\\voice_channel_logs.txt"));

        try {
            if (logFile.createNewFile());

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm:ss");

            Supplier<String> logText = () -> {
                String toReturn = "";
                if (!(event.getChannelJoined() == null) && (event.getChannelLeft() == null)) {
                    toReturn = "[LOG][" + dtf.format(LocalDate.now()) + "..." + dtf2.format(LocalTime.now()) + "]: @" + event.getMember().getUser().getName() + " JOINED " + event.getChannelJoined().getName() + "\n";
                } else if (!(event.getChannelJoined() == null) && !(event.getChannelLeft() == null)) {
                    toReturn = "[LOG][" + dtf.format(LocalDate.now()) + "..." + dtf2.format(LocalTime.now()) + "]: @" + event.getMember().getUser().getName() + " JOINED " + event.getChannelJoined().getName() + " FROM " + event.getChannelLeft().getName() + "\n";
                } else if ((event.getChannelJoined() == null) && !(event.getChannelLeft() == null)) {
                    toReturn = "[LOG][" + dtf.format(LocalDate.now()) + "..." + dtf2.format(LocalTime.now()) + "]: @" + event.getMember().getUser().getName() + " LEFT " + event.getChannelLeft().getName() + "\n";
                }
                return toReturn;
            };

            FileWriter writer = new FileWriter(logFile, true);
            writer.write(logText.get());
            writer.close();
        } catch (Exception e) {
            out.println(e);
        }

        checkIsMemberWhitelisted(event.getGuild(), event.getMember().getUser(), new File(ofMemory(event.getGuild().getIdLong(), "joinattempts\\" + event.getMember().getUser().getIdLong() + ".json")));
    }
}
