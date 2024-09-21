package net.tiimzee.piggyg.resource;

import java.io.File;
import java.io.FileWriter;

import static java.lang.System.out;

public class ResourceCreator {

    public static void addFolder(String folderDir) {
        File guildDir = new File(folderDir);
        boolean isFolderMade = guildDir.mkdir();
    }

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
