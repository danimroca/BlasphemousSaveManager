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
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileController {

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
		profileList.setItems(FXCollections.observableArrayList(profiles));
	}

	public void openSaveDirectoryChooser() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Save file location");
		saveLocation = directoryChooser.showDialog(getCurrentStage());

		if (saveLocation != null && saveLocation.exists()) {
			saveLocationInput.setText(saveLocation.getAbsolutePath());
		}
	}

	public void openProfileDirectoryChooser() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Profiles location");
		profileLocation = directoryChooser.showDialog(getCurrentStage());

		if (profileLocation != null && profileLocation.exists()) {
			profileLocationInput.setText(profileLocation.getAbsolutePath());
		}
	}

	public void newProfile() {
		profile = new Profile();
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
		profile.setActive(true);
		profile.setSaves(new ArrayList<>());
		profile.setName(profileNameInput.getText());

		if (profiles.contains(profile)) {
			updateProfiles();
		} else {
			profiles.add(profile);
		}
		loadProfilesToGUI();
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

	public void closeCurrentStage() {
		((Stage) Stage.getWindows().stream().filter(Window::isFocused).findFirst().get()).close();
	}

	public Stage getCurrentStage() {
		return (Stage) Stage.getWindows().stream().filter(Window::isFocused).findFirst().get();
	}

}
