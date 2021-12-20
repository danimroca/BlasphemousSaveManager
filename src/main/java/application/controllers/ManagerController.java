package application.controllers;

import application.model.Profile;
import application.model.Profiles;
import application.model.Save;
import application.utils.SaveComparatorByDate;
import application.utils.SaveComparatorByName;
import javafx.collections.FXCollections;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerController extends BaseController implements Initializable {

	private Profile profile;
	private List<Profile> profiles;

	@FXML
	private ChoiceBox<Profile> profileChoiceBox;
	@FXML
	private ListView<Save> saveListView;
	@FXML
	private ChoiceBox<Integer> saveSlotChoiceBox;
	@FXML
	private ChoiceBox<String> orderChoiceBox;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		loadProperties();
		loadProfiles();
		loadOrderChoiceBox();
		loadProfileChoiceBox();
		loadSaves();

	}

	private void loadSaves() {
		if (profile != null) {
			saveSlotChoiceBox.setItems(FXCollections.observableArrayList(1,2,3));
			saveSlotChoiceBox.getSelectionModel().select(profile.getSaveSlot()-1);
			loadSaveListView();
		}
	}

	private void loadProfileChoiceBox() {
		profileChoiceBox.getItems().clear();
		profileChoiceBox.setItems(FXCollections.observableArrayList(profiles));
		if (profile != null) {
			profileChoiceBox.getSelectionModel().select(profile);
		}
	}

	private void loadOrderChoiceBox() {
		orderChoiceBox.getItems().clear();
		orderChoiceBox.setItems(FXCollections.observableArrayList("Created","Name"));
		orderChoiceBox.setValue("Created");
	}

	public void orderSaves() {
		if (profile == null || profile.getSaves().isEmpty()) {
			return;
		}
		switch (orderChoiceBox.getValue()) {
			case "Created" :
				profile.getSaves().sort(new SaveComparatorByDate());
				break;
			case "Name" :
				profile.getSaves().sort(new SaveComparatorByName());
				break;
			default:
				break;
		}
		updateProfilesList();
		loadSaveListView();
		savesProfilesToXMLFile(profiles);
	}

	private void loadProfiles() {
		if (getProfilePath() != null && !getProfilePath().isEmpty() && new File(getProfilePath()).exists()) {
			//we load the profiles and then load the saves
			Unmarshaller jaxbUnmarshaller;
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
		if (profile == null && !profiles.isEmpty()) {
			profile = profiles.stream().filter(Profile::isActive).findFirst().orElse(profiles.isEmpty() ? null : profiles.get(0));
			if (profile != null) {
				profile.setActive(true);
			}
		}
	}

	public void openProfileWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileWindow.fxml"));
			Scene scene = new Scene(loader.load(), 550, 300);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Profile Configuration");
			stage.setResizable(false);
			ProfileController profileController = loader.getController();
			profileController.setProfiles(profiles);
			if (!properties.entrySet().isEmpty()) {
				profileController.loadProfilesToGUI();
			}
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
		if (!profiles.contains(profile)) {
			profile = null;
		}
		loadProfiles();
		loadProfileChoiceBox();
	}

	private void closeSaveNameChangeWindowEvent(WindowEvent event) {
		loadProfiles();
		loadSaveListView();
	}

	public void selectProfile() {
		if (profileChoiceBox.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		Profile auxProf = null;
		for (Profile prof1 : profiles) {
			if (prof1.getName().equals(profileChoiceBox.getSelectionModel().getSelectedItem().toString())) {
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
		saveSlotChoiceBox.getSelectionModel().select(profile.getSaveSlot()-1);
		updateProfilesList();
		savesProfilesToXMLFile(profiles);
		loadSaveListView();
	}

	public void selectSaveSlot() {
		if (profile == null) {
			return;
		}
		profile.setSaveSlot(saveSlotChoiceBox.getSelectionModel().getSelectedItem() == null ? 0 : saveSlotChoiceBox.getSelectionModel().getSelectedItem());
		updateProfilesList();
		savesProfilesToXMLFile(profiles);
	}

	private void loadSaveListView() {
		saveListView.getItems().clear();
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

		saveListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (saveListMenuItem.isShowing()) {
				if (event.getButton().equals(MouseButton.PRIMARY) || event.getButton().equals(MouseButton.SECONDARY)) {
					saveListMenuItem.hide();
				}
			} else if (event.getButton().equals(MouseButton.SECONDARY) || (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)) {
				saveListMenuItem.show(saveListView, event.getScreenX(), event.getScreenY());
			}

		});
	}

	public void replaceSave() throws IOException {
		if (profile == null) {
			return;
		}
		Save save = saveListView.getSelectionModel().getSelectedItem();
		if (save == null) {
			return;
		}

		Label label = new Label("Are you sure you want to replace the '" + save.getName() + "' save?");
		label.setWrapText(true);

		Alert alert = new Alert(Alert.AlertType.NONE);
		alert.setTitle("Replace save");
		alert.getDialogPane().setContent(label);
		alert.getButtonTypes().add(ButtonType.YES);
		alert.getButtonTypes().add(ButtonType.NO);
		alert.setResizable(false);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.NO) {
			return;
		}

		int saveSlotNumber = Integer.parseInt(saveSlotChoiceBox.getSelectionModel().getSelectedItem().toString())-1;

		String saveFile = getSavePath() + File.separator + "savegame_" + saveSlotNumber + ".save";
		String saveBackupFile = getSavePath()  + File.separator +  "savegame_" + saveSlotNumber + ".save_backup";

		File f = new File(saveFile);
		File f2 = new File(saveBackupFile);

		if (!f.exists()) {
			Alert alert2 = new Alert(Alert.AlertType.WARNING,"There's no save files in that save slot.");
			alert2.showAndWait();
			return;
		}

		Files.deleteIfExists(save.getSaveFile().toPath());
		Files.deleteIfExists(save.getBackupSaveFile().toPath());

		try {
			FileUtils.copyFile(f, save.getSaveFile());
			if (f2.exists()) {
				FileUtils.copyFile(f2, save.getBackupSaveFile());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteSave() throws IOException {
		if (profile == null) {
			return;
		}
		Save save = saveListView.getSelectionModel().getSelectedItem();
		if (save == null) {
			return;
		}
		Label label = new Label("Are you sure you want to delete the '" + save.getName() + "' save?");
		label.setWrapText(true);

		Alert alert = new Alert(Alert.AlertType.NONE);
		alert.setTitle("Delete save");
		alert.getDialogPane().setContent(label);
		alert.getButtonTypes().add(ButtonType.YES);
		alert.getButtonTypes().add(ButtonType.NO);
		alert.setResizable(false);
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
		if (profile == null) {
			return;
		}
		int saveSlotNumber = Integer.parseInt(saveSlotChoiceBox.getSelectionModel().getSelectedItem().toString())-1;

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
			if (f2.exists()) {
				FileUtils.copyToDirectory(f2, saveGameFolder);
			}
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
		if (profile == null) {
			return;
		}
		Save save = saveListView.getSelectionModel().getSelectedItem();
		if (save == null) {
			return;
		}

		int saveSlotNumber = Integer.parseInt(saveSlotChoiceBox.getSelectionModel().getSelectedItem().toString())-1;

		String saveFileName = "savegame_" + saveSlotNumber + ".save";
		String saveBackupFileName = "savegame_" + saveSlotNumber + ".save_backup";

		FileUtils.copyFile(save.getSaveFile(), new File(getSavePath() + File.separator + saveFileName));
		if (save.getBackupSaveFile().exists()) {
			FileUtils.copyFile(save.getBackupSaveFile(), new File(getSavePath() + File.separator + saveBackupFileName));
		}
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/SaveChangeName.fxml"));
			Scene scene = new Scene(loader.load(), 450, 125);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setResizable(false);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Save rename");
			SaveChangeNameController saveChangeNameController = loader.getController();
			saveChangeNameController.setProfiles(profiles);
			saveChangeNameController.setSave(saveListView.getSelectionModel().getSelectedItem());
			stage.show();
			scene.getWindow().addEventFilter(WindowEvent.WINDOW_HIDING, this::closeSaveNameChangeWindowEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
