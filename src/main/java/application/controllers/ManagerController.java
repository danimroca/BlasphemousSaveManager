package application.controllers;

import application.model.Profile;
import application.model.Profiles;
import application.model.Save;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerController extends BaseController implements Initializable {

	private Profile profile;
	private List<Profile> profiles;

	@FXML
	private ChoiceBox profileChoiceBox;
	@FXML
	private ListView saveListView;
	@FXML
	private ChoiceBox saveSlotChoiceBox;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		loadProperties();
		loadProfiles();
		loadProfileChoiceBox();
		saveSlotChoiceBox.setValue(1);
		saveSlotChoiceBox.setItems(FXCollections.observableArrayList("1","2","3"));

		if (profile != null) {
			profileChoiceBox.setValue(profile.getName());
			saveSlotChoiceBox.setValue(profile.getSaveSlot());
			loadSaveListView();
		}
	}

	private void loadProfileChoiceBox() {
		profileChoiceBox.getItems().clear();
		profileChoiceBox.setItems(FXCollections.observableArrayList(profiles));
		if (profiles.isEmpty()) {
			profile = null;
		} else {
			profile = profiles.get(0);
			profileChoiceBox.setValue(profile.getName());
		}
	}

	private void loadProfiles() {
		if (getProfilePath() != null && !getProfilePath().isEmpty()) {
			//we load the profiles and then load the saves
			Unmarshaller jaxbUnmarshaller = null;
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(Profiles.class);
				jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				profiles = ((Profiles) jaxbUnmarshaller.unmarshal(new File(getProfilesFilePath()))).getProfiles();
			} catch (Exception e) {
				if (!(e instanceof IllegalArgumentException)) {
					e.printStackTrace();
				}
			}
		}
		if (profiles == null) {
			profiles = new ArrayList<>();
		}
		if (!profiles.isEmpty()) {
			profile = profiles.stream().filter(Profile::isActive).findFirst().get();
		}
	}

	public void openProfileWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileWindow.fxml"));
			Scene scene = new Scene(loader.load(), 600, 400);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Profile Configuration");
			ProfileController profileController = loader.getController();
			profileController.setProfiles(profiles);
			profileController.loadProfilesToGUI();
			stage.show();
			scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeProfileWindowEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Event launched when the profile window gets a close request
	 * this will load the profiles from the xml profile file
	 * and load them into the UI
	 *
	 * @param event
	 */
	private void closeProfileWindowEvent(WindowEvent event) {
		loadProfiles();
		loadProfileChoiceBox();
	}

	private void closeSaveNameChangeWindowEvent(WindowEvent event) {
		loadProfiles();
		loadSaveListView();
	}

	public void selectProfile() {
		Profile auxProf = null;
		for (Profile prof1 : profiles) {
			if (prof1.getName().equals(profileChoiceBox.getValue().toString())) {
				auxProf = prof1;
			}
		}
		if (auxProf == null) {
			return;
		}
		if (profile == null) {
			return;
		}
		profile = auxProf;
		profiles.forEach(Profile::deactivateProfile);
		profile.setActive(true);
		updateProfilesList();
		savesProfilesToXMLFile(profiles);
		loadSaveListView();
	}

	public void selectSaveSlot() {
		if (profile == null) {
			return;
		}
		int saveSlot = Integer.parseInt(saveSlotChoiceBox.getValue().toString());
		profile.setSaveSlot(saveSlot);
		updateProfilesList();
		savesProfilesToXMLFile(profiles);
	}

	private void loadSaveListView() {
		saveListView.setItems(FXCollections.observableArrayList(profile.getSaves()));
		final ContextMenu saveListMenuItem = new ContextMenu();
		MenuItem renameMenuItem = new MenuItem("Rename");
		renameMenuItem.setOnAction(event -> openSaveNameChangeDialog());
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(event -> {
			try {
				deleteSave();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		saveListMenuItem.getItems().add(renameMenuItem);
		saveListMenuItem.getItems().add(deleteMenuItem);

		saveListView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (saveListMenuItem.isShowing()) {
					if (event.getButton().equals(MouseButton.PRIMARY) || event.getButton().equals(MouseButton.SECONDARY)) {
						saveListMenuItem.hide();
					}
				} else if (event.getButton().equals(MouseButton.SECONDARY) || (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)) {
					saveListMenuItem.show(saveListView, event.getScreenX(), event.getScreenY());
				}

			}
		});
	}

	public void replaceSave() throws IOException {
		Save save = (Save) saveListView.getSelectionModel().getSelectedItem();
		if (save == null) {
			return;
		}

		int saveSlotNumber = Integer.parseInt(saveSlotChoiceBox.getValue().toString())-1;

		String saveFile = getSavePath() + File.separator + "savegame_" + saveSlotNumber + ".save";
		String saveBackupFile = getSavePath()  + File.separator +  "savegame_" + saveSlotNumber + ".save_backup";

		File f = new File(saveFile);
		File f2 = new File(saveBackupFile);

		if (!f.exists()) {
			Alert alert = new Alert(Alert.AlertType.WARNING,"There's no save files in that save slot.");
			alert.showAndWait();
			return;
		}

		Path saveFilePath = save.getSaveFile().toPath();
		Path backupSaveFilePath = save.getBackupSaveFile().toPath();

		Files.deleteIfExists(saveFilePath);
		Files.deleteIfExists(backupSaveFilePath);

		try {
			FileUtils.copyFile(f, saveFilePath.toFile());
			FileUtils.copyFile(f2, backupSaveFilePath.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteSave() throws IOException {
		Save save = (Save) saveListView.getSelectionModel().getSelectedItem();
		if (save == null) {
			return;
		}
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the save?", ButtonType.YES, ButtonType.NO);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.NO) {
			return;
		}
		profile.removeSave(save);

		FileUtils.deleteDirectory(save.getSaveFile().getParentFile());

		updateProfilesList();
		loadSaveListView();
		savesProfilesToXMLFile(profiles);
	}

	public void importSave() {
		int saveSlotNumber = Integer.parseInt(saveSlotChoiceBox.getValue().toString())-1;

		String saveFileName = "savegame_" + saveSlotNumber + ".save";
		String saveBackupFileName = "savegame_" + saveSlotNumber + ".save_backup";

		String saveFile = getSavePath() + File.separator + "savegame_" + saveSlotNumber + ".save";
		String saveBackupFile = getSavePath()  + File.separator +  "savegame_" + saveSlotNumber + ".save_backup";

		File f = new File(saveFile);
		File f2 = new File(saveBackupFile);

		if (!f.exists()) {
			Alert alert = new Alert(Alert.AlertType.WARNING,"There's no save files in that save slot.");
			alert.showAndWait();
			return;
		}

		File saveFolder = new File(getSavePath() + File.separator + "saves");
		if (!saveFolder.exists()) {
			saveFolder.mkdir();
		}

		File saveGameFolder = generateSaveGameFolder(0);
		saveGameFolder.mkdir();

		try {
			FileUtils.copyToDirectory(f, saveGameFolder);
			FileUtils.copyToDirectory(f2, saveGameFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Save save = new Save();
		save.setName(saveGameFolder.getName());
		save.setSaveFile(new File(saveGameFolder + File.separator + saveFileName));
		save.setBackupSaveFile(new File(saveGameFolder + File.separator + saveBackupFileName));
		save.setOrder(profile.getSaves().size()-1);

		profile.addSave(save);
		updateProfilesList();
		loadSaveListView();
		savesProfilesToXMLFile(profiles);
	}

	public void loadSave() throws IOException {
		Save save = (Save) saveListView.getSelectionModel().getSelectedItem();
		if (save == null) {
			return;
		}

		int saveSlotNumber = Integer.parseInt(saveSlotChoiceBox.getValue().toString())-1;

		String saveFileName = "savegame_" + saveSlotNumber + ".save";
		String saveBackupFileName = "savegame_" + saveSlotNumber + ".save_backup";

		FileUtils.copyFile(save.getSaveFile(), new File(getSavePath() + File.separator + saveFileName));
		FileUtils.copyFile(save.getBackupSaveFile(), new File(getSavePath() + File.separator + saveBackupFileName));
	}

	private File generateSaveGameFolder(int i) {
		String saveGameFolderName = getSavePath() + File.separator + "saves" + File.separator + "savegame" + i;
		if (new File(saveGameFolderName).exists()) {
			i++;
			return generateSaveGameFolder(i);
		} else {
			return new File(saveGameFolderName);
		}
	}

	private void updateProfilesList() {
		profiles.remove(profile);
		profiles.add(profile);
	}

	public void openSaveNameChangeDialog() {
		if (saveListView.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChangeName.fxml"));
			Scene scene = new Scene(loader.load(), 600, 400);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Save change name");
			ChangeNameController changeNameController = loader.getController();
			changeNameController.setProfiles(profiles);
			changeNameController.setSave((Save) saveListView.getSelectionModel().getSelectedItem());
			stage.show();
			scene.getWindow().addEventFilter(WindowEvent.WINDOW_HIDING, this::closeSaveNameChangeWindowEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
