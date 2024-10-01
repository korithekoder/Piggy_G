package net.tiimzee.piggyg.resource;

import java.io.File;
import java.io.FileWriter;

import static java.lang.System.out;

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
}
