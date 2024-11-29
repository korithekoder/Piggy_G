/**
 * Piggy_G
 * An auto mod bot made in Java
 * by korithekoder
 * 
 * This bot uses the Common Creative license,
 * basically meaning that you can reuse and modify it so long as you
 * give proper attribution.
 * 
 * Please also note that this bot pretty much relies on the fact that
 * all of the data (things like settings, memory, etc.) are usually stored on
 * the desktop that Piggy_G was made on.
 * 
 * In other words (this is mostly note-to-self): ONLY CLONE THE BOT'S REPOSITORY IF YOU NEED TO
 * TEST NEW FEATURES OR HOST IT ON ANOTHER COMPUTER IF YOU ABSOLUTELY HAVE TO!!
 * 
 * IMPORTANT!!!
 * When Piggy_G gets ran, it will check for any missing
 * important folders and files that it relies on. It will get most
 * of it, but it needs the C: drive and a proper user folder. If it isn't there, the bot will
 * REFUSE to work properly. 
 */

package net.korithekoder.piggyg;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.korithekoder.piggyg.command.funny.*;
import net.korithekoder.piggyg.command.lang.*;
import net.korithekoder.piggyg.command.notifications.*;
import net.korithekoder.piggyg.command.sys.*;
import net.korithekoder.piggyg.command.user.*;
import net.korithekoder.piggyg.event.message.MessageEventListener;
import net.korithekoder.piggyg.event.server.ServerEventListener;

import static net.korithekoder.piggyg.resource.ResourceCreator.addFolder;

/**
 * The main class that initializes the entire bot
 */
public class Main {

    /**
     * The bot's token (basically its password). 
     * NOTE: This token is stored in a .env file, which is
     * ignored using the .gitignore file for GitHub
     */
    final public static String TOKEN = Dotenv.load().get("TOKEN");

    /**
     * JDA object used throughout the Main.java file
     */
    public static JDA client = JDABuilder.createLight(TOKEN, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES)
        .setEventPassthrough(true)
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .setChunkingFilter(ChunkingFilter.ALL)
        .build();

    /**
     * The main method that gets ran upon being compiled and executed
     */
    public static void main(String[] args) {

        /*
         * Add all the important folders that
         * the bot will need (if they are missing)
         */

        addFolder("C:\\Users\\" + System.getProperty("user.name") + "\\AppData");
        addFolder("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming");
        addFolder("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G");
        addFolder("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources");
        addFolder("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Piggy_G\\resources\\servers");

        /*
         * Adds the bot's event listeners
         */

        // Main events
        client.addEventListener(new MessageEventListener());
        client.addEventListener(new ServerEventListener());

        // Command events
        client.addEventListener(new Troll());
        client.addEventListener(new AddCensoredWord());
        client.addEventListener(new RemoveCensoredWord());
        client.addEventListener(new SetStrikeCount());
        client.addEventListener(new SetMailChannel());
        client.addEventListener(new SendModMail());
        client.addEventListener(new SetReportChannel());
        client.addEventListener(new SendReport());
        client.addEventListener(new SetTimeoutStrike());
        client.addEventListener(new SetKickStrike());
        client.addEventListener(new Permaban());
        client.addEventListener(new Unpermaban());
        client.addEventListener(new ObtainTrollLogs());
        client.addEventListener(new ObtainDisplayNameLogs());
        client.addEventListener(new WhitelistMember());
        client.addEventListener(new BlacklistMember());
        client.addEventListener(new EnableWhitelist());
        client.addEventListener(new DisableWhitelist());
        client.addEventListener(new SetReportTimeout());
    }
}
