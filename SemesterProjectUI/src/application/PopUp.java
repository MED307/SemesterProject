package application;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;



public class PopUp {
	
	private Campaign currentCampaign;

	public void displayCampaign(String title, String FXML) throws IOException
	{
		
		Stage window = new Stage();
		
		FXMLLoader loader = new FXMLLoader();
		
		AnchorPane root = (AnchorPane)loader.load();
		
		Scene popUp = new Scene(root);
		
		popUp.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		window.setResizable(false);
		
		window.setScene(popUp);
		
		window.setTitle(title);
		
		window.setMinWidth(250);
		
		window.showAndWait();		
		
		
	}
	
	//get
	public Campaign getcurrentCampaign() {
		return currentCampaign;
		
	}
	//set
	public void setCampaign(Campaign currentCampaign) {
		this.currentCampaign = currentCampaign;

	
	}
}
