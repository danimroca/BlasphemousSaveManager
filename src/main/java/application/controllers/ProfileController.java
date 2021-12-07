package application.controllers;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import application.model.Profile;
import application.model.Profiles;
import javafx.fxml.Initializable;

public class ProfileController implements Initializable {
	
	private Profile profile;
	private List<Profile> profiles;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Unmarshaller jaxbUnmarshaller = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Profiles.class);
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			profiles = ((Profiles) jaxbUnmarshaller.unmarshal(new File(getClass().getResource("/test.xml").toURI()))).getProfiles();
		} catch (Exception e) {
			e.printStackTrace();
		}

		profile = profiles.stream().filter(Profile::isActive).findFirst().get();

		if (!profiles.isEmpty()) {
			loadProfilesToGUI();
		}
	}

	public void loadProfilesToGUI() {
		
		
		
	}

}
