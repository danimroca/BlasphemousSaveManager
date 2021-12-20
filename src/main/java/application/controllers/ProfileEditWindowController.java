package application.controllers;

import application.model.Profile;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.List;

public class ProfileEditWindowController extends BaseController {

    @FXML
    public TextField profileNameTextField;

    private Profile profile;
    private List<Profile> profiles;

    public void cancel() {
        closeStage(profileNameTextField.getScene().getWindow());
    }

    public void accept() {
        loadProperties();
        String profileName = profileNameTextField.getText().trim();

        if (profiles.contains(profile)) {
            for (Profile p:profiles) {
                if (p.equals(profile)) {
                    p.setName(profileName);
                }
            }
        }
        savesProfilesToXMLFile(profiles);
        closeStage(profileNameTextField.getScene().getWindow());
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

}
