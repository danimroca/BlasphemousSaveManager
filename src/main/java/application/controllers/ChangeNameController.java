package application.controllers;

import application.model.Profile;
import application.model.Save;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.List;

public class ChangeNameController extends BaseController {

    @FXML
    private TextField saveNameTextField;
    private List<Profile> profiles;
    private Save save;

    public void cancel() {
        closeCurrentStage();
    }

    public void accept() {
        loadProperties();
        String saveName = saveNameTextField.getText();
        for (Profile profile:profiles) {
            for (Save s:profile.getSaves()) {
                if (s.equals(save)) {
                    s.setName(saveName);
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
