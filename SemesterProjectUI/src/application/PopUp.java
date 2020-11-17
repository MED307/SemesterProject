package application;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;



public class PopUp {
	
	private Campaign currentCampaign;

	public ArrayList<String> displayCampaign(String title, String FXML) throws IOException
	{
		
		//creates a list to store campaign information
		ArrayList<String> campaignInformation = new ArrayList<>();
		
		//creates a new stage
		Stage window = new Stage();
		
		//loads new FXMLLoader for the stage
		FXMLLoader loader = new FXMLLoader();
		
		//
		AnchorPane root = (AnchorPane)loader.load();
		
		
		//create new scene
		Scene popUp = new Scene(root);
		
		//
		popUp.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		
		//prevents the user from resizing
		window.setResizable(false);
		
		
		//
		window.setScene(popUp);
		
		//sets the title of the stage (window)
		window.setTitle(title);
		
		//
		window.setMinWidth(250);
		
		//
		window.showAndWait();		
		
		
		//
		Controller controller = loader.getController();
		
		//
		controller.setCurrentCampaign(getCurrentCampaign());
		
		//returns campaignInformation
		return campaignInformation;
	}
	
	//get
	public Campaign getCurrentCampaign() {
		return currentCampaign;
		
	}
	//set
	public void setCampaign(Campaign currentCampaign) {
		this.currentCampaign = currentCampaign;

	
	}
}
