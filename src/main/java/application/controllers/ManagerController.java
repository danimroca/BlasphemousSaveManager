package application.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ManagerController {

	private static final String PROPERTIES_PATH = "Blasphemous Save Manager";
	private static final String PROPERTIES_FILENAME = "savemanager.properties";
	private Properties properties;

	public void abrirVentanaPerfil() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/ProfileWindow.fxml"));
			Scene scene = new Scene(root, 600, 400);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
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

}
