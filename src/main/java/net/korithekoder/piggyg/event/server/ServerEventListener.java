package net.korithekoder.piggyg.event.server;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
import static net.korithekoder.piggyg.resource.ResourceCreator.addFile;
import static net.korithekoder.piggyg.resource.ResourceCreator.addFolder;
import static net.korithekoder.piggyg.resource.ResourceCreator.addServerDirectory;
import static net.korithekoder.piggyg.resource.ResourceDirectory.*;
import static net.korithekoder.piggyg.resource.ResourceObtainer.*;

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

        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

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

        if (!isUserBanned(event.getUser(), event.getGuild().getIdLong(), event.getGuild())) {
            if (isWhitelistEnabled(event.getGuild().getIdLong())) {
                if (isUserWhitelisted(event.getUser(), event.getGuild().getIdLong())) {
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

        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

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

        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

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

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (!new File(ofServer(event.getGuild().getIdLong())).exists()) addServerDirectory(event.getGuild().getIdLong(), event.getGuild().getMembers(), event.getGuild());

        File logFile = new File(ofServer(event.getGuild().getIdLong(), "logs\\voice_channel_logs.txt"));

        out.println("osdkvhsodihspdihfsdpfhpsdhvspdvhpsdpfhvpsdhsph");

        try {
            if (logFile.createNewFile());

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm:ss");

            Supplier<String> logText = () -> {
                String toReturn = "";
                if (!(event.getChannelJoined() == null) && (event.getChannelLeft() == null)) {
                    toReturn = "[LOG][" + dtf.format(LocalDate.now()) + "..." + dtf2.format(LocalTime.now()) + "]: @" + event.getMember().getNickname() + " JOINED " + event.getChannelJoined().getName();
                } else if (!(event.getChannelJoined() == null) && !(event.getChannelLeft() == null)) {
                    toReturn = "[LOG][" + dtf.format(LocalDate.now()) + "..." + dtf2.format(LocalTime.now()) + "]: @" + event.getMember().getNickname() + " JOINED " + event.getChannelJoined().getName() + " FROM " + event.getChannelLeft().getName();
                } else if ((event.getChannelJoined() == null) && !(event.getChannelLeft() == null)) {
                    toReturn = "[LOG][" + dtf.format(LocalDate.now()) + "..." + dtf2.format(LocalTime.now()) + "]: @" + event.getMember().getNickname() + " LEFT " + event.getChannelLeft().getName();
                }
                return toReturn;
            };

            FileWriter writer = new FileWriter(logFile, true);
            writer.write(logText.get());
            writer.close();
        } catch (Exception e) {
            out.println(e);
        }
    }
}
