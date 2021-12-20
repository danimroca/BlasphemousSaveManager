package application.controllers;

import application.model.Profile;
import application.model.Save;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SaveChangeNameController extends BaseController {

    @FXML
    private TextField saveNameTextField;
    private List<Profile> profiles;
    private Save save;

    public void cancel() {
        closeStage(saveNameTextField.getScene().getWindow());
    }

    public void accept() throws IOException {
        String saveName = saveNameTextField.getText().trim();
        if (saveName.isEmpty()) {
            return;
        }

        loadProperties();

        for (Profile p:profiles) {
            for (Save s:p.getSaves()) {
                if (s.equals(save)) {

                    File oldSaveName = s.getSaveFile();
                    File f = s.getSaveFile();
                    File f2 = s.getBackupSaveFile();
                    s.setName(saveName);

                    File saveFolder = new File(f.getParentFile().getParent() + File.separator + saveName);

                    FileUtils.moveFileToDirectory(f, saveFolder, true);
                    if (f2.exists()) {
                        FileUtils.moveFileToDirectory(f2, saveFolder, true);
                    }
                    FileUtils.deleteDirectory(oldSaveName.getParentFile());

                    s.setSaveFile(new File(saveFolder + File.separator + f.getName()));
                    s.setBackupSaveFile(new File(saveFolder + File.separator + f2.getName()));

                    savesProfilesToXMLFile(profiles);
                    closeStage(saveNameTextField.getScene().getWindow());

                    return;
                }
            }
        }

    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public void setSave(Save save) {
        this.save = save;
    }

}
