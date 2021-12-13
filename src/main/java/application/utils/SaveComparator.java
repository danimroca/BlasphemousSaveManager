package application.utils;

import application.model.Save;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

//TODO not implemented save sorting yet
public class SaveComparator {

    public int compareSaveByDate(Save save1, Save save2) throws IOException {
        BasicFileAttributes attr1 = Files.readAttributes(save1.getSaveFile().toPath(), BasicFileAttributes.class);
        BasicFileAttributes attr2 = Files.readAttributes(save2.getSaveFile().toPath(), BasicFileAttributes.class);
        return attr1.creationTime().compareTo(attr2.creationTime());
    }

    public int compareSaveByName(Save save1, Save save2) {
        return save1.getName().compareToIgnoreCase(save2.getName());
    }

}
