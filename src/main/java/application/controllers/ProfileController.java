package application.controllers;

import application.model.Profile;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileController {
	
	private List<Profile> profiles;
	private Profile profile;
	private Stage mainStage;
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
		saveLocation = directoryChooser.showDialog(mainStage);

		if (saveLocation != null && saveLocation.exists()) {
			saveLocationInput.setText(saveLocation.getAbsolutePath());
		}
	}

	public void openProfileDirectoryChooser() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Profiles location");
		profileLocation = directoryChooser.showDialog(mainStage);

		if (profileLocation != null && profileLocation.exists()) {
			profileLocationInput.setText(profileLocation.getAbsolutePath());
		}
	}

	public void openProfileNameDialog() {
		try {
			editProfileBox.setVisible(true);
			editProfileBox.setManaged(true);
			profileListPane.setVisible(false);
			profileListPane.setManaged(false);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfileDialog.fxml"));
			Scene scene = new Scene(loader.load(), 400, 300);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			dialogNameStage = stage;
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveProfileName() {
		if (profile == null) {
			profile = new Profile();
			profile.setActive(true);
			profile.setSaves(new ArrayList<>());
			profiles = new ArrayList<>();
		}
		profile.setName(profileNameInput.getText());
		profiles.add(profile);
		updateProfiles();
		loadProfilesToGUI();
		closeCurrentWindow();
	}

	public void updateProfiles() {
		int index = profiles.indexOf(profile);
		profiles.set(index, profile);
	}

	public void setMainStage(Stage mainStage) {
		this.mainStage = mainStage;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public void closeCurrentWindow() {
		Stage stage = (Stage) Stage.getWindows().stream().filter(Window::isFocused).findFirst().get();
		stage.close();
	}

}
