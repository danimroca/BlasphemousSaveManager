package application.controllers;

import application.model.Profile;
import application.model.Save;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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

        Label label = new Label("Are you sure you want to rename the save?");
        label.setWrapText(true);

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Rename save");
        alert.getDialogPane().setContent(label);
        alert.getButtonTypes().add(ButtonType.YES);
        alert.getButtonTypes().add(ButtonType.NO);
        alert.setResizable(false);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.NO) {
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

                    File saveFolder = generateFolderName(f, saveName);

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

    private File generateFolderName(File f, String saveName) {
        String saveGameFolderName = f.getParentFile().getParent() + File.separator + saveName + "-" + UUID.randomUUID();
        if (new File(saveGameFolderName).exists()) {
            return generateFolderName(f, saveName);
        } else {
            return new File(saveGameFolderName);
        }
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public void setSave(Save save) {
        this.save = save;
    }

}
