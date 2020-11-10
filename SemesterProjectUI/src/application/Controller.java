package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Controller {
	
	private Campaign CurrentCampaign;

	public void changeScene(ActionEvent event, String FXML) throws IOException 
	{
		//creates the FXMLLoader used to load the next scene
				FXMLLoader loader = new FXMLLoader();
				
				//Sets the location of where the FXML file is
				loader.setLocation(getClass().getResource(FXML));
				
				//loads the fXML file
				AnchorPane chatRoot = (AnchorPane)loader.load();
				
				//creates a scene from the FXML file
				Scene chat = new Scene(chatRoot);
				
				//add the Stylesheet to the scene
				chat.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				
				//gets the current stage
				Stage window= (Stage)((Node)event.getSource()).getScene().getWindow();
				
				
				//sets the scene of that stage
				window.setScene(chat);
				
				//gets the controller of the FXML file
				Controller controller = (Controller) loader.getController();
				
				controller.setCurrentCampaign(getCurrentCampaign());
	}
	
	public Campaign getCurrentCampaign() {
		return CurrentCampaign;
	}

	public void setCurrentCampaign(Campaign currentCampaign) {
		CurrentCampaign = currentCampaign;
	}
}
