package application.controllers;

import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;
import java.util.Properties;

public class BaseController {

    private static final String TITLE = "Blasphemous Save Manager";
    private static final String PROPERTIES_FILENAME = "savemanager.properties";

    protected Properties properties;

    protected Stage getMainStage() {
        return (Stage) Stage.getWindows().stream().filter(window -> ((Stage) window).getTitle().equals("Blasphemous Save Manager")).findFirst().get();
    }

    protected void closeCurrentStage() {
        ((Stage) Stage.getWindows().stream().filter(Window::isFocused).findFirst().get()).close();
    }

    protected Stage getCurrentStage() {
        return (Stage) Stage.getWindows().stream().filter(Window::isFocused).findFirst().get();
    }

    protected void loadProperties() {
        String userProfilePath = System.getenv("%UserProfile%");
        try {
            properties.load(new FileInputStream(
                    userProfilePath + File.separator + TITLE + File.separator + PROPERTIES_FILENAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void savePropertiesFile() throws IOException {
        String userProfilePath = System.getenv("%UserProfile%");
        FileOutputStream outputStream = new FileOutputStream(userProfilePath + File.separator + TITLE + File.separator + PROPERTIES_FILENAME);
        properties.store(outputStream, "Saving properties file");
        outputStream.close();
    }

    protected boolean isPropertiesFileExists() {
        String userProfilePath = System.getenv("%UserProfile%");
        File f = new File(userProfilePath + File.separator + TITLE + File.separator + PROPERTIES_FILENAME);
        return f.exists();
    }

    protected String getProfilePath() {
        return (String) properties.get("profilePath");
    }

    protected String getSavePath() {
        return (String) properties.get("savePath");
    }

    protected void updateProfilePath(String path) throws IOException {
        properties.put("profilePath", path);
        savePropertiesFile();
        loadProperties();
    }

    protected void updateSavePath(String path) throws IOException {
        properties.put("savePath", path);
        savePropertiesFile();
        loadProperties();
    }

}
