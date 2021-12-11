package application.controllers;

import application.model.Profile;
import application.model.Profiles;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class BaseController {

    private static final String TITLE = "BlasphemousSaveManager";
    private static final String PROPERTIES_FILENAME = "savemanager.properties";
    private static final String PROFILES_FILENAME = "profiles.xml";

    protected Properties properties = new Properties();

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
        //we check if there's a properties file, if not we create it.
        if (!isPropertiesFileExists()) {
            try {
                savePropertiesFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String appDataPath = System.getenv("APPDATA");
        try {
            properties.load(new FileInputStream(
                    appDataPath + File.separator + TITLE + File.separator + PROPERTIES_FILENAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void savePropertiesFile() throws IOException {
        String appDataPath = System.getenv("APPDATA");
        File f = new File(appDataPath + File.separator + TITLE + File.separator + PROPERTIES_FILENAME);
        if (!f.exists()) {
            new File(appDataPath + File.separator + TITLE).mkdirs();
            f.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(appDataPath + File.separator + TITLE + File.separator + PROPERTIES_FILENAME);
        properties.store(outputStream, "Saving properties file");
        outputStream.close();
    }

    protected void savesProfilesToXMLFile(List<Profile> profileList) {
        Marshaller jaxbMarshaller = null;
        try {
            Profiles profiles = new Profiles();
            profiles.setProfiles(profileList);
            JAXBContext jaxbContext = JAXBContext.newInstance(Profiles.class);
            jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(profiles, new File(getProfilesFilePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean isPropertiesFileExists() {
        String appDataPath = System.getenv("APPDATA");
        File f = new File(appDataPath + File.separator + TITLE + File.separator + PROPERTIES_FILENAME);
        return f.exists();
    }

    protected String getProfilesFilePath() {
        return getProfilePath() + File.separator + PROFILES_FILENAME;
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
