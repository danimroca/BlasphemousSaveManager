package application.utils;

import application.model.Save;

import java.util.Comparator;

public class SaveComparatorByName implements Comparator<Save> {
    @Override
    public int compare(Save o1, Save o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
}
