package application.controllers;

import application.model.Profile;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProfileController extends BaseController {

	private List<Profile> profiles;
	private Profile profile;
	private File profileLocation;
	private File saveLocation;
	private Stage dialogNameStage;

	@FXML
	private ListView<Profile> profileList;
	@FXML
	private Button browseProfileButton;
	@FXML
	private TextField saveLocationInput;
	@FXML
	private TextField profileLocationInput;
	@FXML
	private TextField profileNameInput;
	@FXML
	private VBox editProfileBox;
	@FXML
	private Pane profileListPane;

	public void loadProfilesToGUI() {
		loadProperties();
		profileList.getItems().clear();
		profileList.setItems(FXCollections.observableArrayList(profiles));
		if (!getProfilePath().isEmpty()) {
			profileLocationInput.setText(getProfilePath());
		}
		if (!getSavePath().isEmpty()) {
			saveLocationInput.setText(getSavePath());
		}
	}

	public void openSaveDirectoryChooser() throws IOException {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Save file location");
		saveLocation = directoryChooser.showDialog(getCurrentStage(saveLocationInput.getScene().getWindow()));

		if (saveLocation != null && saveLocation.exists()) {
			saveLocationInput.setText(saveLocation.getAbsolutePath());
		}

		updateSavePath(saveLocation.getAbsolutePath());
	}

	public void openProfileDirectoryChooser() throws IOException {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Profiles location");
		profileLocation = directoryChooser.showDialog(getCurrentStage(profileLocationInput.getScene().getWindow()));

		if (profileLocation != null && profileLocation.exists()) {
			profileLocationInput.setText(profileLocation.getAbsolutePath());
		}

		updateProfilePath(profileLocation.getAbsolutePath());
	}

	public void newProfile() {
		profile = new Profile();
		profile.setName("change my name");
		profileNameInput.setText("");
		openProfileNameDialog();
	}

	public void editProfile() {
		profile = profileList.getSelectionModel().getSelectedItem();
		if (profile == null) {
			return;
		}
		profileNameInput.setText(profile.getName());
		openProfileNameDialog();
	}

	public void deleteProfile() {
		profile = profileList.getSelectionModel().getSelectedItem();
		if (profile == null) {
			return;
		}
		profileNameInput.setText(profile.getName());
		profiles.remove(profile);
		if (profiles.isEmpty()) {
			profile = null;
		} else {
			profile = profiles.get(0);
		}
		loadProfilesToGUI();
		savesProfilesToXMLFile(profiles);
	}

	public void openProfileNameDialog() {
		editProfileBox.setVisible(true);
		editProfileBox.setManaged(true);
		profileListPane.setVisible(false);
		profileListPane.setManaged(false);
	}

	public void closeProfileNameMenu() {
		editProfileBox.setVisible(false);
		editProfileBox.setManaged(false);
		profileListPane.setVisible(true);
		profileListPane.setManaged(true);
	}

	public void saveProfile() {
		profile.setName(profileNameInput.getText());
		if (profiles.contains(profile)) {
			updateProfiles();
		} else {
			profiles.add(profile);
		}
		loadProfilesToGUI();
		savesProfilesToXMLFile(profiles);
		closeProfileNameMenu();
	}

	public void updateProfiles() {
		profiles.remove(profile);
		profiles.add(profile);
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

}
