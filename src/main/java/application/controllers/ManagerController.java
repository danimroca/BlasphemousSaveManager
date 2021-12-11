package application.controllers;

import application.model.Profile;
import application.model.Profiles;
import application.model.Save;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerController extends BaseController implements Initializable {

	private Profile profile;
	private List<Profile> profiles;
	private List<Save> saves;
	private Save selectedSave;
	private String savePath;

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

		saveSlotChoiceBox.setItems(FXCollections.observableArrayList("1","2","3"));

		if (profile != null) {
			profileChoiceBox.setValue(profile.getName());
			saveSlotChoiceBox.setValue(profile.getSaveSlot()+1);
			loadSaveListView();
		} else {
			saveSlotChoiceBox.setValue(1);
		}

	}

	public void loadProfileChoiceBox() {
		profileChoiceBox.getItems().clear();
		profileChoiceBox.setItems(FXCollections.observableArrayList(profiles));
		if (profiles.isEmpty()) {
			profile = null;
		} else {
			profile = profiles.get(0);
			profileChoiceBox.setValue(profile.getName());
		}
	}

	public void loadProfiles() {
		if (getProfilePath() != null && !getProfilePath().isEmpty()) {
			//we load the profiles and then load the saves
			Unmarshaller jaxbUnmarshaller = null;
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(Profiles.class);
				jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				profiles = ((Profiles) jaxbUnmarshaller.unmarshal(new File(getProfilesFilePath()))).getProfiles();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			profiles = new ArrayList<>();
		}
		if (!profiles.isEmpty()) {
			profile = profiles.stream().filter(Profile::isActive).findFirst().get();
			saves = profile.getSaves();
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

	public void selectProfile() {
		Profile auxProf = profiles.stream().filter(prof -> prof.getName().equals(profileChoiceBox.getValue())).findFirst().orElse(null);
		if (auxProf == null) {
			return;
		}
		if (profile == null) {
			return;
		}
		profile.setActive(false);
		profile = auxProf;
		profile.setActive(true);
		loadSaveListView();
	}

	public void loadSaveListView() {
		saveListView.setItems(FXCollections.observableArrayList(saves));
	}

	public void importSave() {
		int saveSlotNumber = ((int) saveSlotChoiceBox.getValue()) -1;

		String saveFile = getSavePath() + File.separator + "savegame_" + saveSlotNumber + ".save";
		String saveBackupFile = getSavePath()  + File.separator +  "savegame_" + saveSlotNumber + ".save_backup";

		File f = new File(saveFile);
		File f2 = new File(saveBackupFile);

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
		save.setSaveFile(f);
		save.setBackupSaveFile(f2);
		save.setOrder(saves.size()-1);

		saves.add(save);
		loadSaveListView();
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

}
