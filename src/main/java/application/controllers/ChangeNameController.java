package application.controllers;

import application.model.Profile;
import application.model.Save;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChangeNameController extends BaseController {

    @FXML
    private TextField saveNameTextField;
    private List<Profile> profiles;
    private Save save;

    public void cancel() {
        closeCurrentStage();
    }

    public void accept() throws IOException {
        loadProperties();
        String saveName = saveNameTextField.getText().trim();
        File oldSaveName = null;
        File f = null;
        File f2 = null;
        for (Profile p:profiles) {
            for (Save s:p.getSaves()) {
                if (s.equals(save)) {
                    oldSaveName = s.getSaveFile();
                    f = s.getSaveFile();
                    f2 = s.getBackupSaveFile();
                    s.setName(saveName);
                }
            }
        }

        File saveFolder = new File(f.getParentFile().getParent() + File.separator + saveName);
        if (!saveFolder.exists()) {
            saveFolder.mkdir();
        }

        FileUtils.moveFileToDirectory(f, saveFolder, false);
        if (f2.exists()) {
            FileUtils.moveFileToDirectory(f2, saveFolder, false);
        }
        FileUtils.deleteDirectory(oldSaveName.getParentFile());


        for (Profile p:profiles) {
            for (Save s:p.getSaves()) {
                if (s.equals(save)) {
                    s.setSaveFile(new File(saveFolder + File.separator + f.getName()));
                    if (f2.exists()) {
                        s.setBackupSaveFile(new File(saveFolder + File.separator + f2.getName()));
                    }
                }
            }
        }

        savesProfilesToXMLFile(profiles);
        closeCurrentStage();
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public void setSave(Save save) {
        this.save = save;
    }

}
