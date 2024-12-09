# What *is* Piggy_G?
Piggy_G is an auto mod bot used in Discord. It can do quite a few things, while some small,
other's most may find a huge aid in moderating a Discord server.

# How Do I Use It For My Server?
Piggy_G is not exactly intended for inviting it 
like a normal Discord bot; it is
designed for you to clone the bot's repository and run it off of your personal computer.

## How to Install
Follow these steps to install Piggy_G onto your computer and make your clone online.
It will be a lot of steps, but it will be worth it if done correctly.

### Setup
1. Create a GitHub account (if you don't have one already)
2. Download GitHub desktop with this link: https://desktop.github.com/download/
3. Download VS Code with this link: https://code.visualstudio.com/download
4. Once GitHub desktop is successfully installed, go back to the bot's repository, click on
   "Code" (it will be a green button) and then click "Open with GitHub Desktop". You should see a
   prompt open up asking you to clone it.
5. Click the blue button labeled "Clone". The bot's code should now be installing.
6. Once it is done cloning, click Repository (located at the top of the window), and click
   "Open in Visual Studio Code". VS Code should now be opening with the bot's code.
7. Once VS Code is opened, you will need to install the Java Extension. On the left, click the "Extensions"
   button and search "Java". Select the one titled "Extension Pack for Java".
8. Once the extension pack is finished installing, go back to your browser and open Discord Developer Portal.
9. Create a new application and call it "Piggy_G" or whatever you wish (it doesn't matter, as long as you can tell it's Piggy_G,
    you should be okay).
10. On the app's page, go to "bot", and then click "Reset Token". Enter your password when prompted and then copy the new token.
    ***It is very important that you do not lose this, or you will have to reset it again!***
11. Go back to VS Code (sorry for the bouncing back and forth lol), and then make a new file called *exactly* `.env`. Make sure the file
    is located in the parent directory (aka, the file's pathway should look like `Piggy_G/.env`.
12. Inside of the `.env` file, type out *exactly* `TOKEN=` and at the end paste your clone's token.
    It should look like `TOKEN=[your bot's token here]`.
13. Run the bot by opening the `Main.java` file, which is loctated in `src/main/java/net/korithekoder/piggyg/Main.java`. The run button is loctated
    near the top right, and it will look like a play button.
### Inviting the Bot to a Server
14. To add the bot to a server you need to enable certain permissions for the invite link. It is very simple (compared to the last steps you had to do lmao).
    To do so, go to the bot's page and click `OAuth2`. For the first section, select `bot` and `application.commands`. For the next section, just select `Administrator`.
    Copy the link that is generated at the bottom and then paste it into your search bar or send the link on your server.
15. Click the link, authorize the bot and you're done! It will take about 30 seconds for the bot to upload it's commands.

Enjoy!,  
Kori <3
