package application.controllers;

import application.model.Profile;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProfileController extends BaseController {

	private List<Profile> profiles;
	private Profile profile;

	@FXML
	private ListView<Profile> profileList;
	@FXML
	private TextField saveLocationInput;
	@FXML
	private TextField profileLocationInput;

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
		File saveLocation = directoryChooser.showDialog(getStage(saveLocationInput.getScene().getWindow()));

		if (saveLocation == null) {
			return;
		}

		if (saveLocation.exists()) {
			saveLocationInput.setText(saveLocation.getAbsolutePath());
			updateSavePath(saveLocation.getAbsolutePath());
		}

	}

	public void openProfileDirectoryChooser() throws IOException {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Profiles location");
		File profileLocation = directoryChooser.showDialog(getStage(profileLocationInput.getScene().getWindow()));

		if (profileLocation == null) {
			return;
		}

		if (profileLocation.exists()) {
			profileLocationInput.setText(profileLocation.getAbsolutePath());
			updateProfilePath(profileLocation.getAbsolutePath());
		}
	}

	public void newProfile() {
		profile = new Profile();
		profile.setName("");
		profiles.add(profile);
		openProfileEditWindow();
	}

	public void editProfile() {
		profile = profileList.getSelectionModel().getSelectedItem();
		if (profile == null) {
			return;
		}
		openProfileEditWindow();
	}

	public void deleteProfile() {
		profile = profileList.getSelectionModel().getSelectedItem();
		if (profile == null) {
			return;
		}
		profiles.remove(profile);
		profile = null;

		loadProfilesToGUI();
		savesProfilesToXMLFile(profiles);
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

	public void openProfileEditWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileEditWindow.fxml"));
			Scene scene = new Scene(loader.load(), 600, 400);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Profile");
			ProfileEditWindowController profileEditWindowController = loader.getController();
			profileEditWindowController.setProfiles(profiles);
			profileEditWindowController.setProfile(profile);
			stage.show();
			scene.getWindow().addEventFilter(WindowEvent.WINDOW_HIDING, this::closeProfileEditWindowEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeProfileEditWindowEvent(WindowEvent event) {
		loadProfilesToGUI();
	}

}
