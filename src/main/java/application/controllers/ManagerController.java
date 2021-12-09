package application.controllers;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import application.model.Profile;
import application.model.Profiles;
import application.model.Save;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class ManagerController implements Initializable {

	private static final String PROPERTIES_PATH = "Blasphemous Save Manager";
	private static final String PROPERTIES_FILENAME = "savemanager.properties";
	private Properties properties;
	private Profile profile;
	private List<Profile> profiles;
	private List<Save> saves;
	private Save selectedSave;

	@FXML
	private ChoiceBox profileChoiceBox;
	@FXML
	private ListView saveListView;


	@Override
	public void initialize(URL location, ResourceBundle resources) {//Inicia el controlador y obtiene el Profile

		Unmarshaller jaxbUnmarshaller = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Profiles.class); 
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			profiles = ((Profiles) jaxbUnmarshaller.unmarshal(new File(getClass().getResource("/test.xml").toURI()))).getProfiles();

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (profiles.isEmpty()) {
			profiles = new ArrayList<>();
			profile = new Profile();
			profiles.add(profile);
		} else {
			profile = profiles.stream().filter(Profile::isActive).findFirst().get();
		}
		saves = profile.getSaves();
		profileChoiceBox.setItems(FXCollections.observableArrayList(profiles));
		profileChoiceBox.setValue(profile.getName());
		loadSaveListView();
	}

	public void openProfileWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileWindow.fxml"));
			Scene scene = new Scene(loader.load(), 600, 400);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			ProfileController profileController = loader.getController();
			profileController.setProfile(profile);
			profileController.setProfiles(profiles);
			profileController.loadProfilesToGUI();
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void loadProperties() {
		String userProfilePath = System.getenv("%UserProfile%");
		try {
			properties.load(new FileInputStream(
					userProfilePath + File.separator + PROPERTIES_PATH + File.separator + PROPERTIES_FILENAME));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			File file = new File(
					userProfilePath + File.separator + PROPERTIES_PATH + File.separator + PROPERTIES_FILENAME);
		}
	}

	public void selectProfile() {
		profile = profiles.stream().filter(prof -> prof.getName().equals(profileChoiceBox.getValue())).findFirst().get();

		loadSaveListView();
	}

	public void loadSaveListView() {
		saveListView.setItems(FXCollections.observableArrayList(saves));
		if (!saves.isEmpty()) {
			selectedSave = saves.get(0);
		}
	}

	public void importSave() {

	}

}
