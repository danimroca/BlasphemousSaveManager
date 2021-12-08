module BlasphemousSaveMaganer {

	requires javafx.controls;
	requires javafx.fxml;
	requires java.xml;
	requires java.xml.bind;

	opens application to javafx.graphics, javafx.fxml;
	opens application.controllers to javafx.graphics, javafx.fxml;
	opens application.model to java.xml.bind;

	exports application.controllers to javafx.fxml;
	exports application.model to javafx.fxml;
    
}
