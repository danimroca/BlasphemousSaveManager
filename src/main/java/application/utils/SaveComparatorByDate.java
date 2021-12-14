package application.utils;

import application.model.Save;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;

public class SaveComparatorByDate implements Comparator<Save> {
    @Override
    public int compare(Save o1, Save o2) {
        BasicFileAttributes attr1 = null;
        BasicFileAttributes attr2 = null;
        try {
            attr1 = Files.readAttributes(o1.getSaveFile().toPath(), BasicFileAttributes.class);
            attr2 = Files.readAttributes(o2.getSaveFile().toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return attr1.creationTime().compareTo(attr2.creationTime());
    }
}
